package az.azcup.backend.dto;

import java.util.List;

// "POST /api/run/check" cavabı. checked=false — yoxlama aparıla bilmədi (server
// məşğuldur, vaxt bitdi və ya g++ yoxdur): bu, "səhv yoxdur" DEMƏK DEYİL, ona görə
// frontend belə cavabda mövcud işarələri dəyişməməlidir.
public class SyntaxCheckResponse {

    private final boolean checked;
    private final List<SyntaxErrorDto> errors;

    public SyntaxCheckResponse(boolean checked, List<SyntaxErrorDto> errors) {
        this.checked = checked;
        this.errors = errors;
    }

    public boolean isChecked() {
        return checked;
    }

    public List<SyntaxErrorDto> getErrors() {
        return errors;
    }
}
