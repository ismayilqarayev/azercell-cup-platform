package az.azcup.backend.controller;

import az.azcup.backend.dto.live.LiveCodeUpdateRequest;
import az.azcup.backend.dto.live.LiveSessionDto;
import az.azcup.backend.dto.live.LiveSessionStateDto;
import az.azcup.backend.service.LiveSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Onlayn dərs zamanı müəllim/şagird arasında canlı kod güzgüləməsi üçün
// endpoint-lər — bax: LiveSessionService (sadə "polling" mexanizmi, WebSocket
// YOX). Sessiya kodunu BİLƏN istənilən giriş etmiş istifadəçi ona qoşula
// bilər (əlavə "üzvlük" yoxlaması yoxdur) — qısa kod özü artıq bir növ
// giriş açarıdır (müəllim onu yalnız öz şagirdinə söz ilə deyir).
@RestController
@RequestMapping("/api/live/sessions")
public class LiveSessionController {

    // Bütün faktiki sessiya idarəetməsini yerinə yetirən servis.
    private final LiveSessionService liveSessionService;

    // Spring tərəfindən inject olunan LiveSessionService-i sahəyə təyin edir.
    public LiveSessionController(LiveSessionService liveSessionService) {
        this.liveSessionService = liveSessionService;
    }

    // Yeni canlı dərs sessiyası yaradır — yalnız müəllim/admin başlada bilər.
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<LiveSessionDto> createSession() {
        return ResponseEntity.status(HttpStatus.CREATED).body(liveSessionService.createSession());
    }

    // Sessiyanın hazırkı vəziyyətini qaytarır — hər iki tərəf bunu tez-tez
    // (poll edərək) çağırır ki, digərinin kodundakı dəyişikliyi görsün.
    @GetMapping("/{code}")
    public LiveSessionStateDto getState(@PathVariable String code) {
        return liveSessionService.getState(code);
    }

    // Müəllim panelinin kodunu yeniləyir.
    @PutMapping("/{code}/teacher")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public LiveSessionStateDto updateTeacherCode(@PathVariable String code, @RequestBody LiveCodeUpdateRequest request) {
        return liveSessionService.updateTeacherCode(code, request.getSourceCode());
    }

    // Şagird panelinin kodunu yeniləyir.
    @PutMapping("/{code}/student")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public LiveSessionStateDto updateStudentCode(@PathVariable String code, @RequestBody LiveCodeUpdateRequest request) {
        return liveSessionService.updateStudentCode(code, request.getSourceCode());
    }
}
