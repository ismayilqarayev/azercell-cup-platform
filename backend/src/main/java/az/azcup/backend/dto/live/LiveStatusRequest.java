package az.azcup.backend.dto.live;

import az.azcup.backend.entity.SubmissionStatus;

import java.util.Objects;

// "PUT /api/live/sessions/{code}/status" gövdəsi — şagird öz kodunu "İşə sal"
// etdikdən sonra nəticəni müəllim panelindəki çipə əks etdirmək üçün göndərilir
// (bax: index.html runLiveCode). Status /api/run-dan AYRI, ikinci bir çağırışla
// gəlir, çünki /api/run ümumi (canlı dərsdən xəbərsiz) icra endpoint-idir.
public class LiveStatusRequest {

    // Son "İşə sal" nəticəsi.
    private final SubmissionStatus status;

    // status sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveStatusRequest(SubmissionStatus status) {
        this.status = status;
    }

    // status sahəsinin dəyərini qaytarır.
    public SubmissionStatus getStatus() {
        return status;
    }

    // İki LiveStatusRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiveStatusRequest that = (LiveStatusRequest) o;
        return status == that.status;
    }

    // equals() ilə uyğun hash kodu yaradır.
    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LiveStatusRequest{status=" + status + "}";
    }
}
