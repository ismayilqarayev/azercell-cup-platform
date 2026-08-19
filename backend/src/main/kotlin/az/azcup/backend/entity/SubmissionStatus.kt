package az.azcup.backend.entity

// Bir kod təqdimatının yoxlanış nəticəsi. JudgeService bu dəyərlərdən
// birini qaytarır:
//   PENDING             — hələ yoxlanılmayıb (hazırda demək olar istifadə olunmur,
//                          JudgeService sinxron işlədiyi üçün nəticə dərhal məlum olur)
//   ACCEPTED            — kod uğurla compile olub, icra olunub və çıxışı gözlənilənlə üst-üstə düşür
//   WRONG_ANSWER        — kod işləyib, amma çıxışı gözlənilən nəticədən fərqlidir
//   COMPILE_ERROR       — g++ kodu compile edə bilmədi
//   RUNTIME_ERROR       — kod icra zamanı sıfırdan fərqli çıxış kodu ilə bitdi (çöküş və s.)
//   TIME_LIMIT_EXCEEDED — kod vaxt limitində bitmədi (sonsuz dövr ehtimalı və s.)
enum class SubmissionStatus {
    PENDING, ACCEPTED, WRONG_ANSWER, COMPILE_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED
}
