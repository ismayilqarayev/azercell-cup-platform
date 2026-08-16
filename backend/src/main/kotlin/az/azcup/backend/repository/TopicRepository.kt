package az.azcup.backend.repository

import az.azcup.backend.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findBySlug(slug: String): Topic?
    fun findAllByOrderByOrderIndexAsc(): List<Topic>
}
