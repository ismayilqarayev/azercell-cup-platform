package az.azcup.backend.seed;

import java.util.List;
import java.util.Objects;

// seed-data.json faylının ən üst səviyyəli (kök) strukturu — bütün
// mövzuları və problemləri bir yerdə saxlayır. SeedLoader bunu tək
// bir ObjectMapper.readValue() çağırışı ilə tam oxuyur.
public class SeedData {

    // JSON-dakı bütün mövzuların siyahısı.
    private final List<SeedTopic> topics;
    // JSON-dakı bütün problemlərin siyahısı.
    private final List<SeedProblem> problems;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public SeedData(List<SeedTopic> topics, List<SeedProblem> problems) {
        this.topics = topics;
        this.problems = problems;
    }

    // topics sahəsinin dəyərini qaytarır.
    public List<SeedTopic> getTopics() {
        return topics;
    }

    // problems sahəsinin dəyərini qaytarır.
    public List<SeedProblem> getProblems() {
        return problems;
    }

    // İki SeedData obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeedData that = (SeedData) o;
        return Objects.equals(topics, that.topics) && Objects.equals(problems, that.problems);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(topics, problems);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "SeedData{" +
            "topics=" + topics +
            ", problems=" + problems +
            '}';
    }
}
