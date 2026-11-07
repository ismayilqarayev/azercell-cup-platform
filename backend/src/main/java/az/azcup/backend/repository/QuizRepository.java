package az.azcup.backend.repository;

import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Şagirdlərə göstərilən siyahı: yalnız dərc olunmuşlar, yenisi əvvəldə.
    List<Quiz> findByPublishedTrueOrderByCreatedAtDesc();

    // Müəllimin öz testləri (idarəetmə siyahısı).
    List<Quiz> findByCreatedByOrderByUpdatedAtDesc(User createdBy);

    // Admin üçün bütün testlər.
    List<Quiz> findAllByOrderByUpdatedAtDesc();
}
