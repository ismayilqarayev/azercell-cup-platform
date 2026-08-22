package az.azcup.backend.repository;

import az.azcup.backend.entity.Contest;
import az.azcup.backend.entity.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Long> {

    // Bir yarışın bütün məsələləri, göstərilmə sırasına görə.
    List<ContestProblem> findByContestOrderByOrderIndexAsc(Contest contest);

    // Bir yarışa aid neçə məsələ olduğunu sürətli saymaq üçün (admin
    // panelində "N məsələ" kimi göstərmək üçün).
    long countByContest(Contest contest);
}
