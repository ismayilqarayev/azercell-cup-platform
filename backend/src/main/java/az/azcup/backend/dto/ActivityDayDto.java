package az.azcup.backend.dto;

import java.util.Objects;

// "GET /api/me/activity" və "GET /api/teacher/students/{id}/activity"
// cavabında bir GÜN üçün bir sətir — GitHub-un "contribution graph"ına
// bənzər fəaliyyət xəritəsi üçün. Yalnız cəhd olan günlər qaytarılır
// (0 cəhdli günlər siyahıda yoxdur) — frontend qalan günləri 0 kimi doldurur.
public class ActivityDayDto {

    // "YYYY-MM-DD" formatında, istifadəçinin YERLİ (Asia/Baku) təqvim günü.
    private final String date;
    // Həmin gün göndərilən cəhdlərin (nəticəsindən asılı olmayaraq) sayı.
    private final int count;

    public ActivityDayDto(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivityDayDto that = (ActivityDayDto) o;
        return count == that.count && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, count);
    }

    @Override
    public String toString() {
        return "ActivityDayDto{date='" + date + '\'' + ", count=" + count + '}';
    }
}
