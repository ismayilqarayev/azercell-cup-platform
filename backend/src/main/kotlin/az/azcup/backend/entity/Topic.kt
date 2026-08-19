package az.azcup.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// Roadmap-dakı bir mövzunu (məs. "Massivlər", "Qraflar") təmsil edir.
// Hər mövzunun öz problem-ləri var (bax: Problem.topic əlaqəsi).
@Entity
@Table(name = "topic")
class Topic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // Frontend-də URL-dostu identifikator kimi istifadə olunur (məs. "week1").
    // Unikal olmalıdır ki, hər mövzu birmənalı şəkildə tapıla bilsin.
    @Column(nullable = false, unique = true)
    var slug: String = "",

    // Roadmap-da mövzuların hansı sırada göstəriləcəyini müəyyən edir.
    @Column(nullable = false)
    var orderIndex: Int = 0,

    @Column(nullable = false)
    var title: String = "",

    // Məsələn "Ay 1", "Ay 2" kimi qruplaşdırma etiketi — sırf görüntü məqsədlidir.
    var monthTag: String? = null,

    @Column(length = 2000)
    var description: String? = null,

    // Müəllim mövzunu "dərc" etməyənə qədər şagirdlər ona giriş əldə edə bilmir
    // (bax: TopicService-dəki giriş nəzarəti). Bu, məzmunu tədricən açmaq
    // (müəllimin nəzarəti altında) üçün istifadə olunur.
    @Column(nullable = false)
    var published: Boolean = false
)
