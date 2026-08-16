package az.azcup.backend.repository

import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.Submission
import az.azcup.backend.entity.SubmissionStatus
import az.azcup.backend.entity.Topic
import az.azcup.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SubmissionRepository : JpaRepository<Submission, Long> {

    fun findByUserAndProblemOrderBySubmittedAtDesc(user: User, problem: Problem): List<Submission>

    fun existsByUserAndProblemAndStatus(user: User, problem: Problem, status: SubmissionStatus): Boolean

    @Query(
        """
        SELECT p.topic.id AS topicId, COUNT(DISTINCT p.id) AS solvedCount
        FROM Submission s JOIN s.problem p
        WHERE s.user = :user AND s.status = az.azcup.backend.entity.SubmissionStatus.ACCEPTED
        GROUP BY p.topic.id
        """
    )
    fun solvedCountsByTopicForUser(@Param("user") user: User): List<TopicSolvedCount>

    @Query(
        """
        SELECT DISTINCT s.problem.id
        FROM Submission s
        WHERE s.user = :user AND s.problem.topic = :topic AND s.status = az.azcup.backend.entity.SubmissionStatus.ACCEPTED
        """
    )
    fun solvedProblemIdsForUserInTopic(@Param("user") user: User, @Param("topic") topic: Topic): List<Long>

    @Query(
        """
        SELECT COUNT(DISTINCT s.problem.id)
        FROM Submission s
        WHERE s.user = :user AND s.status = az.azcup.backend.entity.SubmissionStatus.ACCEPTED
        """
    )
    fun countDistinctSolvedProblems(@Param("user") user: User): Long

    interface TopicSolvedCount {
        val topicId: Long
        val solvedCount: Long
    }
}
