package az.azcup.backend.repository;

import az.azcup.backend.entity.ContestProblem;
import az.azcup.backend.entity.ContestTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestTestCaseRepository extends JpaRepository<ContestTestCase, Long> {

    // Bir yarış məsələsinin bütün test halları (gizli + nümunə), icra
    // sırasına görə — JudgeService.judgeMultiple bunları sırayla işlədir.
    List<ContestTestCase> findByContestProblemOrderByOrderIndexAsc(ContestProblem contestProblem);
}
