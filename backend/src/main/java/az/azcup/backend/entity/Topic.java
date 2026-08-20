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

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
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

    // Mövzunun başlığı.
    @Column(nullable = false)
    private String title = "";

    // Məsələn "Ay 1", "Ay 2" kimi qruplaşdırma etiketi — sırf görüntü məqsədlidir.
    private String monthTag;

    // Mövzunun qısa təsviri.
    @Column(length = 2000)
    private String description;

    // Müəllim mövzunu "dərc" etməyənə qədər şagirdlər ona giriş əldə edə bilmir
    // (bax: TopicService-dəki giriş nəzarəti). Bu, məzmunu tədricən açmaq
    // (müəllimin nəzarəti altında) üçün istifadə olunur.
    @Column(nullable = false)
    private boolean published = false;

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // slug sahəsinin dəyərini qaytarır.
    public String getSlug() {
        return slug;
    }

    // slug sahəsinə yeni dəyər təyin edir.
    public void setSlug(String slug) {
        this.slug = slug;
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

    // monthTag sahəsinin dəyərini qaytarır.
    public String getMonthTag() {
        return monthTag;
    }

    // monthTag sahəsinə yeni dəyər təyin edir.
    public void setMonthTag(String monthTag) {
        this.monthTag = monthTag;
    }

    // description sahəsinin dəyərini qaytarır.
    public String getDescription() {
        return description;
    }

    // description sahəsinə yeni dəyər təyin edir.
    public void setDescription(String description) {
        this.description = description;
    }

    // published sahəsinin dəyərini qaytarır. Boolean sahə olduğu üçün getter
    // "get" əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isPublished() {
        return published;
    }

    // published sahəsinə yeni dəyər təyin edir.
    public void setPublished(boolean published) {
        this.published = published;
    }
}
