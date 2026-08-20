package az.azcup.backend.service;

import az.azcup.backend.dto.ProblemDetailDto;
import az.azcup.backend.dto.ProblemSummaryDto;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.SubmissionStatus;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Problemlərin OXUNMASI (siyahı və detal) ilə bağlı biznes-məntiq.
// Yaratma/redaktə AdminService-dədir, göndərmə/yoxlama SubmissionService-dədir.
@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final TopicService topicService;

    public ProblemService(ProblemRepository problemRepository, SubmissionRepository submissionRepository, TopicService topicService) {
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.topicService = topicService;
    }

    // Mövzunun slug-una görə (məs. "week1") o mövzudakı bütün problemlərin
    // qısa siyahısını qaytarır. topicService.getBySlug() daxildə mövzunun
    // açıq (published) olub-olmadığını da yoxlayır — STUDENT hələ açılmamış
    // mövzunu bu yolla görə bilməz.
    @Transactional(readOnly = true)
    public List<ProblemSummaryDto> listByTopicSlug(String slug, User user) {
        Topic topic = topicService.getBySlug(slug, user);
        List<Problem> problems = problemRepository.findAllByTopicOrderByOrderIndexAsc(topic);
        // Bu istifadəçinin hansı problemləri artıq həll etdiyini TƏK bir
        // sorğu ilə əldə edib Set-ə çeviririk ki, aşağıdakı dövr içində hər
        // problem üçün ayrıca bazaya getmək əvəzinə O(1) yoxlama aparılsın.
        Set<Long> solvedIds = new HashSet<>(submissionRepository.solvedProblemIdsForUserInTopic(user, topic));
        List<ProblemSummaryDto> result = new ArrayList<>();
        for (Problem p : problems) {
            result.add(new ProblemSummaryDto(
                p.getId(), p.getOrderIndex(), p.getSubgroupLabel(), p.getTitle(),
                p.getDifficulty(), p.getTags(), solvedIds.contains(p.getId())
            ));
        }
        return result;
    }

    // Bir problemin tam detallarını (şərt, giriş/çıxış spesifikasiyası,
    // nümunələr) qaytarır.
    @Transactional(readOnly = true)
    public ProblemDetailDto getDetail(Long id, User user) {
        Problem p = getEntity(id, user);
        boolean solved = submissionRepository.existsByUserAndProblemAndStatus(user, p, SubmissionStatus.ACCEPTED);
        return new ProblemDetailDto(
            p.getId(), p.getTopic().getSlug(), p.getOrderIndex(), p.getSubgroupLabel(), p.getTitle(),
            p.getDifficulty(), p.getTags(), p.getStatement(), p.getInputSpec(), p.getOutputSpec(),
            p.getExampleInput(), p.getExampleOutput(), p.getApproach(), solved
        );
    }

    // ID ilə xam Problem entity-sini tapır — həm getDetail(), həm də
    // SubmissionService (submit/history) buradan keçir, ona görə "bu məsələ
    // hələ açıq elan olunmayan bir mövzudadırsa, STUDENT ona nə baxa, nə də
    // kod göndərə bilməz" qaydası bir yerdə, mərkəzləşdirilmiş şəkildə
    // tətbiq olunur — problem ID-sini əl ilə "təxmin edərək" bu qadağanı
    // dolayı yolla keçmək mümkün olmasın deyə.
    @Transactional(readOnly = true)
    public Problem getEntity(Long id, User user) {
        Problem p = problemRepository.findById(id).orElseThrow(() -> new NotFoundException("Məsələ tapılmadı: " + id));
        if (user.getRole() == Role.STUDENT && p.getTopic() != null && !p.getTopic().isPublished()) {
            throw new NotFoundException("Məsələ tapılmadı: " + id);
        }
        return p;
    }
}
