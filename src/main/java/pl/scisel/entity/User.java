package pl.scisel.user;

import jakarta.persistence.*;
import lombok.*;
import pl.scisel.item.Item;
import pl.scisel.rental.Rental;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    private String password;

    private int enabled;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @OneToOne(mappedBy = "user")
    private UserCompany userCompany;

    @OneToMany(mappedBy = "owner")
    private List<Item> ownedItems;

    @OneToMany(mappedBy = "leaser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentalsLeased;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

}