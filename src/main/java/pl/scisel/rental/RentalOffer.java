package pl.scisel.rental;

import jakarta.persistence.*;
import pl.scisel.product.Product;
import pl.scisel.user.User;

import java.time.LocalDateTime;

@Entity
public class RentalOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String status; // Status ACTIVE, BUSY

    @Column
    private LocalDateTime created;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_until")
    private LocalDateTime availableUntil;

    @Column(name = "payment_method")
    private String paymentMethod;

    @ManyToOne
    @JoinTable(
            name = "product_rental_offers",
            joinColumns = @JoinColumn(name = "rental_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}