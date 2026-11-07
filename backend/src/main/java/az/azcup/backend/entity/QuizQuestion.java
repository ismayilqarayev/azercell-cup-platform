package az.azcup.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

// Testin bir sualı. CHOICE üçün variantlar + düzgün variantın indeksi, NUMERIC üçün qəbul olunan cavab(lar) saxlanılır.
// DİQQƏT: düzgün cavab (correctIndex/correctText) və izah şagirdə YALNIZ təhvil verəndən sonra göndərilir (bax: QuizService).
@Entity
@Table(name = "quiz_question")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Testdəki sıra (0-dan başlayır).
    @Column(nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private QuizQuestionType type = QuizQuestionType.CHOICE;

    // Sualın mətni.
    @Column(nullable = false, columnDefinition = "text")
    private String prompt = "";

    // Sualla birlikdə göstərilən kod/cədvəl parçası (monoşrift blokunda), istəyə bağlı.
    @Column(columnDefinition = "text")
    private String code;

    // CHOICE variantları (NUMERIC üçün boş). Sıra OrderColumn ilə qorunur.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "quiz_question_option", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "option_idx")
    @Column(name = "option_text", length = 500)
    @BatchSize(size = 50)
    private List<String> options = new ArrayList<>();

    // CHOICE: düzgün variantın indeksi.
    @Column
    private Integer correctIndex;

    // NUMERIC: düzgün cavab; bir neçə qəbul olunan yazılış "|" ilə ayrılır (məs. "12|12.0").
    @Column(length = 200)
    private String correctText;

    // Cavabdan sonra göstərilən izah (istəyə bağlı).
    @Column(columnDefinition = "text")
    private String explanation;

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public QuizQuestionType getType() {
        return type;
    }

    public void setType(QuizQuestionType type) {
        this.type = type;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getOptions() {
        return options;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getCorrectText() {
        return correctText;
    }

    public void setCorrectText(String correctText) {
        this.correctText = correctText;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
