package az.azcup.backend.repository;

import az.azcup.backend.entity.Contest;
import az.azcup.backend.entity.ContestParticipant;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, Long> {

    // Bu istifadəçinin bu yarışa artıq qoşulub-qoşulmadığını yoxlamaq üçün
    // (ContestService.join-də təkrar qoşulmanın qarşısını almaq, və
    // ContestSubmissionService.submit-də "qoşulmadan göndərə bilməz" qaydası üçün).
    Optional<ContestParticipant> findByContestAndUser(Contest contest, User user);

    // Reytinq cədvəlində, hələ heç bir məsələni həll etməmiş (0 balı olan)
    // iştirakçıları da göstərmək üçün — bax: ContestService.leaderboard.
    List<ContestParticipant> findByContest(Contest contest);
}
