package az.azcup.backend.controller

import az.azcup.backend.dto.admin.AdminProblemDto
import az.azcup.backend.dto.admin.AdminTopicDto
import az.azcup.backend.dto.admin.AdminUserDetailDto
import az.azcup.backend.dto.admin.AdminUserDto
import az.azcup.backend.dto.admin.PasswordResetRequest
import az.azcup.backend.dto.admin.PasswordResetResponse
import az.azcup.backend.dto.admin.ProblemUpsertRequest
import az.azcup.backend.dto.admin.RoleUpdateRequest
import az.azcup.backend.dto.admin.StatusUpdateRequest
import az.azcup.backend.dto.admin.TopicUpsertRequest
import az.azcup.backend.dto.admin.UserProfileUpdateRequest
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.AdminService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminController(private val adminService: AdminService) {

    @PostMapping("/topics")
    fun createTopic(@Valid @RequestBody request: TopicUpsertRequest): ResponseEntity<AdminTopicDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(adminService.createTopic(request))

    @PutMapping("/topics/{id}")
    fun updateTopic(@PathVariable id: Long, @Valid @RequestBody request: TopicUpsertRequest): AdminTopicDto =
        adminService.updateTopic(id, request)

    @DeleteMapping("/topics/{id}")
    fun deleteTopic(@PathVariable id: Long): ResponseEntity<Void> {
        adminService.deleteTopic(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/problems")
    fun createProblem(@Valid @RequestBody request: ProblemUpsertRequest): ResponseEntity<AdminProblemDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(adminService.createProblem(request))

    @PutMapping("/problems/{id}")
    fun updateProblem(@PathVariable id: Long, @Valid @RequestBody request: ProblemUpsertRequest): AdminProblemDto =
        adminService.updateProblem(id, request)

    @DeleteMapping("/problems/{id}")
    fun deleteProblem(@PathVariable id: Long): ResponseEntity<Void> {
        adminService.deleteProblem(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/users")
    fun listStudents(): List<AdminUserDto> = adminService.listStudents()

    @GetMapping("/pending-teachers")
    fun listPendingTeachers(): List<AdminUserDto> = adminService.listPendingTeachers()

    @PostMapping("/teachers/{id}/approve")
    fun approveTeacher(@PathVariable id: Long): ResponseEntity<Void> {
        adminService.approveTeacher(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/teachers/{id}")
    fun rejectTeacher(@PathVariable id: Long): ResponseEntity<Void> {
        adminService.rejectTeacher(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/users/all")
    fun listAllUsers(): List<AdminUserDetailDto> = adminService.listAllUsers()

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): AdminUserDetailDto = adminService.getUser(id)

    @PutMapping("/users/{id}")
    fun updateUserProfile(
        @PathVariable id: Long,
        @Valid @RequestBody request: UserProfileUpdateRequest
    ): AdminUserDetailDto = adminService.updateProfile(id, request)

    @PutMapping("/users/{id}/role")
    fun changeUserRole(
        @PathVariable id: Long,
        @Valid @RequestBody request: RoleUpdateRequest,
        @AuthenticationPrincipal principal: UserPrincipal
    ): AdminUserDetailDto = adminService.changeRole(id, request.role!!, principal.user.id!!)

    @PutMapping("/users/{id}/status")
    fun changeUserStatus(
        @PathVariable id: Long,
        @RequestBody request: StatusUpdateRequest,
        @AuthenticationPrincipal principal: UserPrincipal
    ): AdminUserDetailDto = adminService.setActive(id, request.active, principal.user.id!!)

    @PostMapping("/users/{id}/reset-password")
    fun resetUserPassword(
        @PathVariable id: Long,
        @RequestBody(required = false) request: PasswordResetRequest?
    ): PasswordResetResponse {
        val newPassword = adminService.resetPassword(id, request?.newPassword)
        return PasswordResetResponse(newPassword)
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long, @AuthenticationPrincipal principal: UserPrincipal): ResponseEntity<Void> {
        adminService.deleteUser(id, principal.user.id!!)
        return ResponseEntity.noContent().build()
    }
}
