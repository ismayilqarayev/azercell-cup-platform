package az.azcup.backend.dto.live;

import java.util.Objects;

// "POST /api/live/sessions" cavabı — müəllimin şagirdə söz ilə deyəcəyi qısa kod.
public class LiveSessionDto {

    // Sessiyanı tanıdan qısa kod (məs. "AB3X9K").
    private final String code;

    // code sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveSessionDto(String code) {
        this.code = code;
    }

    // code sahəsinin dəyərini qaytarır.
    public String getCode() {
        return code;
    }

    // İki LiveSessionDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiveSessionDto that = (LiveSessionDto) o;
        return Objects.equals(code, that.code);
    }

    // equals() ilə uyğun hash kodu yaradır.
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LiveSessionDto{code='" + code + "'}";
    }
}
