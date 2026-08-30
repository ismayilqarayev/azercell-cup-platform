package az.azcup.backend.service;

import az.azcup.backend.dto.StudentAssignmentDto;
import az.azcup.backend.dto.teacher.AssignmentDto;
import az.azcup.backend.dto.teacher.AssignmentUpsertRequest;
import az.azcup.backend.dto.teacher.GradebookRowDto;
import az.azcup.backend.entity.Assignment;
import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.GroupMember;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ForbiddenException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.AssignmentRepository;
import az.azcup.backend.repository.GroupMemberRepository;
import az.azcup.backend.repository.GroupRepository;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public AssignmentService(
        AssignmentRepository assignmentRepository,
        GroupRepository groupRepository,
        GroupMemberRepository groupMemberRepository,
        TopicRepository topicRepository,
        ProblemRepository problemRepository,
        SubmissionRepository submissionRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
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
        assignmentRepository.delete(assignment);
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
