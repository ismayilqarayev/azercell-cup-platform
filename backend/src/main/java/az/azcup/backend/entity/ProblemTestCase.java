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

// Bir Problem-ə aid ƏLAVƏ, GİZLİ test halını (giriş + gözlənilən çıxış)
// təmsil edir — bax: ContestTestCase, eyni naxış. Fərq budur ki, burada
// "hidden" sahəsinə ehtiyac yoxdur: Problem.exampleInput/exampleOutput artıq
// HƏMİŞƏ göstərilən yeganə nümunədir, bu cədvəldəki sətirlərin HAMISI
// yoxlama zamanı istifadə olunan, lakin şagirdə HEÇ VAXT göstərilməyən
// əlavə testlərdir (bax: SubmissionService.submit — JudgeService.judgeMultiple
// çağırışı).
//
// DİQQƏT: bu cədvəl BOŞ ola bilər (əksər problemlər üçün belədir) — bu halda
// yoxlama sadəcə tək nümunə cütünə qarşı aparılır (əvvəlki davranışın eynisi).
// Admin panelindən tədricən əlavə test halları əlavə etməklə, "şagird
// sadəcə nümunə çıxışını hardcode edir" boşluğu bağlanır.
@Entity
@Table(name = "problem_test_case")
public class ProblemTestCase {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu test halının aid olduğu problem.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // Test hallarının icra sırası — hansı test halının ilk uğursuz olduğunu
    // şagirdə göstərmək üçün istifadə olunur (məzmununu açmadan).
    @Column(nullable = false)
    private int orderIndex = 0;

    // Test halının girişi (stdin) — boş ola bilər (bəzi məsələlərdə giriş yoxdur).
    @Lob
    @Column(nullable = false)
    private String input = "";

    // Bu girişə uyğun gözlənilən düzgün çıxış.
    @Lob
    @Column(nullable = false)
    private String expectedOutput = "";

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // problem sahəsinin dəyərini qaytarır.
    public Problem getProblem() {
        return problem;
    }

    // problem sahəsinə yeni dəyər təyin edir.
    public void setProblem(Problem problem) {
        this.problem = problem;
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
}
