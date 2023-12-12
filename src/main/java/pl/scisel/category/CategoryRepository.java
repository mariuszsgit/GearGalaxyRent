package pl.scisel.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findById(long id);

    @Query("SELECT c FROM Category c ORDER BY c.categoryOrder")
    List<Category> findAllSortByOrder();


}