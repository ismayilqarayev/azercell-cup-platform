package az.azcup.backend.repository

import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemRepository : JpaRepository<Problem, Long> {
    fun findAllByTopicOrderByOrderIndexAsc(topic: Topic): List<Problem>
    fun countByTopic(topic: Topic): Long
}
