package pl.scisel.item;

import jakarta.persistence.*;
import lombok.*;
import pl.scisel.category.Category;
import pl.scisel.rental.Rental;
import pl.scisel.user.User;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 70)
    private String name;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(length = 1024)
    private String imageUrl;

    @ManyToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "item")
    private List<Rental> rentals;

}
