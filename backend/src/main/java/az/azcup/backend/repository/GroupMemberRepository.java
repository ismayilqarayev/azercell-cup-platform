package az.azcup.backend.repository;

import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.GroupMember;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    // Bir qrupun bütün üzvlərini (şagirdlərini) qaytarır — irəliləyiş cədvəli üçün.
    List<GroupMember> findByGroupOrderByJoinedAtAsc(Group group);

    // Eyni şagirdin eyni qrupa artıq əlavə olunub-olunmadığını yoxlamaq üçün
    // (təkrar əlavəni bloklamaq məqsədilə).
    boolean existsByGroupAndStudent(Group group, User student);

    // Bir üzvlük sətrini qrup+şagird cütünə görə tapır (silmək üçün istifadə olunur).
    Optional<GroupMember> findByGroupAndStudent(Group group, User student);

    // Qrup silinəndə ona aid bütün üzvlük sətirlərini təmizləmək üçün.
    void deleteByGroup(Group group);
}
