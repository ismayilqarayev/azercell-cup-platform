package az.azcup.backend.repository

import az.azcup.backend.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepository : JpaRepository<Topic, Long> {
    // URL-dəki slug-a görə mövzunu tapır (məs. "/api/topics/week1/problems").
    fun findBySlug(slug: String): Topic?

    // Roadmap-da göstərilmə sırasına görə bütün mövzuları qaytarır.
    fun findAllByOrderByOrderIndexAsc(): List<Topic>
}
