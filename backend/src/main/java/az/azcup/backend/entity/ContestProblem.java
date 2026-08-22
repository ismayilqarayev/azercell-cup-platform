package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Bir yarışa aid, şagirdin C++ kodu yazıb göndərəcəyi konkret məsələni
// təmsil edir. Adi (təcrübə) Problem entity-sindən BİLƏRƏKDƏN ayrıdır —
// çünki yarış məsələləri GİZLİ test halları tələb edir (bax:
// ContestTestCase), Problem isə tək bir nümunə giriş/çıxışla işləyən köhnə
// "practice" yoxlama axınına bağlıdır. İkisini qarışdırmamaq, 800-dən çox
// mövcud Problem sətrinə və onların yoxlama məntiqinə TOXUNMADAN yeni
// funksionallıq əlavə etməyə imkan verir.
@Entity
@Table(name = "contest_problem")
public class ContestProblem {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu məsələnin aid olduğu yarış.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    // Yarış daxilində məsələlərin göstərilmə sırası.
    @Column(nullable = false)
    private int orderIndex = 0;

    // Məsələnin başlığı.
    @Column(nullable = false)
    private String title = "";

    // Məsələnin tam mətni.
    @Column(nullable = false, length = 4000)
    private String statement = "";

    // Giriş formatının izahı.
    @Column(length = 2000)
    private String inputSpec;

    // Çıxış formatının izahı.
    @Column(length = 2000)
    private String outputSpec;

    // Bu məsələnin düzgün həllinə görə şagirdə veriləcək bal — "hamısı ya
    // heç nə" prinsipi ilə: bütün gizli test halları keçilərsə TAM bal,
    // əks halda 0 (qismən bal yoxdur, bax: ContestSubmissionService).
    @Column(nullable = false)
    private int points = 100;

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // contest sahəsinin dəyərini qaytarır.
    public Contest getContest() {
        return contest;
    }

    // contest sahəsinə yeni dəyər təyin edir.
    public void setContest(Contest contest) {
        this.contest = contest;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public int getOrderIndex() {
        return orderIndex;
    }

    // orderIndex sahəsinə yeni dəyər təyin edir.
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // title sahəsinə yeni dəyər təyin edir.
    public void setTitle(String title) {
        this.title = title;
    }

    // statement sahəsinin dəyərini qaytarır.
    public String getStatement() {
        return statement;
    }

    // statement sahəsinə yeni dəyər təyin edir.
    public void setStatement(String statement) {
        this.statement = statement;
    }

    // inputSpec sahəsinin dəyərini qaytarır.
    public String getInputSpec() {
        return inputSpec;
    }

    // inputSpec sahəsinə yeni dəyər təyin edir.
    public void setInputSpec(String inputSpec) {
        this.inputSpec = inputSpec;
    }

    // outputSpec sahəsinin dəyərini qaytarır.
    public String getOutputSpec() {
        return outputSpec;
    }

    // outputSpec sahəsinə yeni dəyər təyin edir.
    public void setOutputSpec(String outputSpec) {
        this.outputSpec = outputSpec;
    }

    // points sahəsinin dəyərini qaytarır.
    public int getPoints() {
        return points;
    }

    // points sahəsinə yeni dəyər təyin edir.
    public void setPoints(int points) {
        this.points = points;
    }
}
