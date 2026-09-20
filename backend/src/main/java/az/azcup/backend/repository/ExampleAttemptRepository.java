package az.azcup.backend.repository;

import az.azcup.backend.entity.Assignment;
import az.azcup.backend.entity.AssignmentExample;
import az.azcup.backend.entity.ExampleAttempt;
import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExampleAttemptRepository extends JpaRepository<ExampleAttempt, Long> {

    // Bir şagirdin bir tapşırığın bütün nümunələri üzrə bütün cəhdləri (müəllimin şagird təfərrüatı üçün).
    List<ExampleAttempt> findByExample_AssignmentAndStudent(Assignment assignment, User student);

    // Yeni cəhd yazılmazdan əvvəl köhnə cəhdlərdəki kodu təmizləyir — yalnız ən son cəhddə kod qalır.
    @Modifying
    @Query("update ExampleAttempt a set a.typedCode = null where a.example = :example and a.student = :student and a.typedCode is not null")
    void clearTypedCode(@Param("example") AssignmentExample example, @Param("student") User student);

    void deleteByExample(AssignmentExample example);

    void deleteByExample_Assignment(Assignment assignment);

    void deleteByExample_Assignment_Group(Group group);
}
