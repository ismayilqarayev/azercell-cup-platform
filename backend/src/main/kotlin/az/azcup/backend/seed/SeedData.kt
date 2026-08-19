package az.azcup.backend.seed

// seed-data.json faylının ən üst səviyyəli (kök) strukturu — bütün
// mövzuları və problemləri bir yerdə saxlayır. SeedLoader bunu tək
// bir ObjectMapper.readValue() çağırışı ilə tam oxuyur.
data class SeedData(
    val topics: List<SeedTopic>,
    val problems: List<SeedProblem>
)
