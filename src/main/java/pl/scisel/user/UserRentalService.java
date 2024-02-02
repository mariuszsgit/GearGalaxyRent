package pl.scisel.user;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.scisel.email.EmailService;
import pl.scisel.item.Item;
import pl.scisel.rental.Rental;
import pl.scisel.rental.RentalRepository;
import pl.scisel.rental.RentalStatus;
import pl.scisel.security.CurrentUser;

import java.util.Optional;

@Service
public class UserRentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EmailService emailService;

    public boolean rentalLease(Long rentalId, CurrentUser currentUser) {
        User user2 = currentUser.getUser();

        // Pobierz wynajem, który ma być wypożyczony
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);

        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            Item rentalItem = rental.getItem();
            User user1 = rentalItem.getOwner(); // Pobierz właściciela przedmiotu

            // Sprawdź, czy użytkownik (user2) nie jest właścicielem przedmiotu wynajmu
            if (!user1.equals(user2)) {
                // Przypisz wypożyczenie (rental) do użytkownika (user2)
                rental.setLeaser(user2);
                rental.setRentalStatus(RentalStatus.RENTED);

                // Wysyłanie emaila

                emailService.sendSimpleMessage(user1.getEmail(), "email.rental.subject", "email.rental.body");
                emailService.sendSimpleMessage(user2.getEmail(), "email.lease.subject", "email.lease.body");
                rentalRepository.save(rental);

            }
        }
        return optionalRental.isPresent();
    }

    public boolean returnRental(Long rentalId, CurrentUser currentUser) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + rentalId));

        if (!rental.getLeaser().getId().equals(currentUser.getUser().getId())) {
            return false;
        }

        User userOwner = rental.getItem().getOwner();
        User userLeaser = rental.getLeaser();

        // Wysyłanie emaila
        emailService.sendSimpleMessage(userOwner.getEmail(), "email.rental.return.subject", "email.rental.return.body");
        emailService.sendSimpleMessage(userLeaser.getEmail(), "email.lease.return.subject", "email.lease.return.body");

        rental.setRentalStatus(RentalStatus.AVAILABLE);
        rental.setLeaser(null); // Usunięcie przypisania użytkownika do wypożyczenia
        rentalRepository.save(rental);

        return true;
    }

}
