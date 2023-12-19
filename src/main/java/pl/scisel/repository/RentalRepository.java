package pl.scisel.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.scisel.entity.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>  {
}