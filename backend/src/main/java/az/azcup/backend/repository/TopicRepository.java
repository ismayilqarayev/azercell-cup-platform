package az.azcup.backend.repository;

import az.azcup.backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    // URL-dəki slug-a görə mövzunu tapır (məs. "/api/topics/week1/problems").
    Topic findBySlug(String slug);

    // Roadmap-da göstərilmə sırasına görə bütün mövzuları qaytarır.
    List<Topic> findAllByOrderByOrderIndexAsc();
}
