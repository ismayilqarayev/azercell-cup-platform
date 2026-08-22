package az.azcup.backend.dto.contest;

// Bir yarışın CARİ vəziyyəti — bazada AYRICA sütun kimi saxlanılmır, hər
// sorğuda Contest.startTime/endTime-a görə HESABLANIR (bax:
// ContestService.computeStatus), ona görə heç vaxt "köhnəlmiş" ola bilməz.
public enum ContestStatusEnum {
    // İndiki vaxt hələ startTime-dan əvvəldir.
    UPCOMING,
    // İndiki vaxt startTime və endTime arasındadır.
    ACTIVE,
    // İndiki vaxt endTime-dan sonradır.
    ENDED
}
