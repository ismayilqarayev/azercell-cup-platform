package az.azcup.backend.service;

import az.azcup.backend.dto.ExampleCheckResponse;
import az.azcup.backend.dto.ExampleSummaryDto;
import az.azcup.backend.dto.StudentAssignmentDto;
import az.azcup.backend.dto.StudentExampleDto;
import az.azcup.backend.dto.teacher.AssignmentDto;
import az.azcup.backend.dto.teacher.AssignmentUpsertRequest;
import az.azcup.backend.dto.teacher.ExampleDto;
import az.azcup.backend.dto.teacher.ExampleProgressRowDto;
import az.azcup.backend.dto.teacher.ExampleUpsertRequest;
import az.azcup.backend.dto.teacher.GradebookRowDto;
import az.azcup.backend.entity.Assignment;
import az.azcup.backend.entity.AssignmentExample;
import az.azcup.backend.entity.ExampleCompletion;
import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.GroupMember;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ForbiddenException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.AssignmentExampleRepository;
import az.azcup.backend.repository.AssignmentRepository;
import az.azcup.backend.repository.ExampleCompletionRepository;
import az.azcup.backend.repository.GroupMemberRepository;
import az.azcup.backend.repository.GroupRepository;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Müəllimin öz qrupuna tapşırıq (Assignment) yaratmasını/idarə etməsini və
// sinif jurnalını (Gradebook) görməsini, həmçinin şagirdin öz tapşırıqlarını
// görməsini təmin edir. Bir tapşırıq bütövlükdə bir MÖVZUya aiddir (konkret
// problem seçimi yoxdur) — "neçəsi həll edilib" hesabı mövcud
// ProblemRepository/SubmissionRepository metodları ilə aparılır ki, ayrıca
// "hansı problem həll edilib" cədvəlinə ehtiyac qalmasın.
@Service
public class AssignmentService {

    // Tapşırıq sətirlərini oxumaq/yazmaq üçün.
    private final AssignmentRepository assignmentRepository;
    // Tapşırığın aid olduğu qrupu tapmaq və sahibliyini yoxlamaq üçün.
    private final GroupRepository groupRepository;
    // Qrupun üzvlərini (gradebook üçün) və şagirdin üzvü olduğu qrupları
    // (öz tapşırıqları üçün) tapmaq üçün.
    private final GroupMemberRepository groupMemberRepository;
    // Tapşırığın əhatə etdiyi mövzunu slug-a görə tapmaq üçün.
    private final TopicRepository topicRepository;
    // Mövzudakı ümumi problem sayını hesablamaq üçün.
    private final ProblemRepository problemRepository;
    // Hər şagirdin bu mövzuda həll etdiyi problem sayını hesablamaq üçün.
    private final SubmissionRepository submissionRepository;
    // Tapşırığın kod nümunələri və şagirdlərin onları tamamlama qeydləri.
    private final AssignmentExampleRepository exampleRepository;
    private final ExampleCompletionRepository completionRepository;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public AssignmentService(
        AssignmentRepository assignmentRepository,
        GroupRepository groupRepository,
        GroupMemberRepository groupMemberRepository,
        TopicRepository topicRepository,
        ProblemRepository problemRepository,
        SubmissionRepository submissionRepository,
        AssignmentExampleRepository exampleRepository,
        ExampleCompletionRepository completionRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.exampleRepository = exampleRepository;
        this.completionRepository = completionRepository;
    }

    // Bir qrupun bütün tapşırıqlarının siyahısı (ən yenisi əvvəldə).
    @Transactional(readOnly = true)
    public List<AssignmentDto> listForGroup(Long groupId, User requester) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        List<AssignmentDto> result = new ArrayList<>();
        for (Assignment a : assignmentRepository.findByGroupOrderByDueAtDesc(group)) {
            result.add(toDto(a));
        }
        return result;
    }

    // Yeni tapşırıq yaradır.
    @Transactional
    public AssignmentDto create(Long groupId, User requester, AssignmentUpsertRequest req) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        Topic topic = getTopicOrThrow(req.getTopicSlug());

        Assignment assignment = new Assignment();
        assignment.setGroup(group);
        assignment.setTopic(topic);
        applyUpsert(assignment, req, topic);
        assignmentRepository.save(assignment);
        return toDto(assignment);
    }

    // Mövcud tapşırığı yeniləyir.
    @Transactional
    public AssignmentDto update(Long groupId, Long assignmentId, User requester, AssignmentUpsertRequest req) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        Assignment assignment = getAssignmentOrThrow(group, assignmentId);
        Topic topic = getTopicOrThrow(req.getTopicSlug());
        assignment.setTopic(topic);
        applyUpsert(assignment, req, topic);
        assignmentRepository.save(assignment);
        return toDto(assignment);
    }

    // Tapşırığı silir.
    @Transactional
    public void delete(Long groupId, Long assignmentId, User requester) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        Assignment assignment = getAssignmentOrThrow(group, assignmentId);
        completionRepository.deleteByExample_Assignment(assignment);
        exampleRepository.deleteByAssignment(assignment);
        assignmentRepository.delete(assignment);
    }

    // ---------- Kod nümunələri (şagird baxıb yazır) ----------

    // Tapşırığın kod nümunələri, hər biri üçün neçə şagirdin tamamladığı ilə (müəllim üçün).
    @Transactional(readOnly = true)
    public List<ExampleDto> listExamples(Long groupId, Long assignmentId, User requester) {
        Assignment assignment = getOwnedAssignment(groupId, assignmentId, requester);
        Map<Long, Long> completedCounts = new HashMap<>();
        for (ExampleCompletion c : completionRepository.findByExample_Assignment(assignment)) {
            completedCounts.merge(c.getExample().getId(), 1L, Long::sum);
        }
        List<ExampleDto> result = new ArrayList<>();
        for (AssignmentExample e : exampleRepository.findByAssignmentOrderByIdAsc(assignment)) {
            result.add(new ExampleDto(e.getId(), e.getTitle(), e.getSourceCode(), completedCounts.getOrDefault(e.getId(), 0L)));
        }
        return result;
    }

    // Tapşırığa yeni kod nümunəsi əlavə edir.
    @Transactional
    public ExampleDto addExample(Long groupId, Long assignmentId, User requester, ExampleUpsertRequest req) {
        Assignment assignment = getOwnedAssignment(groupId, assignmentId, requester);
        AssignmentExample example = new AssignmentExample();
        example.setAssignment(assignment);
        example.setTitle(req.getTitle().trim());
        example.setSourceCode(req.getSourceCode());
        exampleRepository.save(example);
        return new ExampleDto(example.getId(), example.getTitle(), example.getSourceCode(), 0L);
    }

    // Kod nümunəsini (və şagirdlərin ona aid tamamlanma qeydlərini) silir.
    @Transactional
    public void deleteExample(Long groupId, Long assignmentId, Long exampleId, User requester) {
        Assignment assignment = getOwnedAssignment(groupId, assignmentId, requester);
        AssignmentExample example = getExampleOrThrow(assignment, exampleId);
        completionRepository.deleteAll(completionRepository.findByExample_Assignment(assignment).stream()
            .filter(c -> c.getExample().getId().equals(example.getId()))
            .toList());
        exampleRepository.delete(example);
    }

    // Qrupun hər şagirdinin bu tapşırığın kod nümunələrindən neçəsini tamamladığı (müəllim jurnalı üçün).
    @Transactional(readOnly = true)
    public List<ExampleProgressRowDto> getExampleProgress(Long groupId, Long assignmentId, User requester) {
        Assignment assignment = getOwnedAssignment(groupId, assignmentId, requester);
        long total = exampleRepository.findByAssignmentOrderByIdAsc(assignment).size();
        Map<Long, Long> doneByStudent = new HashMap<>();
        for (ExampleCompletion c : completionRepository.findByExample_Assignment(assignment)) {
            doneByStudent.merge(c.getStudent().getId(), 1L, Long::sum);
        }
        List<ExampleProgressRowDto> result = new ArrayList<>();
        for (GroupMember member : groupMemberRepository.findByGroupOrderByJoinedAtAsc(assignment.getGroup())) {
            Long studentId = member.getStudent().getId();
            result.add(new ExampleProgressRowDto(studentId, doneByStudent.getOrDefault(studentId, 0L), total));
        }
        return result;
    }

    // Şagirdin bu tapşırığın kod nümunələri (yalnız qrupun üzvü görə bilər).
    @Transactional(readOnly = true)
    public List<StudentExampleDto> listExamplesForStudent(Long assignmentId, User student) {
        Assignment assignment = getAssignmentForMember(assignmentId, student);
        List<AssignmentExample> examples = exampleRepository.findByAssignmentOrderByIdAsc(assignment);
        if (examples.isEmpty()) {
            return List.of();
        }
        Set<Long> doneIds = new HashSet<>();
        for (ExampleCompletion c : completionRepository.findByStudentAndExampleIn(student, examples)) {
            doneIds.add(c.getExample().getId());
        }
        List<StudentExampleDto> result = new ArrayList<>();
        for (AssignmentExample e : examples) {
            result.add(new StudentExampleDto(e.getId(), e.getTitle(), e.getSourceCode(), doneIds.contains(e.getId())));
        }
        return result;
    }

    // Şagirdin yazdığı kodu nümunə ilə müqayisə edir; tam uyğun gələndə nümunə "tamamlandı" yazılır.
    @Transactional
    public ExampleCheckResponse checkExample(Long assignmentId, Long exampleId, User student, String typedCode) {
        Assignment assignment = getAssignmentForMember(assignmentId, student);
        AssignmentExample example = getExampleOrThrow(assignment, exampleId);
        ExampleCheckResponse response = CodeTypingComparer.compare(example.getSourceCode(), typedCode);
        if (response.isMatch() && !completionRepository.existsByExampleAndStudent(example, student)) {
            ExampleCompletion completion = new ExampleCompletion();
            completion.setExample(example);
            completion.setStudent(student);
            completionRepository.save(completion);
        }
        return response;
    }

    // "Tapşırıqlarım" kartları üçün: nümunəsi olan hər tapşırıqda neçə nümunə var, şagird neçəsini tamamlayıb.
    @Transactional(readOnly = true)
    public List<ExampleSummaryDto> examplesSummaryForStudent(User student) {
        List<Group> groups = new ArrayList<>();
        for (GroupMember member : groupMemberRepository.findByStudent(student)) {
            groups.add(member.getGroup());
        }
        if (groups.isEmpty()) {
            return List.of();
        }
        List<Assignment> assignments = assignmentRepository.findByGroupInOrderByDueAtAsc(groups);
        if (assignments.isEmpty()) {
            return List.of();
        }
        List<AssignmentExample> examples = exampleRepository.findByAssignmentIn(assignments);
        if (examples.isEmpty()) {
            return List.of();
        }
        Set<Long> doneIds = new HashSet<>();
        for (ExampleCompletion c : completionRepository.findByStudentAndExampleIn(student, examples)) {
            doneIds.add(c.getExample().getId());
        }
        Map<Long, long[]> perAssignment = new HashMap<>(); // [total, done]
        for (AssignmentExample e : examples) {
            long[] counts = perAssignment.computeIfAbsent(e.getAssignment().getId(), k -> new long[2]);
            counts[0]++;
            if (doneIds.contains(e.getId())) {
                counts[1]++;
            }
        }
        List<ExampleSummaryDto> result = new ArrayList<>();
        for (Map.Entry<Long, long[]> entry : perAssignment.entrySet()) {
            result.add(new ExampleSummaryDto(entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }
        return result;
    }

    // Qrupun sahibliyini yoxlayıb tapşırığı qaytarır.
    private Assignment getOwnedAssignment(Long groupId, Long assignmentId, User requester) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        return getAssignmentOrThrow(group, assignmentId);
    }

    // Şagirdin tapşırığın qrupunun üzvü olduğunu yoxlayır; deyilsə 404 (tapşırığın varlığını da açmır).
    private Assignment getAssignmentForMember(Long assignmentId, User student) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Tapşırıq tapılmadı: " + assignmentId));
        if (!groupMemberRepository.existsByGroupAndStudent(assignment.getGroup(), student)) {
            throw new NotFoundException("Tapşırıq tapılmadı: " + assignmentId);
        }
        return assignment;
    }

    // Nümunəni tapır və onun DOĞRUDAN bu tapşırığa aid olduğunu yoxlayır.
    private AssignmentExample getExampleOrThrow(Assignment assignment, Long exampleId) {
        AssignmentExample example = exampleRepository.findById(exampleId)
            .orElseThrow(() -> new NotFoundException("Nümunə tapılmadı: " + exampleId));
        if (!example.getAssignment().getId().equals(assignment.getId())) {
            throw new NotFoundException("Nümunə tapılmadı: " + exampleId);
        }
        return example;
    }

    // Bir tapşırığın sinif jurnalı — qrupun hər üzvü üçün, mövzudakı
    // problemlərdən neçəsini həll etdiyi və gecikib-gecikmədiyi.
    @Transactional(readOnly = true)
    public List<GradebookRowDto> getGradebook(Long groupId, Long assignmentId, User requester) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        Assignment assignment = getAssignmentOrThrow(group, assignmentId);
        long totalCount = problemRepository.countByTopic(assignment.getTopic());
        boolean pastDue = Instant.now().isAfter(assignment.getDueAt());

        List<GradebookRowDto> result = new ArrayList<>();
        for (GroupMember member : groupMemberRepository.findByGroupOrderByJoinedAtAsc(group)) {
            User student = member.getStudent();
            long solvedCount = submissionRepository.solvedProblemIdsForUserInTopic(student, assignment.getTopic()).size();
            boolean overdue = pastDue && solvedCount < totalCount;
            result.add(new GradebookRowDto(student.getId(), student.getFullName(), student.getEmail(), solvedCount, totalCount, overdue));
        }
        return result;
    }

    // Şagirdin ÜZVÜ olduğu bütün qruplardakı tapşırıqları, öz irəliləyişi
    // ilə birlikdə qaytarır ("Tapşırıqlarım" ekranı).
    @Transactional(readOnly = true)
    public List<StudentAssignmentDto> listForStudent(User student) {
        List<Group> groups = new ArrayList<>();
        for (GroupMember member : groupMemberRepository.findByStudent(student)) {
            groups.add(member.getGroup());
        }
        if (groups.isEmpty()) {
            return List.of();
        }

        Instant now = Instant.now();
        List<StudentAssignmentDto> result = new ArrayList<>();
        for (Assignment a : assignmentRepository.findByGroupInOrderByDueAtAsc(groups)) {
            long totalCount = problemRepository.countByTopic(a.getTopic());
            long solvedCount = submissionRepository.solvedProblemIdsForUserInTopic(student, a.getTopic()).size();
            boolean overdue = now.isAfter(a.getDueAt()) && solvedCount < totalCount;
            result.add(new StudentAssignmentDto(
                a.getId(), a.getGroup().getName(), a.getTopic().getSlug(), a.getTopic().getTitle(),
                a.getTitle(), a.getDescription(), a.getDueAt(), solvedCount, totalCount, overdue
            ));
        }
        return result;
    }

    // Qrupu ID-yə görə tapır, yoxdursa 404 atır.
    private Group getGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Qrup tapılmadı: " + groupId));
    }

    // Mövzunu slug-a görə tapır, yoxdursa 404 atır.
    private Topic getTopicOrThrow(String topicSlug) {
        Topic topic = topicRepository.findBySlug(topicSlug);
        if (topic == null) {
            throw new NotFoundException("Mövzu tapılmadı: " + topicSlug);
        }
        return topic;
    }

    // Tapşırığı ID-yə görə tapır, HƏM DƏ onun DOĞRUDAN bu qrupa aid olduğunu
    // yoxlayır — əks halda başqa qrupun tapşırıq ID-sini bu qrupun URL-inə
    // yazaraq yanlış qovluqda redaktə etmək mümkün olardı.
    private Assignment getAssignmentOrThrow(Group group, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Tapşırıq tapılmadı: " + assignmentId));
        if (!assignment.getGroup().getId().equals(group.getId())) {
            throw new NotFoundException("Tapşırıq tapılmadı: " + assignmentId);
        }
        return assignment;
    }

    // Sorğunu edən istifadəçinin bu qrupu idarə etməyə səlahiyyəti olub-olmadığını
    // yoxlayır: ADMIN həmişə keçir, TEACHER isə yalnız ÖZ qrupu üçün (bax:
    // GroupService.requireOwnership, eyni qayda).
    private void requireOwnership(Group group, User requester) {
        if (requester.getRole() == Role.ADMIN) {
            return;
        }
        if (!group.getTeacher().getId().equals(requester.getId())) {
            throw new ForbiddenException("Bu qrup sizə aid deyil");
        }
    }

    // AssignmentUpsertRequest-dəki sahələri Assignment entity-sinə köçürür.
    private void applyUpsert(Assignment assignment, AssignmentUpsertRequest req, Topic topic) {
        assignment.setTitle(req.getTitle());
        assignment.setDescription(req.getDescription());
        assignment.setDueAt(req.getDueAt());
    }

    // Assignment entity-sini AssignmentDto-ya çevirir.
    private AssignmentDto toDto(Assignment a) {
        long totalProblems = problemRepository.countByTopic(a.getTopic());
        return new AssignmentDto(
            a.getId(), a.getGroup().getId(), a.getTopic().getSlug(), a.getTopic().getTitle(),
            a.getTitle(), a.getDescription(), a.getDueAt(), totalProblems, a.getCreatedAt()
        );
    }
}
