package az.azcup.backend.service;

import az.azcup.backend.dto.ExampleCheckResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

// Şagirdin yazdığı kodu müəllimin nümunəsi ilə müqayisə edir. Boşluq, sətir
// sonu və şərhlər (// və /* */) nəzərə alınmır — yalnız kodun ÖZ məzmunu
// (identifikatorlar, ədədlər, operatorlar, sətir literalları) müqayisə olunur,
// ona görə "int x=5;" ilə "int  x = 5 ;" eyni sayılır.
public final class CodeTypingComparer {

    // Bundan uzun kodlarda ətraflı fərq (LCS) hesablanmır — yalnız dəqiq bərabərlik yoxlanır.
    private static final int MAX_TOKENS_FOR_DIFF = 1500;

    private CodeTypingComparer() {
    }

    private record Token(String text, int line) {
    }

    public static ExampleCheckResponse compare(String expectedCode, String typedCode) {
        List<Token> expected = tokenize(expectedCode);
        List<Token> typed = tokenize(typedCode);
        int n = expected.size();
        int m = typed.size();

        if (n > MAX_TOKENS_FOR_DIFF || m > MAX_TOKENS_FOR_DIFF) {
            boolean same = sameTokens(expected, typed);
            return new ExampleCheckResponse(same, same ? 100 : 0, List.of(), 0, true);
        }

        // lcs[i][j] — expected-in ilk i və typed-ın ilk j tokeni arasında ən uzun ortaq alt-ardıcıllıq.
        int[][] lcs = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (expected.get(i - 1).text().equals(typed.get(j - 1).text())) {
                    lcs[i][j] = lcs[i - 1][j - 1] + 1;
                } else {
                    lcs[i][j] = Math.max(lcs[i - 1][j], lcs[i][j - 1]);
                }
            }
        }

        // Geriyə izləyərək nümunədə olmayan (səhv/artıq) yazılmış tokenlərin sətirlərini toplayırıq.
        TreeSet<Integer> wrongLines = new TreeSet<>();
        int i = n;
        int j = m;
        while (i > 0 && j > 0) {
            if (expected.get(i - 1).text().equals(typed.get(j - 1).text())) {
                i--;
                j--;
            } else if (lcs[i - 1][j] >= lcs[i][j - 1]) {
                i--;
            } else {
                wrongLines.add(typed.get(j - 1).line());
                j--;
            }
        }
        while (j > 0) {
            wrongLines.add(typed.get(j - 1).line());
            j--;
        }

        int common = lcs[n][m];
        boolean match = n > 0 && common == n && common == m;
        int percent = Math.max(n, m) == 0 ? 0 : (int) Math.round(100.0 * common / Math.max(n, m));
        return new ExampleCheckResponse(match, percent, new ArrayList<>(wrongLines), n - common, false);
    }

    private static boolean sameTokens(List<Token> a, List<Token> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).text().equals(b.get(i).text())) {
                return false;
            }
        }
        return true;
    }

    private static List<Token> tokenize(String src) {
        List<Token> tokens = new ArrayList<>();
        if (src == null) {
            return tokens;
        }
        int n = src.length();
        int line = 1;
        int i = 0;
        while (i < n) {
            char c = src.charAt(i);
            if (c == '\n') {
                line++;
                i++;
            } else if (Character.isWhitespace(c)) {
                i++;
            } else if (c == '/' && i + 1 < n && src.charAt(i + 1) == '/') {
                while (i < n && src.charAt(i) != '\n') {
                    i++;
                }
            } else if (c == '/' && i + 1 < n && src.charAt(i + 1) == '*') {
                i += 2;
                while (i < n && !(src.charAt(i) == '*' && i + 1 < n && src.charAt(i + 1) == '/')) {
                    if (src.charAt(i) == '\n') {
                        line++;
                    }
                    i++;
                }
                i = Math.min(n, i + 2);
            } else if (c == '"' || c == '\'') {
                int start = i;
                int startLine = line;
                i++;
                while (i < n && src.charAt(i) != c && src.charAt(i) != '\n') {
                    i += src.charAt(i) == '\\' ? 2 : 1;
                }
                if (i < n && src.charAt(i) == c) {
                    i++;
                }
                tokens.add(new Token(src.substring(start, Math.min(i, n)), startLine));
            } else if (Character.isLetterOrDigit(c) || c == '_') {
                int start = i;
                while (i < n && (Character.isLetterOrDigit(src.charAt(i)) || src.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(new Token(src.substring(start, i), line));
            } else {
                tokens.add(new Token(String.valueOf(c), line));
                i++;
            }
        }
        return tokens;
    }
}
