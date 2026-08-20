package az.azcup.backend.judge;

import az.azcup.backend.entity.Difficulty;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class JudgeServiceTest {

    @TempDir
    Path workspace;

    private JudgeService newService() {
        return new JudgeService("g++", workspace.toString(), 10, 3, 20000, 65536);
    }

    private Problem problem(String exampleInput, String exampleOutput) {
        Problem p = new Problem();
        p.setId(1L);
        p.setOrderIndex(1);
        p.setTitle("İki ədədin cəmi");
        p.setDifficulty(Difficulty.EASY);
        p.setStatement("Cəm");
        p.setExampleInput(exampleInput);
        p.setExampleOutput(exampleOutput);
        return p;
    }

    @Test
    void acceptsCorrectSolution() {
        String source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ long long a,b; cin>>a>>b; cout<<a+b<<"\\n"; }
            """;
        JudgeResult result = newService().judge(source, problem("17 5", "22"));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void rejectsWrongOutput() {
        String source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ long long a,b; cin>>a>>b; cout<<a-b<<"\\n"; }
            """;
        JudgeResult result = newService().judge(source, problem("17 5", "22"));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    @Test
    void reportsCompileError() {
        String source = "int main( { this is not valid c++ }";
        JudgeResult result = newService().judge(source, problem("17 5", "22"));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILE_ERROR);
        assertThat(result.getStderr()).isNotBlank();
    }

    @Test
    void killsInfiniteLoopAsTimeLimitExceeded() {
        String source = """
            int main(){ while(true){} return 0; }
            """;
        JudgeService service = new JudgeService("g++", workspace.toString(), 10, 1, 20000, 65536);
        JudgeResult result = service.judge(source, problem("", ""));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED);
    }

    @Test
    void normalizesTrailingWhitespaceWhenComparing() {
        String source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ cout << "22" << "\\n" << "\\n"; }
            """;
        JudgeResult result = newService().judge(source, problem("", "22"));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
    }
}
