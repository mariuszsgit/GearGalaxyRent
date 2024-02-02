package pl.scisel.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.scisel.user.User;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>  {
    boolean existsByItemId(Long itemId);

    // Metoda do pobierania Rental, gdzie Item należy do określonego użytkownika
    @Query("SELECT r FROM Rental r WHERE r.item.owner.id = :ownerId")
    List<Rental> findByItemOwnerId(@Param("ownerId") Long ownerId);

    List<Rental> findByLeaserIdAndItemOwnerNot(Long leaserId, User owner);

    List<Rental> findByRentalStatus(RentalStatus status);

    List<Rental> findByLeaserIdAndRentalStatus(Long leaserId, RentalStatus status);

    List<Rental> findByItemOwnerIdAndRentalStatus(Long ownerId, RentalStatus status);
}