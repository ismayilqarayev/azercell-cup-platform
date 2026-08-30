package az.azcup.backend.service;

import az.azcup.backend.dto.teacher.GroupDto;
import az.azcup.backend.dto.teacher.GroupMemberDto;
import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.GroupMember;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ConflictException;
import az.azcup.backend.exception.ForbiddenException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.GroupMemberRepository;
import az.azcup.backend.repository.GroupRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Müəllimin öz şagird qruplarını yaratmasını/idarə etməsini və hər qrupun
// irəliləyiş cədvəlini görməsini təmin edir. ADMIN bütün qrupları görə bilir
// (overview üçün), amma yalnız qrupun SAHİB müəllimi (və ya ADMIN) onu
// dəyişə/silə bilər — bax: requireOwnership().
@Service
public class GroupService {

    // Qrup sətirlərini oxumaq/yazmaq üçün.
    private final GroupRepository groupRepository;
    // Qrup üzvlük sətirlərini oxumaq/yazmaq üçün.
    private final GroupMemberRepository groupMemberRepository;
    // Şagirdi e-poçtla tapmaq üçün.
    private final UserRepository userRepository;
    // Hər şagirdin ümumi həll etdiyi problem sayını hesablamaq üçün
    // (irəliləyiş cədvəlində istifadə olunur).
    private final SubmissionRepository submissionRepository;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public GroupService(
        GroupRepository groupRepository,
        GroupMemberRepository groupMemberRepository,
        UserRepository userRepository,
        SubmissionRepository submissionRepository
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
    }

    // Bir müəllimin sahib olduğu bütün qrupların siyahısı ("Qruplarım" ekranı).
    @Transactional(readOnly = true)
    public List<GroupDto> listMyGroups(User teacher) {
        return groupRepository.findByTeacherOrderByCreatedAtDesc(teacher).stream()
            .map(this::toDto)
            .toList();
    }

    // Bütün qrupların siyahısı — yalnız ADMIN overview ekranı üçün (bax:
    // AdminController.listGroups). Sahiblikdən asılı olmayaraq hər şeyi qaytarır.
    @Transactional(readOnly = true)
    public List<GroupDto> listAllGroups() {
        return groupRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    // Yeni qrup yaradır, sahibi sorğunu edən müəllim (və ya admin) olur.
    @Transactional
    public GroupDto createGroup(User teacher, String name) {
        Group group = new Group();
        group.setName(name);
        group.setTeacher(teacher);
        return toDto(groupRepository.save(group));
    }

    // Qrupun adını dəyişir — yalnız sahib müəllim və ya ADMIN edə bilər.
    @Transactional
    public GroupDto renameGroup(Long groupId, User requester, String newName) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        group.setName(newName);
        return toDto(groupRepository.save(group));
    }

    // Qrupu (və bütün üzvlük sətirlərini) silir — yalnız sahib müəllim və ya ADMIN edə bilər.
    @Transactional
    public void deleteGroup(Long groupId, User requester) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        groupMemberRepository.deleteByGroup(group);
        groupRepository.delete(group);
    }

    // Qrupun üzv (şagird) siyahısını, hər birinin ümumi həll sayı ilə birlikdə qaytarır.
    @Transactional(readOnly = true)
    public List<GroupMemberDto> listMembers(Long groupId, User requester) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);
        return groupMemberRepository.findByGroupOrderByJoinedAtAsc(group).stream()
            .map(m -> new GroupMemberDto(
                m.getId(),
                m.getStudent().getId(),
                m.getStudent().getFullName(),
                m.getStudent().getEmail(),
                submissionRepository.countDistinctSolvedProblems(m.getStudent()),
                m.getJoinedAt()
            ))
            .toList();
    }

    // E-poçtla tapılan şagirdi qrupa əlavə edir. Şagird tapılmazsa və ya
    // rolu STUDENT deyilsə, yaxud artıq qrupdadırsa, uyğun xəta atılır.
    @Transactional
    public GroupMemberDto addStudent(Long groupId, User requester, String studentEmail) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);

        User student = userRepository.findByEmail(studentEmail);
        if (student == null || student.getRole() != Role.STUDENT) {
            throw new NotFoundException("Bu e-poçtla şagird tapılmadı: " + studentEmail);
        }
        if (groupMemberRepository.existsByGroupAndStudent(group, student)) {
            throw new ConflictException("Bu şagird artıq qrupdadır");
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setStudent(student);
        GroupMember saved = groupMemberRepository.save(member);

        return new GroupMemberDto(
            saved.getId(),
            student.getId(),
            student.getFullName(),
            student.getEmail(),
            submissionRepository.countDistinctSolvedProblems(student),
            saved.getJoinedAt()
        );
    }

    // Şagirdi qrupdan çıxarır (üzvlük sətrini silir) — şagirdin hesabına
    // toxunulmur, sadəcə bu qrupla əlaqəsi kəsilir.
    @Transactional
    public void removeStudent(Long groupId, User requester, Long membershipId) {
        Group group = getGroupOrThrow(groupId);
        requireOwnership(group, requester);

        GroupMember member = groupMemberRepository.findById(membershipId)
            .orElseThrow(() -> new NotFoundException("Üzvlük tapılmadı: " + membershipId));
        if (!member.getGroup().getId().equals(group.getId())) {
            throw new NotFoundException("Üzvlük tapılmadı: " + membershipId);
        }
        groupMemberRepository.delete(member);
    }

    // Qrupu ID-yə görə tapır, yoxdursa 404 atır.
    private Group getGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Qrup tapılmadı: " + groupId));
    }

    // Sorğunu edən istifadəçinin bu qrupu idarə etməyə səlahiyyəti olub-olmadığını
    // yoxlayır: ADMIN həmişə keçir, TEACHER isə yalnız ÖZ qrupu üçün.
    private void requireOwnership(Group group, User requester) {
        if (requester.getRole() == Role.ADMIN) {
            return;
        }
        if (!group.getTeacher().getId().equals(requester.getId())) {
            throw new ForbiddenException("Bu qrup sizə aid deyil");
        }
    }

    // Group entity-sini GroupDto-ya çevirir (üzv sayını ayrıca sorğu ilə hesablayır).
    private GroupDto toDto(Group group) {
        long memberCount = groupMemberRepository.findByGroupOrderByJoinedAtAsc(group).size();
        return new GroupDto(
            group.getId(),
            group.getName(),
            group.getTeacher().getId(),
            group.getTeacher().getFullName(),
            memberCount,
            group.getCreatedAt()
        );
    }
}
