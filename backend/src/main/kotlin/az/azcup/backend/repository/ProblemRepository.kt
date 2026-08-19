package az.azcup.backend.repository

import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository

// Spring Data JPA — bu interfeysin gövdəsini biz yazmırıq, Spring metod
// adından (findAllByTopicOrderByOrderIndexAsc) avtomatik SQL sorğusu yaradır.
interface ProblemRepository : JpaRepository<Problem, Long> {
    // Bir mövzunun bütün problemlərini, göstərilmə sırasına görə sıralanmış qaytarır.
    fun findAllByTopicOrderByOrderIndexAsc(topic: Topic): List<Problem>

    // Bir mövzuda neçə problem olduğunu sayır (TopicDto-dakı problemCount üçün).
    fun countByTopic(topic: Topic): Long
}
