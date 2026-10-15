package az.azcup.backend.dto.live;

import java.util.Objects;

// "PUT /api/live/sessions/{code}/help" gövdəsi — şagirdin "✋ Kömək istə"
// düyməsini açıb-bağlaması.
public class LiveHelpRequest {

    // true = şagird köməyə ehtiyacı olduğunu bildirir, false = ləğv edir.
    private final boolean needsHelp;

    // needsHelp sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveHelpRequest(boolean needsHelp) {
        this.needsHelp = needsHelp;
    }

    // needsHelp sahəsinin dəyərini qaytarır.
    public boolean isNeedsHelp() {
        return needsHelp;
    }

    // İki LiveHelpRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiveHelpRequest that = (LiveHelpRequest) o;
        return needsHelp == that.needsHelp;
    }

    // equals() ilə uyğun hash kodu yaradır.
    @Override
    public int hashCode() {
        return Objects.hash(needsHelp);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LiveHelpRequest{needsHelp=" + needsHelp + "}";
    }
}
