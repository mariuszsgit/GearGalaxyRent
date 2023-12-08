package pl.scisel.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ControllerRepository extends JpaRepository<Category, Long> {
}
