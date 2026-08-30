package az.azcup.backend.repository;

import az.azcup.backend.entity.Assignment;
import az.azcup.backend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Bir qrupun bütün tapşırıqları, ən yenisi əvvəldə — müəllimin qrup
    // görünüşü üçün.
    List<Assignment> findByGroupOrderByDueAtDesc(Group group);

    // Bir neçə qrupun (şagirdin ÜZVÜ olduğu bütün qruplar) tapşırıqları,
    // son tarixə görə artan sırada — şagirdin "Tapşırıqlarım" görünüşü üçün
    // (bax: AssignmentService.listForStudent).
    List<Assignment> findByGroupInOrderByDueAtAsc(List<Group> groups);

    // Qrup silinəndə ona aid bütün tapşırıqları təmizləmək üçün.
    void deleteByGroup(Group group);
}
