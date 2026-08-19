package az.azcup.backend.entity

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize

// Bir mövzuya aid, şagirdin C++ kodu yazıb göndərəcəyi konkret məsələni
// təmsil edir. exampleInput/exampleOutput cütü JudgeService tərəfindən
// şagirdin kodunun düzgünlüyünü yoxlamaq üçün istifadə olunur.
@Entity
@Table(name = "problem")
class Problem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // LAZY seçilib ki, problem siyahısı çəkiləndə hər dəfə əlaqəli Topic
    // sətri də avtomatik yüklənməsin (performans üçün) — yalnız lazım
    // olanda (topic.title və s. çağırılanda) ayrıca sorğu ilə gətirilir.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    var topic: Topic? = null,

    // Mövzu daxilində məsələlərin göstərilmə sırası.
    @Column(nullable = false)
    var orderIndex: Int = 0,

    // Bəzi mövzularda məsələlər alt-qruplara bölünür (məs. "For dövrü",
    // "If/Switch") — bu sahə həmin alt-qrupun adını saxlayır, boş ola bilər.
    var subgroupLabel: String? = null,

    @Column(nullable = false)
    var title: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var difficulty: Difficulty? = null,

    // Məsələnin mövzu etiketləri (məs. "massiv", "dp"). EAGER seçilib, çünki
    // etiketlər adətən problem ilə birlikdə dərhal göstərilir (ayrıca sorğuya
    // ehtiyac qalmasın deyə). BatchSize isə eyni anda çoxlu problem yüklənəndə
    // hər biri üçün ayrı-ayrı sorğu əvəzinə TOPLU sorğu istifadə etməyə imkan verir.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_tags", joinColumns = [JoinColumn(name = "problem_id")])
    @Column(name = "tag")
    @BatchSize(size = 50)
    var tags: MutableList<String> = mutableListOf(),

    @Column(nullable = false, length = 4000)
    var statement: String = "",

    @Column(length = 2000)
    var inputSpec: String? = null,

    @Column(length = 2000)
    var outputSpec: String? = null,

    // Şagirdə göstərilən nümunə giriş/çıxış. Eyni zamanda JudgeService bu
    // dəyərləri şagirdin göndərdiyi kodun stdout-u ilə müqayisə edərək
    // ACCEPTED/WRONG_ANSWER qərarını verir (bax: JudgeService.runAgainstInput).
    @Column(nullable = false, length = 4000)
    var exampleInput: String = "",

    @Column(nullable = false, length = 4000)
    var exampleOutput: String = "",

    // Məsələnin həll yanaşmasının qısa izahı — şagirdə ipucu kimi göstərilir.
    @Column(length = 4000)
    var approach: String? = null,

    // Müəllim/admin üçün nümunə həll kodu — şagirdlərə göstərilmir.
    @Column(length = 8000)
    var referenceSolution: String? = null
)
