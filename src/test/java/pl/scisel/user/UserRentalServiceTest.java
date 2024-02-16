package pl.scisel.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.scisel.email.EmailService;
import pl.scisel.item.Item;
import pl.scisel.item.ItemRepository;
import pl.scisel.rental.Rental;
import pl.scisel.rental.RentalRepository;
import pl.scisel.rental.RentalStatus;
import pl.scisel.security.CurrentUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRentalServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserRentalService userRentalService;

    private final String username = "ownerUsername";
    private final String password = "ownerPassword";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateNewRental() {
        // Act
        Rental rental = userRentalService.createNewRental();

        // Assert
        assertNotNull(rental);
        assertEquals(BigDecimal.valueOf(0), rental.getPrice());
        assertTrue(rental.getRentFrom().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(rental.getRentTo().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    public void testGetItemIfOwnedByUser() throws IllegalAccessException {
        // Arrange
        Long itemId = 1L;
        Long userId = 1L;
        Item item = new Item();
        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(item));

        // Act
        Item result = userRentalService.getItemIfOwnedByUser(itemId, userId);

        // Assert
        assertEquals(item, result);
    }

    @Test
    public void testGetItemIfOwnedByUserThrowsException() {
        // Arrange
        Long itemId = 1L;
        Long userId = 1L;
        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalAccessException.class, () -> userRentalService.getItemIfOwnedByUser(itemId, userId));
    }


    @Test
    public void whenReturnRental_thenSuccess() {

        User owner = User.builder()
                .id(1L)
                .username("ownerUsername")
                .password("ownerPassword")
                .enabled(1)
                .email("owner@example.com")
                .firstName("OwnerFirstName")
                .lastName("OwnerLastName")
                .build();

        User leaser = User.builder()
                .id(2L)
                .username("leaserUsername")
                .password("leaserPassword")
                .enabled(1)
                .email("leaser@example.com")
                .firstName("LeaserFirstName")
                .lastName("LeaserLastName")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .owner(owner)
                .build();

        item.setOwner(owner);

        Rental rental = new Rental();
        rental.setItem(item);
        rental.setLeaser(leaser);
        rental.setRentalStatus(RentalStatus.RENTED);

        CurrentUser currentUser = new CurrentUser(username, password, Collections.emptyList(), leaser);

        when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(rental));

        // When
        boolean result = userRentalService.returnRental(1L, currentUser);

        // Then
        assertTrue(result);
        assertEquals(RentalStatus.AVAILABLE, rental.getRentalStatus());
        assertNull(rental.getLeaser());
        verify(rentalRepository, times(1)).save(rental);
        verify(emailService, times(1)).sendSimpleMessage(eq(owner.getEmail()), anyString(), anyString());
        verify(emailService, times(1)).sendSimpleMessage(eq(leaser.getEmail()), anyString(), anyString());
    }

    /* TEST END */

    @Test
    public void whenReturnRental_andUserIsNotLeaser_thenFail() {
        // Given

        User owner = User.builder()
                .id(1L)
                .username("ownerUsername")
                .password("ownerPassword")
                .enabled(1)
                .email("owner@example.com")
                .firstName("OwnerFirstName")
                .lastName("OwnerLastName")
                .build();

        User leaser = User.builder()
                .id(2L)
                .username("leaserUsername")
                .password("leaserPassword")
                .enabled(1)
                .email("leaser@example.com")
                .firstName("LeaserFirstName")
                .lastName("LeaserLastName")
                .build();

        User differentUser = User.builder()
                .id(3L)
                .username("differentUserName")
                .password("differentUserPassword")
                .enabled(1)
                .email("differentUser@example.com")
                .firstName("differentUserFirstName")
                .lastName("differentUserLastName")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .owner(owner)
                .build();

        item.setOwner(owner);

        Rental rental = new Rental();
        rental.setItem(item);
        rental.setLeaser(leaser);
        rental.setRentalStatus(RentalStatus.RENTED);

        CurrentUser currentUserDifferent = new CurrentUser(username, password, Collections.emptyList(), differentUser);

        when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(rental));

        // When
        boolean result = userRentalService.returnRental(1L, currentUserDifferent);

        // Then
        assertFalse(result);
        verify(rentalRepository, never()).save(rental);
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    public void whenReturnRental_andRentalNotFound_thenThrowException() {
        // Given

        User someUser = User.builder()
                .id(1L)
                .username("ownerUsername")
                .password("ownerPassword")
                .enabled(1)
                .email("owner@example.com")
                .firstName("OwnerFirstName")
                .lastName("OwnerLastName")
                .build();

        CurrentUser currentUser = new CurrentUser(username, password, Collections.emptyList(), someUser);

        when(rentalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userRentalService.returnRental(1L, currentUser));
    }
}
