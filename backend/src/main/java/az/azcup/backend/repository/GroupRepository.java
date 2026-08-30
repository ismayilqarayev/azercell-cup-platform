package az.azcup.backend.repository;

import az.azcup.backend.entity.Group;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    // Bir müəllimin sahib olduğu bütün qrupları qaytarır ("Qruplarım" ekranı).
    List<Group> findByTeacherOrderByCreatedAtDesc(User teacher);
}
