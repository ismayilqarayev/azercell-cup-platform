package az.azcup.backend.seed;

import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// SeedLoader-in "tətbiq artıq işləyib" yolunu (backfillEmptyTopics) yoxlayır — bax: SeedLoader-in sinif-üstü şərhi.
// @SpringBootTest konteksti açılarkən SeedLoader.run() bir dəfə ÖZÜ İŞLƏYİB (ilk boot, topic cədvəli boş idi) və
// bütün seed-data.json məzmununu (week1-7) artıq yazıb. Testlər bu vəziyyət üzərində "təkrar boot"u simulyasiya edir.
@SpringBootTest
@ActiveProfiles("test")
class SeedLoaderBackfillTest {

    @Autowired
    private SeedLoader seedLoader;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ProblemRepository problemRepository;

    // Təkrar boot ƏN VACİB təhlükəsizlik şərti: artıq problemi olan mövzulara TOXUNULMAMALI
    // (nə təkrarlanma, nə silinmə) — admin/müəllimin ora etdiyi dəyişikliklər qorunmalıdır.
    @Test
    void secondRunIsANoOpForTopicsThatAlreadyHaveProblems() throws IOException {
        long totalBefore = problemRepository.count();
        Topic week1 = topicRepository.findBySlug("week1");
        long week1CountBefore = problemRepository.countByTopic(week1);
        assertThat(week1CountBefore).isGreaterThan(0);

        seedLoader.run();

        assertThat(problemRepository.count()).isEqualTo(totalBefore);
        assertThat(problemRepository.countByTopic(week1)).isEqualTo(week1CountBefore);
    }

    // Həqiqi "backfill" ssenarisi: tətbiqin köhnə versiyası bu mövzunu hələ bilmirdi (DB-də
    // mövzu var, amma 0 problemi var) — yeni kod deploy olunub yenidən başladılanda seed-data.json-dakı
    // problemlər ora yazılmalı və mövzu dərc olunmalıdır (publish), amma başqa heç bir mövzuya toxunulmamalıdır.
    @Test
    void backfillsATopicThatHadNoProblemsYetWithoutTouchingOthers() throws IOException {
        Topic week7 = topicRepository.findBySlug("week7");
        assertThat(week7).isNotNull();
        assertThat(problemRepository.countByTopic(week7)).isEqualTo(100L); // ilk boot artıq doldurub

        // "Köhnə deploy" vəziyyətini simulyasiya edirik: bu mövzunun problemlərini silib, mövzunu dərc olunmamış edirik.
        List<Problem> existing = problemRepository.findAllByTopicOrderByOrderIndexAsc(week7);
        problemRepository.deleteAll(existing);
        week7.setPublished(false);
        topicRepository.save(week7);
        assertThat(problemRepository.countByTopic(week7)).isZero();

        Topic week1Before = topicRepository.findBySlug("week1");
        long week1CountBefore = problemRepository.countByTopic(week1Before);

        seedLoader.run(); // "yeni deploy, tətbiq yenidən başlayır" simulyasiyası

        Topic week7After = topicRepository.findBySlug("week7");
        assertThat(problemRepository.countByTopic(week7After)).isEqualTo(100L);
        assertThat(week7After.isPublished()).isTrue();
        // week1-ə toxunulmayıb.
        assertThat(problemRepository.countByTopic(topicRepository.findBySlug("week1"))).isEqualTo(week1CountBefore);
    }
}
