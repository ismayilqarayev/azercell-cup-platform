package az.azcup.backend.dto;

// g++-ın tapdığı bir sintaksis xətası — canlı dərsdə səhv sətri qırmızı göstərmək üçün.
public class SyntaxErrorDto {

    private final int line;
    private final int column;
    private final String message;

    public SyntaxErrorDto(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }
}
