package az.azcup.backend.repository;

import az.azcup.backend.entity.QuizAnswer;
import az.azcup.backend.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    // Bir cəhdin bütün cavabları (nəticə səhifəsi).
    List<QuizAnswer> findByAttempt(QuizAttempt attempt);

    // Müəllimin sual statistikası üçün: verilən cəhdlərin hamısının cavabları.
    List<QuizAnswer> findByAttemptIn(Collection<QuizAttempt> attempts);
}
