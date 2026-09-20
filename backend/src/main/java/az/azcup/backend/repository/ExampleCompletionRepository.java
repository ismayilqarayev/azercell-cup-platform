package az.azcup.backend.repository;

import az.azcup.backend.entity.Assignment;
import az.azcup.backend.entity.AssignmentExample;
import az.azcup.backend.entity.ExampleCompletion;
import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExampleCompletionRepository extends JpaRepository<ExampleCompletion, Long> {

    boolean existsByExampleAndStudent(AssignmentExample example, User student);

    // Şagirdin verilən nümunələr arasından tamamladıqları.
    List<ExampleCompletion> findByStudentAndExampleIn(User student, Collection<AssignmentExample> examples);

    // Bir tapşırığın bütün nümunələri üzrə bütün tamamlanmalar (müəllim jurnalı üçün).
    List<ExampleCompletion> findByExample_Assignment(Assignment assignment);

    // Şagirdin bir tapşırıqdakı bütün tamamlanmaları (müəllimin şagird təfərrüatı üçün).
    List<ExampleCompletion> findByStudentAndExample_Assignment(User student, Assignment assignment);

    // Fəaliyyət xəritəsi (heatmap) üçün: şagirdin verilən vaxtdan sonrakı tamamlanmaları.
    List<ExampleCompletion> findByStudentAndCompletedAtAfter(User student, java.time.Instant since);

    void deleteByExample(AssignmentExample example);

    void deleteByExample_Assignment(Assignment assignment);

    void deleteByExample_Assignment_Group(Group group);
}
