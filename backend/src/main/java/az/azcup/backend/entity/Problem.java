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

    @Column(nullable = false)
    private String title = "";

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

    @Column(nullable = false, length = 4000)
    private String statement = "";

    @Column(length = 2000)
    private String inputSpec;

    @Column(length = 2000)
    private String outputSpec;

    // Şagirdə göstərilən nümunə giriş/çıxış. Eyni zamanda JudgeService bu
    // dəyərləri şagirdin göndərdiyi kodun stdout-u ilə müqayisə edərək
    // ACCEPTED/WRONG_ANSWER qərarını verir (bax: JudgeService.runAgainstInput).
    @Column(nullable = false, length = 4000)
    private String exampleInput = "";

    @Column(nullable = false, length = 4000)
    private String exampleOutput = "";

    // Məsələnin həll yanaşmasının qısa izahı — şagirdə ipucu kimi göstərilir.
    @Column(length = 4000)
    private String approach;

    // Müəllim/admin üçün nümunə həll kodu — şagirdlərə göstərilmir.
    @Column(length = 8000)
    private String referenceSolution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getSubgroupLabel() {
        return subgroupLabel;
    }

    public void setSubgroupLabel(String subgroupLabel) {
        this.subgroupLabel = subgroupLabel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getInputSpec() {
        return inputSpec;
    }

    public void setInputSpec(String inputSpec) {
        this.inputSpec = inputSpec;
    }

    public String getOutputSpec() {
        return outputSpec;
    }

    public void setOutputSpec(String outputSpec) {
        this.outputSpec = outputSpec;
    }

    public String getExampleInput() {
        return exampleInput;
    }

    public void setExampleInput(String exampleInput) {
        this.exampleInput = exampleInput;
    }

    public String getExampleOutput() {
        return exampleOutput;
    }

    public void setExampleOutput(String exampleOutput) {
        this.exampleOutput = exampleOutput;
    }

    public String getApproach() {
        return approach;
    }

    public void setApproach(String approach) {
        this.approach = approach;
    }

    public String getReferenceSolution() {
        return referenceSolution;
    }

    public void setReferenceSolution(String referenceSolution) {
        this.referenceSolution = referenceSolution;
    }
}
