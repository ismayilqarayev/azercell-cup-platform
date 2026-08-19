package az.azcup.backend.controller

import az.azcup.backend.dto.TopicDto
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.TopicService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Şagirdlərin roadmap (dərs proqramı) səhifəsində gördüyü mövzu siyahısı.
@RestController
@RequestMapping("/api/topics")
class TopicController(private val topicService: TopicService) {

    // STUDENT roluna yalnız dərc olunmuş (published=true) mövzular göstərilir,
    // TEACHER/ADMIN isə hamısını görür — bu qərar burada yox, TopicService-də
    // verilir (bax: TopicService.listTopics).
    @GetMapping
    fun listTopics(@AuthenticationPrincipal principal: UserPrincipal): List<TopicDto> =
        topicService.listTopics(principal.user)
}
