package pl.scisel.user;

import jakarta.persistence.*;
import pl.scisel.product.Product;
import pl.scisel.rental.RentalOffer;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String last_name;

    @OneToOne(mappedBy = "user")
    private UserCompany userCompany;

    @OneToMany(mappedBy = "owner")
    private List<Product> ownedProducts;

    @OneToMany(mappedBy = "owner")
    private List<RentalOffer> rentalOffers;

}