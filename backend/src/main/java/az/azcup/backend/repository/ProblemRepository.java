package az.azcup.backend.repository;

import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data JPA — bu interfeysin gövdəsini biz yazmırıq, Spring metod
// adından (findAllByTopicOrderByOrderIndexAsc) avtomatik SQL sorğusu yaradır.
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    // Bir mövzunun bütün problemlərini, göstərilmə sırasına görə sıralanmış qaytarır.
    List<Problem> findAllByTopicOrderByOrderIndexAsc(Topic topic);

    // Bir mövzuda neçə problem olduğunu sayır (TopicDto-dakı problemCount üçün).
    long countByTopic(Topic topic);
}
