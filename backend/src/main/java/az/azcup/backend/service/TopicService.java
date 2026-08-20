package az.azcup.backend.service;

import az.azcup.backend.dto.TopicDto;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;

    public TopicService(TopicRepository topicRepository, ProblemRepository problemRepository, SubmissionRepository submissionRepository) {
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
    }

    // STUDENT rolu üçün yalnız müəllim/admin tərəfindən "açıq" elan olunmuş
    // (published=true) mövzular göstərilir — TEACHER və ADMIN isə hələ açıq
    // olmayanları da görür ki, dərc etməzdən əvvəl nəzərdən keçirə bilsin.
    @Transactional(readOnly = true)
    public List<TopicDto> listTopics(User user) {
        List<Topic> allTopics = topicRepository.findAllByOrderByOrderIndexAsc();
        List<Topic> topics = new ArrayList<>();
        for (Topic t : allTopics) {
            if (user.getRole() != Role.STUDENT || t.isPublished()) {
                topics.add(t);
            }
        }
        Map<Long, Long> solvedByTopicId = new HashMap<>();
        for (SubmissionRepository.TopicSolvedCount tsc : submissionRepository.solvedCountsByTopicForUser(user)) {
            solvedByTopicId.put(tsc.getTopicId(), tsc.getSolvedCount());
        }
        List<TopicDto> result = new ArrayList<>();
        for (Topic t : topics) {
            result.add(new TopicDto(
                t.getId(),
                t.getSlug(),
                t.getOrderIndex(),
                t.getTitle(),
                t.getMonthTag(),
                t.getDescription(),
                t.isPublished(),
                problemRepository.countByTopic(t),
                solvedByTopicId.getOrDefault(t.getId(), 0L)
            ));
        }
        return result;
    }

    // Bir STUDENT-in hələ açıq elan olunmamış mövzunu slug-unu bilərəkdən
    // sınayaraq (URL-i "təxmin edərək") görməsinin qarşısını almaq üçün, bu
    // yoxlama TopicController-in özündə deyil, birbaşa burada aparılır —
    // beləliklə bu metoddan keçən HƏR yol (mövzu siyahısı, məsələ siyahısı,
    // məsələ təfərrüatı) eyni qaydaya tabe olur. Mövcud olmayan mövzu ilə
    // eyni NotFoundException atılır ki, "mövzu yoxdur" ilə "mövzu hələ açıq
    // deyil" halları bir-birindən fərqləndirilə bilinməsin.
    @Transactional(readOnly = true)
    public Topic getBySlug(String slug, User user) {
        Topic topic = topicRepository.findBySlug(slug);
        if (topic == null) {
            throw new NotFoundException("Mövzu tapılmadı: " + slug);
        }
        if (user.getRole() == Role.STUDENT && !topic.isPublished()) {
            throw new NotFoundException("Mövzu tapılmadı: " + slug);
        }
        return topic;
    }
}
