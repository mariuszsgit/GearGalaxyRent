package pl.scisel.rental;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.scisel.item.Item;
import pl.scisel.user.User;
import pl.scisel.util.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private RentalStatus rentalStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updated;

    @NotNull
    @Digits (integer = 10, fraction = 2, message = "rental.quantity.digits.message")
    @DecimalMin(value = "0.00", message = "rental.price.min.message")
    @DecimalMax(value = "1000000.00", message = "rental.price.max.message")
    private BigDecimal price;

    @NotNull(message = "{rental.rentFrom.notNull.message}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime rentFrom;

    @NotNull(message = "{rental.rentTo.notNull.message}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime rentTo;

    @Column(name = "payment_method")
    private String paymentMethod;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Leaser, ten który wziął ofertę
    @ManyToOne
    @JoinColumn(name = "leaser_id", referencedColumnName = "id")
    private User leaser;

    @PrePersist
    protected void onCreate() {
        if (created == null) {
            created = LocalDateTime.now();
        }
    }

}