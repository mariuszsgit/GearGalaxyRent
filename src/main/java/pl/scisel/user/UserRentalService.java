package pl.scisel.user;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pl.scisel.email.EmailService;
import pl.scisel.item.Item;
import pl.scisel.item.ItemRepository;
import pl.scisel.rental.Rental;
import pl.scisel.rental.RentalRepository;
import pl.scisel.rental.RentalStatus;
import pl.scisel.security.CurrentUser;
import pl.scisel.user.exception.ToDateIsBeforeFromDateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserRentalService {

    private final ItemRepository itemRepository;
    private final RentalRepository rentalRepository;
    private final EmailService emailService;

    UserRentalService(ItemRepository itemRepository,
                      RentalRepository rentalRepository,
                      EmailService emailService) {
        this.itemRepository = itemRepository;
        this.rentalRepository = rentalRepository;
        this.emailService = emailService;
    }

    public Rental createNewRental() {
        Rental rental = new Rental();
        rental.setRentFrom(LocalDateTime.now());
        rental.setRentTo(LocalDateTime.now());
        rental.setPrice(BigDecimal.valueOf(0));
        return rental;
    }

    public Item getItemIfOwnedByUser(Long itemId, Long userId) throws IllegalAccessException {
        Optional<Item> optionalItem = itemRepository.findByIdAndOwnerId(itemId, userId);
        if (optionalItem.isPresent()) {
            return optionalItem.get();
        } else {
            throw new IllegalAccessException("Item with given ID does not belong to the user.");
        }
    }

    public Rental getEditableRental(Long rentalId, Long userId) throws IllegalAccessException {
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);

        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            Item rentalItem = rental.getItem();

            if (rentalItem == null || rentalItem.getOwner() == null || !rentalItem.getOwner().getId().equals(userId)) {
                throw new IllegalAccessException("User does not have access to edit this rental.");
            }

            return rental;
        } else {
            throw new NoSuchElementException("Rental with given ID does not exist.");
        }
    }

    public List<Item> getUserItems(Long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    public void checkIfFromIsBeforeTo(Rental rental) {
        if (rental.getRentFrom() != null && rental.getRentTo() != null &&
                rental.getRentTo().isBefore(rental.getRentFrom())) {
            throw new ToDateIsBeforeFromDateException("Rent date from is after to rent date.");
        }
    }

    public void updateRental(Rental rental) {
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
