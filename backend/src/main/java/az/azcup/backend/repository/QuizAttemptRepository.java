package az.azcup.backend.repository;

import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.QuizAttempt;
import az.azcup.backend.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    // Təhvil zamanı eyni cəhdin iki paralel sorğu ilə iki dəfə qiymətləndirilməməsi üçün sətir kilidlənir.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from QuizAttempt a where a.id = :id")
    Optional<QuizAttempt> findByIdForUpdate(@Param("id") Long id);

    // Şagirdin bu testdə hələ təhvil verilməmiş (davam edən) ən son cəhdi.
    Optional<QuizAttempt> findFirstByQuizAndStudentAndSubmittedAtIsNullOrderByStartedAtDesc(Quiz quiz, User student);

    // Şagirdin təhvil verilmiş bütün cəhdləri (siyahıda "ən yaxşı nəticə" üçün).
    List<QuizAttempt> findByStudentAndSubmittedAtIsNotNull(User student);

    // Şagirdin davam edən cəhdləri (siyahıda "davam edir" nişanı üçün).
    List<QuizAttempt> findByStudentAndSubmittedAtIsNull(User student);

    // Testin təhvil verilmiş cəhdlərinin sayı.
    long countByQuizAndSubmittedAtIsNotNull(Quiz quiz);

    // Müəllimin nəticə cədvəli üçün testin təhvil verilmiş bütün cəhdləri.
    List<QuizAttempt> findByQuizAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(Quiz quiz);

    // Test silinəndə (yalnız həll olunmamış testlər silinə bilər) davam edən boş cəhdləri təmizləmək üçün.
    void deleteByQuiz(Quiz quiz);
}
