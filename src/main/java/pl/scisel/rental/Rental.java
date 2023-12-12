package pl.scisel.rental;

import jakarta.persistence.*;
import pl.scisel.item.Item;
import pl.scisel.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime updated;

    private BigDecimal price;

    @Column(name = "rent_from")
    private LocalDateTime rentFrom;

    @Column(name = "rent_to")
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
}