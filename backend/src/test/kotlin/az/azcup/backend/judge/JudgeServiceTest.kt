package az.azcup.backend.judge

import az.azcup.backend.entity.Difficulty
import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.SubmissionStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class JudgeServiceTest {

    @TempDir
    lateinit var workspace: Path

    private fun newService(): JudgeService = JudgeService("g++", workspace.toString(), 10, 3, 20000, 65536)

    private fun problem(exampleInput: String, exampleOutput: String): Problem =
        Problem(
            id = 1L,
            orderIndex = 1,
            title = "İki ədədin cəmi",
            difficulty = Difficulty.EASY,
            statement = "Cəm",
            exampleInput = exampleInput,
            exampleOutput = exampleOutput
        )

    @Test
    fun acceptsCorrectSolution() {
        val source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ long long a,b; cin>>a>>b; cout<<a+b<<"\n"; }
        """.trimIndent()
        val result = newService().judge(source, problem("17 5", "22"))
        assertThat(result.status).isEqualTo(SubmissionStatus.ACCEPTED)
    }

    @Test
    fun rejectsWrongOutput() {
        val source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ long long a,b; cin>>a>>b; cout<<a-b<<"\n"; }
        """.trimIndent()
        val result = newService().judge(source, problem("17 5", "22"))
        assertThat(result.status).isEqualTo(SubmissionStatus.WRONG_ANSWER)
    }

    @Test
    fun reportsCompileError() {
        val source = "int main( { this is not valid c++ }"
        val result = newService().judge(source, problem("17 5", "22"))
        assertThat(result.status).isEqualTo(SubmissionStatus.COMPILE_ERROR)
        assertThat(result.stderr).isNotBlank()
    }

    @Test
    fun killsInfiniteLoopAsTimeLimitExceeded() {
        val source = """
            int main(){ while(true){} return 0; }
        """.trimIndent()
        val service = JudgeService("g++", workspace.toString(), 10, 1, 20000, 65536)
        val result = service.judge(source, problem("", ""))
        assertThat(result.status).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED)
    }

    @Test
    fun normalizesTrailingWhitespaceWhenComparing() {
        val source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ cout << "22" << "\n" << "\n"; }
        """.trimIndent()
        val result = newService().judge(source, problem("", "22"))
        assertThat(result.status).isEqualTo(SubmissionStatus.ACCEPTED)
    }
}
