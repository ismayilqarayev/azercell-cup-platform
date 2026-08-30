package az.azcup.backend.repository;

import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemTestCaseRepository extends JpaRepository<ProblemTestCase, Long> {
    // Bir problemin bütün əlavə (gizli) test hallarını, icra sırasına görə qaytarır.
    List<ProblemTestCase> findByProblemOrderByOrderIndexAsc(Problem problem);

    // Problem silinəndə ona aid bütün test hallarını əvvəlcədən təmizləmək üçün
    // (FK ON DELETE CASCADE təyin olunmadığı üçün, əks halda constraint xətası olardı).
    void deleteByProblem(Problem problem);
}
