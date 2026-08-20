package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Roadmap-dakı bir mövzunu (məs. "Massivlər", "Qraflar") təmsil edir.
// Hər mövzunun öz problem-ləri var (bax: Problem.topic əlaqəsi).
@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Frontend-də URL-dostu identifikator kimi istifadə olunur (məs. "week1").
    // Unikal olmalıdır ki, hər mövzu birmənalı şəkildə tapıla bilsin.
    @Column(nullable = false, unique = true)
    private String slug = "";

    // Roadmap-da mövzuların hansı sırada göstəriləcəyini müəyyən edir.
    @Column(nullable = false)
    private int orderIndex = 0;

    @Column(nullable = false)
    private String title = "";

    // Məsələn "Ay 1", "Ay 2" kimi qruplaşdırma etiketi — sırf görüntü məqsədlidir.
    private String monthTag;

    @Column(length = 2000)
    private String description;

    // Müəllim mövzunu "dərc" etməyənə qədər şagirdlər ona giriş əldə edə bilmir
    // (bax: TopicService-dəki giriş nəzarəti). Bu, məzmunu tədricən açmaq
    // (müəllimin nəzarəti altında) üçün istifadə olunur.
    @Column(nullable = false)
    private boolean published = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMonthTag() {
        return monthTag;
    }

    public void setMonthTag(String monthTag) {
        this.monthTag = monthTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
