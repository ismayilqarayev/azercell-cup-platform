package az.azcup.backend.controller

import az.azcup.backend.dto.TopicDto
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.TopicService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/topics")
class TopicController(private val topicService: TopicService) {

    @GetMapping
    fun listTopics(@AuthenticationPrincipal principal: UserPrincipal): List<TopicDto> =
        topicService.listTopics(principal.user)
}
