package az.azcup.backend.service;

import az.azcup.backend.dto.live.LiveSessionDto;
import az.azcup.backend.dto.live.LiveSessionStateDto;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.live.LiveSessionState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Onlayn dərs zamanı müəllim/şagird arasında CANLI kod güzgüləməsini idarə
// edir: müəllim sessiya yaradır, qısa kodu şagirdə söz ilə deyir, hər ikisi
// öz tərəfini yazır və digərinin yazdığını (bir neçə saniyəlik gecikmə ilə,
// "polling" üsulu ilə) görür. WebSocket YOX, sadə REST + tez-tez sorğu
// seçilib — bir dərs kontekstində 1 saniyəlik gecikmə hiss olunmur, amma
// WebSocket-in gətirəcəyi əlavə infrastruktur mürəkkəbliyi (yeni asılılıq,
// Render-in pulsuz planında uzunmüddətli bağlantı riski) tamamilə aradan qalxır.
//
// Sessiyalar YALNIZ YADDAŞDA saxlanılır (bax: LiveSessionState) — server
// yenidən başladılanda hamısı itir, bu, gözlənilən və qəbul edilən davranışdır
// (canlı dərs sessiyası zatən keçicidir, daimi saxlanmasına ehtiyac yoxdur).
@Service
public class LiveSessionService {

    // Böyük I/O, kiçik l və rəqəm 0/1 kimi vizual cəhətdən qarışdırıla bilən
    // simvollar BİLƏRƏKDƏN çıxarılıb (bax: AdminService.PASSWORD_CHARS, eyni səbəb) —
    // müəllim kodu şagirdə SƏSLİ deyəcək, ona görə aydın oxunmalıdır.
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    // Bu müddətdən artıq heç bir tərəf kod yeniləməyibsə, sessiya "tərk edilmiş"
    // sayılıb yaddaşdan silinir (bax: cleanupExpiredSessions).
    private static final Duration EXPIRY = Duration.ofHours(3);

    // Aktiv sessiyaların kod → vəziyyət xəritəsi. ConcurrentHashMap seçilib
    // ki, eyni anda bir neçə HTTP sorğusu (müəllim + şagird eyni vaxtda kod
    // göndərəndə) təhlükəsiz işləsin.
    private final Map<String, LiveSessionState> sessions = new ConcurrentHashMap<>();

    // Kod yaratmaq üçün — java.util.Random YOX, SecureRandom (bax:
    // AdminService-dəki eyni qərar və izah).
    private final SecureRandom secureRandom = new SecureRandom();

    // Yeni canlı dərs sessiyası yaradır və müəllimin şagirdə deyəcəyi qısa kodu qaytarır.
    public LiveSessionDto createSession() {
        String code = generateUniqueCode();
        sessions.put(code, new LiveSessionState(code));
        return new LiveSessionDto(code);
    }

    // Bir sessiyanın hazırkı vəziyyətini qaytarır (poll üçün istifadə olunur).
    public LiveSessionStateDto getState(String code) {
        return toDto(getOrThrow(code));
    }

    // Müəllim panelinin kodunu yeniləyir.
    public LiveSessionStateDto updateTeacherCode(String code, String sourceCode) {
        LiveSessionState state = getOrThrow(code);
        state.setTeacherCode(normalize(sourceCode));
        state.touch();
        return toDto(state);
    }

    // Şagird panelinin kodunu yeniləyir.
    public LiveSessionStateDto updateStudentCode(String code, String sourceCode) {
        LiveSessionState state = getOrThrow(code);
        state.setStudentCode(normalize(sourceCode));
        state.touch();
        return toDto(state);
    }

    // Hər 30 dəqiqədən bir işə düşür və 3 saatdan artıq toxunulmamış (dərsi
    // bitmiş) sessiyaları yaddaşdan təmizləyir — əks halda server heç vaxt
    // dayanmayan sessiyalarla yavaş-yavaş yaddaş dolduracaqdı.
    @Scheduled(fixedRate = 30 * 60 * 1000L)
    public void cleanupExpiredSessions() {
        Instant cutoff = Instant.now().minus(EXPIRY);
        sessions.values().removeIf(state -> state.getLastActivity().isBefore(cutoff));
    }

    // null göndərilibsə boş sətrə çevirir (sourceCode DB-dəki kimi @Lob deyil,
    // sadə String olduğu üçün null-a icazə vermək əvəzinə hər yerdə eyni qaydanı saxlayırıq).
    private String normalize(String sourceCode) {
        if (sourceCode == null) {
            return "";
        }
        return sourceCode;
    }

    // Kodu böyük hərflərə çevirib xəritədə axtarır, tapılmazsa 404 atır.
    private LiveSessionState getOrThrow(String code) {
        LiveSessionState state = sessions.get(code.toUpperCase());
        if (state == null) {
            throw new NotFoundException("Sessiya tapılmadı: " + code);
        }
        return state;
    }

    // Toqquşma ehtimalı sıfıra endirilənə qədər təsadüfi kod yaradır (6
    // simvollu kod fəzası kifayət qədər böyükdür ki, praktiki olaraq bir
    // dəfədən artıq təkrar cəhd demək olar heç vaxt lazım olmasın).
    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(secureRandom.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (sessions.containsKey(code));
        return code;
    }

    // LiveSessionState-i LiveSessionStateDto-ya çevirir.
    private LiveSessionStateDto toDto(LiveSessionState state) {
        return new LiveSessionStateDto(state.getCode(), state.getTeacherCode(), state.getStudentCode(), state.getLastActivity());
    }
}
