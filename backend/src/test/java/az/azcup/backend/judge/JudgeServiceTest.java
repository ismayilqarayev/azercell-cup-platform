package az.azcup.backend.judge;

import az.azcup.backend.entity.Difficulty;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

// JudgeService-in compile+icra+müqayisə davranışını real g++ kompilyatoru
// ilə (mock YOX, faktiki proses işə salınaraq) sınayan test dəsti.
class JudgeServiceTest {

    // JUnit hər testdən əvvəl yaradıb sonra avtomatik silən müvəqqəti qovluq —
    // JudgeService-in workspace-dir-i kimi istifadə olunur ki, testlər real
    // fayl sistemini çirkləndirməsin.
    @TempDir
    Path workspace;

    // Standart (10s compile / 3s run) limitlərlə yeni bir JudgeService nümunəsi yaradır.
    private JudgeService newService() {
        return new JudgeService("g++", workspace.toString(), 10, 3, 20000, 65536);
    }

    // Testlərdə istifadə olunacaq minimal, sadə bir Problem obyekti qurur —
    // yalnız nümunə giriş/çıxış sahələri əhəmiyyətlidir, qalanları formallıq üçündür.
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

    // Düzgün cavab verən kod ACCEPTED statusu almalıdır.
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

    // Compile olan, amma yanlış nəticə verən kod WRONG_ANSWER statusu almalıdır.
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

    // Sintaktik cəhətdən yanlış (compile olunmayan) kod COMPILE_ERROR statusu
    // almalı və g++-ın stderr çıxışı boş olmamalıdır.
    @Test
    void reportsCompileError() {
        String source = "int main( { this is not valid c++ }";
        JudgeResult result = newService().judge(source, problem("17 5", "22"));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILE_ERROR);
        assertThat(result.getStderr()).isNotBlank();
    }

    // Sonsuz dövrlü kod, run timeout-u bitdikdən sonra məcburi dayandırılıb
    // TIME_LIMIT_EXCEEDED statusu qaytarmalıdır (bu test üçün 1 saniyəlik
    // qısa timeout-lu ayrıca JudgeService nümunəsi istifadə olunur).
    @Test
    void killsInfiniteLoopAsTimeLimitExceeded() {
        String source = """
            int main(){ while(true){} return 0; }
            """;
        JudgeService service = new JudgeService("g++", workspace.toString(), 10, 1, 20000, 65536);
        JudgeResult result = service.judge(source, problem("", ""));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED);
    }

    // Çıxışın sonunda əlavə boş sətirlər olsa belə, normalize() sayəsində
    // məzmunca eyni nəticə ACCEPTED kimi qəbul edilməlidir.
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

    // judgeMultiple: bütün test halları keçilirsə ACCEPTED, passedTestCases
    // totalTestCases-a bərabər olmalı, firstFailedTestCaseOrder null qalmalıdır.
    @Test
    void judgeMultipleAcceptsWhenAllTestCasesPass() {
        String source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ long long a,b; cin>>a>>b; cout<<a+b<<"\\n"; }
            """;
        MultiJudgeResult result = newService().judgeMultiple(source, java.util.List.of(
            new TestCaseInput(0, "2 3", "5"),
            new TestCaseInput(1, "10 20", "30")
        ));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(result.getPassedTestCases()).isEqualTo(2);
        assertThat(result.getTotalTestCases()).isEqualTo(2);
        assertThat(result.getFirstFailedTestCaseOrder()).isNull();
    }

    // judgeMultiple: ikinci test halı uğursuz olduqda, dövr ORADA
    // DAYANMALI (short-circuit) — passedTestCases 1 olmalı (yalnız birinci
    // test keçib), firstFailedTestCaseOrder ikinci testin sırasını (1) göstərməlidir.
    @Test
    void judgeMultipleStopsAtFirstFailingTestCase() {
        String source = """
            #include <bits/stdc++.h>
            using namespace std;
            int main(){ long long a,b; cin>>a>>b; cout<<a+b<<"\\n"; }
            """;
        MultiJudgeResult result = newService().judgeMultiple(source, java.util.List.of(
            new TestCaseInput(0, "2 3", "5"),
            new TestCaseInput(1, "10 20", "999")  // yanlış gözlənilən nəticə — bu test uğursuz olacaq
        ));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
        assertThat(result.getPassedTestCases()).isEqualTo(1);
        assertThat(result.getTotalTestCases()).isEqualTo(2);
        assertThat(result.getFirstFailedTestCaseOrder()).isEqualTo(1);
    }

    // judgeMultiple: kompilyasiya xətası olduqda, heç bir test icra
    // OLUNMADAN COMPILE_ERROR qaytarılmalıdır.
    @Test
    void judgeMultipleReportsCompileError() {
        String source = "int main( { this is not valid c++ }";
        MultiJudgeResult result = newService().judgeMultiple(source, java.util.List.of(
            new TestCaseInput(0, "2 3", "5")
        ));
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILE_ERROR);
        assertThat(result.getPassedTestCases()).isEqualTo(0);
        assertThat(result.getStderr()).isNotBlank();
    }
}
