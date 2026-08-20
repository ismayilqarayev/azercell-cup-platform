package az.azcup.backend.seed;

import java.util.List;
import java.util.Objects;

// seed-data.json faylının ən üst səviyyəli (kök) strukturu — bütün
// mövzuları və problemləri bir yerdə saxlayır. SeedLoader bunu tək
// bir ObjectMapper.readValue() çağırışı ilə tam oxuyur.
public class SeedData {

    private final List<SeedTopic> topics;
    private final List<SeedProblem> problems;

    public SeedData(List<SeedTopic> topics, List<SeedProblem> problems) {
        this.topics = topics;
        this.problems = problems;
    }

    public List<SeedTopic> getTopics() {
        return topics;
    }

    public List<SeedProblem> getProblems() {
        return problems;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(topics, problems);
    }

    @Override
    public String toString() {
        return "SeedData{" +
            "topics=" + topics +
            ", problems=" + problems +
            '}';
    }
}
