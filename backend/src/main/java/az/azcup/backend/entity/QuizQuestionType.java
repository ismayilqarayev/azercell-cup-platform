package az.azcup.backend.entity;

// Test sualının növü (DİM informatika blokundakı avtomatik yoxlanan tapşırıq tipləri).
public enum QuizQuestionType {
    // Qapalı sual: 2–6 variantdan BİR doğru cavab seçilir (DİM-də 5 variant).
    CHOICE,
    // Kodlu sual: cavab ədəd (və ya qısa mətn) kimi yazılır; yoxlama normallaşdırılmış müqayisə ilədir (bax: QuizGrader).
    NUMERIC
}
