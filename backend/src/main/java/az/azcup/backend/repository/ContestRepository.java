package az.azcup.backend.repository;

import az.azcup.backend.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    // Yarışları başlama vaxtına görə ƏN YENİDƏN ƏN KÖHNƏYƏ sıralayır —
    // siyahıda ən aktual (yaxın vaxtda başlayan/başlamış) yarışlar öndə olsun.
    List<Contest> findAllByOrderByStartTimeDesc();
}
