package az.azcup.backend.repository;

import az.azcup.backend.entity.Contest;
import az.azcup.backend.entity.ContestProblem;
import az.azcup.backend.entity.ContestSubmission;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestSubmissionRepository extends JpaRepository<ContestSubmission, Long> {

    // Bir yarışın BÜTÜN təqdimatları — reytinq cədvəlini Java tərəfində
    // (JPQL GROUP BY əvəzinə) hesablamaq üçün istifadə olunur, çünki bu
    // layihədə mürəkkəb sorğular yerinə sadə "çək + Java-da topla" üsulu
    // üstünlük təşkil edir (bax: SubmissionService.progress-dəki eyni yanaşma).
    List<ContestSubmission> findByContestOrderBySubmittedAtAsc(Contest contest);

    // Bu şagirdin bu yarış məsələsinə göndərdiyi bütün cəhdlər (ən yenisi
    // əvvəldə) — "Tarixçə" funksiyası üçün.
    List<ContestSubmission> findByUserAndContestProblemOrderBySubmittedAtDesc(User user, ContestProblem contestProblem);

    // Şagird bu məsələni ARTIQ (bu cəhddən əvvəl) tam həll edib-etmədiyini
    // yoxlamaq üçün — "hamısı ya heç nə" bal siyasətində təkrar bal
    // verilməsinin qarşısını alır (bax: ContestSubmissionService.submit).
    boolean existsByUserAndContestProblemAndPointsAwardedGreaterThan(User user, ContestProblem contestProblem, int points);
}
