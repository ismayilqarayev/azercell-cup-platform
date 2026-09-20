package az.azcup.backend.repository;

import az.azcup.backend.entity.Assignment;
import az.azcup.backend.entity.AssignmentExample;
import az.azcup.backend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AssignmentExampleRepository extends JpaRepository<AssignmentExample, Long> {

    List<AssignmentExample> findByAssignmentOrderByIdAsc(Assignment assignment);

    List<AssignmentExample> findByAssignmentIn(Collection<Assignment> assignments);

    // Tapşırıq/qrup silinəndə nümunələri təmizləmək üçün (FK cascade yoxdur — bax: GroupService.deleteGroup).
    void deleteByAssignment(Assignment assignment);

    void deleteByAssignment_Group(Group group);
}
