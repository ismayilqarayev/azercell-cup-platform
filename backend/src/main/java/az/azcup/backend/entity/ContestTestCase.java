package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Bir ContestProblem-ə aid TƏK BİR test halını (giriş + gözlənilən çıxış)
// təmsil edir. Bir məsələnin bir neçə ContestTestCase sətri ola bilər —
// ContestSubmissionService şagirdin kodunu HAMISINA qarşı işlədir (bax:
// JudgeService.judgeMultiple). "hidden" sahəsi ilə bəziləri şagirdə nümunə
// kimi göstərilir (hidden=false), qalanları isə gizli saxlanılır ki, şagird
// kodunu sadəcə "nümunə çıxışı təqlid edərək" aldada bilməsin.
@Entity
@Table(name = "contest_test_case")
public class ContestTestCase {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu test halının aid olduğu yarış məsələsi.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_problem_id", nullable = false)
    private ContestProblem contestProblem;

    // Test hallarının icra/göstərilmə sırası — birinci uğursuz olan test
    // halının nömrəsini şagirdə göstərmək üçün istifadə olunur.
    @Column(nullable = false)
    private int orderIndex = 0;

    // Test halının girişi (stdin).
    @Lob
    @Column(nullable = false)
    private String input = "";

    // Bu girişə uyğun gözlənilən düzgün çıxış.
    @Lob
    @Column(nullable = false)
    private String expectedOutput = "";

    // false olduqda bu test halı NÜMUNƏ kimi şagirdə göstərilir (məsələn
    // problemin "Nümunə giriş/çıxış" bölməsində). true olduqda isə GİZLİDİR —
    // yalnız yoxlama zamanı istifadə olunur, şagird onun məzmununu görmür.
    @Column(nullable = false)
    private boolean hidden = true;

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // contestProblem sahəsinin dəyərini qaytarır.
    public ContestProblem getContestProblem() {
        return contestProblem;
    }

    // contestProblem sahəsinə yeni dəyər təyin edir.
    public void setContestProblem(ContestProblem contestProblem) {
        this.contestProblem = contestProblem;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public int getOrderIndex() {
        return orderIndex;
    }

    // orderIndex sahəsinə yeni dəyər təyin edir.
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    // input sahəsinin dəyərini qaytarır.
    public String getInput() {
        return input;
    }

    // input sahəsinə yeni dəyər təyin edir.
    public void setInput(String input) {
        this.input = input;
    }

    // expectedOutput sahəsinin dəyərini qaytarır.
    public String getExpectedOutput() {
        return expectedOutput;
    }

    // expectedOutput sahəsinə yeni dəyər təyin edir.
    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    // hidden sahəsinin dəyərini qaytarır.
    public boolean isHidden() {
        return hidden;
    }

    // hidden sahəsinə yeni dəyər təyin edir.
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
