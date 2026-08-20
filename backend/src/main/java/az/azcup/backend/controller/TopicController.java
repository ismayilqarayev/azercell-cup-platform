package az.azcup.backend.controller;

import az.azcup.backend.dto.TopicDto;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.TopicService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Şagirdlərin roadmap (dərs proqramı) səhifəsində gördüyü mövzu siyahısı.
@RestController
@RequestMapping("/api/topics")
public class TopicController {

    // Mövzu siyahısı sorğularının faktiki icraçısı.
    private final TopicService topicService;

    // Spring tərəfindən inject olunan TopicService-i sahəyə təyin edir.
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    // STUDENT roluna yalnız dərc olunmuş (published=true) mövzular göstərilir,
    // TEACHER/ADMIN isə hamısını görür — bu qərar burada yox, TopicService-də
    // verilir (bax: TopicService.listTopics).
    @GetMapping
    public List<TopicDto> listTopics(@AuthenticationPrincipal UserPrincipal principal) {
        return topicService.listTopics(principal.getUser());
    }
}
