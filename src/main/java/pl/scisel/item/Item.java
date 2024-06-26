package pl.scisel.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.scisel.category.Category;
import pl.scisel.rental.Rental;
import pl.scisel.user.User;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "rentals")
@EqualsAndHashCode(exclude = "rentals")
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{item.name.notBlank.message}")
    @Column(nullable = false, length = 70)
    private String name;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(length = 1024)
    private String imageUrl;

    @ManyToOne
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "item")
    private List<Rental> rentals;

}
