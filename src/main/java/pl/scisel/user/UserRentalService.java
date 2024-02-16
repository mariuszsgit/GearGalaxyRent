package pl.scisel.user;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import pl.scisel.email.EmailService;
import pl.scisel.item.Item;
import pl.scisel.rental.Rental;
import pl.scisel.rental.RentalRepository;
import pl.scisel.rental.RentalStatus;
import pl.scisel.security.CurrentUser;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserRentalService {

    private final RentalRepository rentalRepository;
    private final MessageSource messageSource;
    private final EmailService emailService;

    UserRentalService(RentalRepository rentalRepository,
                      MessageSource messageSource,
                      EmailService emailService) {
        this.rentalRepository = rentalRepository;
        this.messageSource = messageSource;
        this.emailService = emailService;
    }

    public void checkIfFromIsBeforeTo(Rental rental) throws BindException {
        if (rental.getRentFrom() != null && rental.getRentTo() != null &&
                rental.getRentTo().isBefore(rental.getRentFrom())) {
            BindException bindException = new BindException(rental, "rentTo");
            Locale currentLocale = LocaleContextHolder.getLocale();
            String errorMessage = messageSource.getMessage("rentTo.after.rentFrom", null, currentLocale);
            bindException.rejectValue("rentTo", "error.rentTo", errorMessage);
            throw bindException;
        }
    }

    public void updateRental(Rental rental) throws BindException {
        checkIfFromIsBeforeTo(rental);
        rentalRepository.save(rental);
    }

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

    public boolean deleteRental(Long id) {
        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        if (rentalOptional.isPresent()) {
            rentalRepository.delete(rentalOptional.get());
            return true;
        } else {
            return false;
        }
    }

    public List<Rental> getRentalsByUser(User user) {
        return rentalRepository.findByLeaserIdAndItemOwnerNot(user.getId(), user);
    }

    public List<Rental> getRentalsByItemOwnerId(Long ownerId) {
        return rentalRepository.findByItemOwnerId(ownerId);
    }

    public void save(Rental rental) {
        if (rental != null) {
            rentalRepository.save(rental);
        }
    }
}
