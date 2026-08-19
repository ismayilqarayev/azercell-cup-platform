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

    // Bir şagirdin bir problemə göndərdiyi bütün cəhdlərin tarixçəsi
    // (ən yenisi əvvəldə) — "Tarixçə" düyməsi bunu çağırır.
    fun findByUserAndProblemOrderBySubmittedAtDesc(user: User, problem: Problem): List<Submission>

    // Şagird həmin problemi artıq həll edib-etmədiyini (ACCEPTED alıb-almadığını)
    // sürətli yoxlamaq üçün — hər dəfə bütün tarixçəni çəkib gəzməyə ehtiyac qalmır.
    fun existsByUserAndProblemAndStatus(user: User, problem: Problem, status: SubmissionStatus): Boolean

    // Hər mövzu üçün, bu istifadəçinin neçə FƏRQLİ problem həll etdiyini
    // (ACCEPTED aldığını) hesablayır — GROUP BY topic.id ilə tək sorğuda.
    // Nəticə TopicSolvedCount interfeysi (Spring "projection") vasitəsilə
    // topicId/solvedCount cütləri kimi qaytarılır (ProgressDto üçün istifadə olunur).
    @Query(
        """
        SELECT p.topic.id AS topicId, COUNT(DISTINCT p.id) AS solvedCount
        FROM Submission s JOIN s.problem p
        WHERE s.user = :user AND s.status = az.azcup.backend.entity.SubmissionStatus.ACCEPTED
        GROUP BY p.topic.id
        """
    )
    fun solvedCountsByTopicForUser(@Param("user") user: User): List<TopicSolvedCount>

    // Konkret bir mövzuda, bu istifadəçinin həll etdiyi problem ID-lərinin
    // siyahısı — frontend-də hər problem kartında "✓ Həll edilib" nişanını
    // göstərmək üçün istifadə olunur.
    @Query(
        """
        SELECT DISTINCT s.problem.id
        FROM Submission s
        WHERE s.user = :user AND s.problem.topic = :topic AND s.status = az.azcup.backend.entity.SubmissionStatus.ACCEPTED
        """
    )
    fun solvedProblemIdsForUserInTopic(@Param("user") user: User, @Param("topic") topic: Topic): List<Long>

    // İstifadəçinin bütün mövzular üzrə cəmi neçə fərqli problem həll etdiyi
    // (ümumi irəliləyiş statistikası üçün).
    @Query(
        """
        SELECT COUNT(DISTINCT s.problem.id)
        FROM Submission s
        WHERE s.user = :user AND s.status = az.azcup.backend.entity.SubmissionStatus.ACCEPTED
        """
    )
    fun countDistinctSolvedProblems(@Param("user") user: User): Long

    // Spring Data "interface-based projection" — yuxarıdakı @Query-nin SELECT
    // siyahısındakı sütun adlarına (topicId, solvedCount) uyğun get-metodları.
    // Spring bunun arxasında dinamik proxy yaradıb nəticə sətirlərini bura map edir.
    interface TopicSolvedCount {
        val topicId: Long
        val solvedCount: Long
    }
}
