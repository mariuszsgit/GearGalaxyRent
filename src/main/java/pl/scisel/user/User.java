package pl.scisel.user;

import jakarta.persistence.*;
import pl.scisel.item.Item;
import pl.scisel.rental.Rental;

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

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String last_name;

    @OneToOne(mappedBy = "user")
    private UserCompany userCompany;

    @OneToMany(mappedBy = "owner")
    private List<Item> ownedItems;

    @OneToMany(mappedBy = "leaser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentalsLeased;

}