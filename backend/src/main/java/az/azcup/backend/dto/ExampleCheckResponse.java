package az.azcup.backend.dto;

import java.util.List;

// "Yoxla" cavabı: şagirdin yazdığı kod nümunə ilə uyğundurmu, nə qədər uyğundur və
// hansı yazılmış sətirlər nümunədə olmayan məzmun ehtiva edir. missingTokens —
// nümunədə olub, yazılmayan hissələrin sayı (hansı sətirdə olduğu bilinmir, çünki
// yazılmayıb). tooLongForDiff=true — kod çox uzundur, yalnız dəqiq bərabərlik yoxlanıb.
public class ExampleCheckResponse {

    private final boolean match;
    private final int percent;
    private final List<Integer> wrongLines;
    private final int missingTokens;
    private final boolean tooLongForDiff;

    public ExampleCheckResponse(boolean match, int percent, List<Integer> wrongLines, int missingTokens, boolean tooLongForDiff) {
        this.match = match;
        this.percent = percent;
        this.wrongLines = wrongLines;
        this.missingTokens = missingTokens;
        this.tooLongForDiff = tooLongForDiff;
    }

    public boolean isMatch() {
        return match;
    }

    public int getPercent() {
        return percent;
    }

    public List<Integer> getWrongLines() {
        return wrongLines;
    }

    public int getMissingTokens() {
        return missingTokens;
    }

    public boolean isTooLongForDiff() {
        return tooLongForDiff;
    }
}
