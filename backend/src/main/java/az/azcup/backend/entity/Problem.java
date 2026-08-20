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
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

// Bir mövzuya aid, şagirdin C++ kodu yazıb göndərəcəyi konkret məsələni
// təmsil edir. exampleInput/exampleOutput cütü JudgeService tərəfindən
// şagirdin kodunun düzgünlüyünü yoxlamaq üçün istifadə olunur.
@Entity
@Table(name = "problem")
public class Problem {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LAZY seçilib ki, problem siyahısı çəkiləndə hər dəfə əlaqəli Topic
    // sətri də avtomatik yüklənməsin (performans üçün) — yalnız lazım
    // olanda (topic.title və s. çağırılanda) ayrıca sorğu ilə gətirilir.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // Mövzu daxilində məsələlərin göstərilmə sırası.
    @Column(nullable = false)
    private int orderIndex = 0;

    // Bəzi mövzularda məsələlər alt-qruplara bölünür (məs. "For dövrü",
    // "If/Switch") — bu sahə həmin alt-qrupun adını saxlayır, boş ola bilər.
    private String subgroupLabel;

    // Məsələnin başlığı.
    @Column(nullable = false)
    private String title = "";

    // Məsələnin çətinlik səviyyəsi (EASY/MID/HARD) — bax: Difficulty enum-u.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    // Məsələnin mövzu etiketləri (məs. "massiv", "dp"). EAGER seçilib, çünki
    // etiketlər adətən problem ilə birlikdə dərhal göstərilir (ayrıca sorğuya
    // ehtiyac qalmasın deyə). BatchSize isə eyni anda çoxlu problem yüklənəndə
    // hər biri üçün ayrı-ayrı sorğu əvəzinə TOPLU sorğu istifadə etməyə imkan verir.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_tags", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "tag")
    @BatchSize(size = 50)
    private List<String> tags = new ArrayList<>();

    // Məsələnin tam mətni (şagirdə göstərilən əsas izahat).
    @Column(nullable = false, length = 4000)
    private String statement = "";

    // Giriş formatının izahı (məsələn "İlk sətirdə N ədədi verilir...").
    @Column(length = 2000)
    private String inputSpec;

    // Çıxış formatının izahı.
    @Column(length = 2000)
    private String outputSpec;

    // Şagirdə göstərilən nümunə giriş/çıxış. Eyni zamanda JudgeService bu
    // dəyərləri şagirdin göndərdiyi kodun stdout-u ilə müqayisə edərək
    // ACCEPTED/WRONG_ANSWER qərarını verir (bax: JudgeService.runAgainstInput).
    @Column(nullable = false, length = 4000)
    private String exampleInput = "";

    // Nümunə girişə uyğun gözlənilən çıxış.
    @Column(nullable = false, length = 4000)
    private String exampleOutput = "";

    // Məsələnin həll yanaşmasının qısa izahı — şagirdə ipucu kimi göstərilir.
    @Column(length = 4000)
    private String approach;

    // Müəllim/admin üçün nümunə həll kodu — şagirdlərə göstərilmir.
    @Column(length = 8000)
    private String referenceSolution;

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // topic sahəsinin dəyərini qaytarır.
    public Topic getTopic() {
        return topic;
    }

    // topic sahəsinə yeni dəyər təyin edir.
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public int getOrderIndex() {
        return orderIndex;
    }

    // orderIndex sahəsinə yeni dəyər təyin edir.
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    // subgroupLabel sahəsinin dəyərini qaytarır.
    public String getSubgroupLabel() {
        return subgroupLabel;
    }

    // subgroupLabel sahəsinə yeni dəyər təyin edir.
    public void setSubgroupLabel(String subgroupLabel) {
        this.subgroupLabel = subgroupLabel;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // title sahəsinə yeni dəyər təyin edir.
    public void setTitle(String title) {
        this.title = title;
    }

    // difficulty sahəsinin dəyərini qaytarır.
    public Difficulty getDifficulty() {
        return difficulty;
    }

    // difficulty sahəsinə yeni dəyər təyin edir.
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    // tags sahəsinin dəyərini qaytarır.
    public List<String> getTags() {
        return tags;
    }

    // tags sahəsinə yeni dəyər təyin edir.
    public void setTags(List<String> tags) {
        this.tags = tags;
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

    // exampleInput sahəsinin dəyərini qaytarır.
    public String getExampleInput() {
        return exampleInput;
    }

    // exampleInput sahəsinə yeni dəyər təyin edir.
    public void setExampleInput(String exampleInput) {
        this.exampleInput = exampleInput;
    }

    // exampleOutput sahəsinin dəyərini qaytarır.
    public String getExampleOutput() {
        return exampleOutput;
    }

    // exampleOutput sahəsinə yeni dəyər təyin edir.
    public void setExampleOutput(String exampleOutput) {
        this.exampleOutput = exampleOutput;
    }

    // approach sahəsinin dəyərini qaytarır.
    public String getApproach() {
        return approach;
    }

    // approach sahəsinə yeni dəyər təyin edir.
    public void setApproach(String approach) {
        this.approach = approach;
    }

    // referenceSolution sahəsinin dəyərini qaytarır.
    public String getReferenceSolution() {
        return referenceSolution;
    }

    // referenceSolution sahəsinə yeni dəyər təyin edir.
    public void setReferenceSolution(String referenceSolution) {
        this.referenceSolution = referenceSolution;
    }
}
