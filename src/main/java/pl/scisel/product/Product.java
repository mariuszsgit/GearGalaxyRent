package pl.scisel.product;

import jakarta.persistence.*;
import lombok.*;
import pl.scisel.category.Category;
import pl.scisel.rental.RentalOffer;
import pl.scisel.user.User;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "description")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 70)
    private String name;

    @Column(nullable = false, length = 1500)
    private String description;

    @ManyToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "product")
    private List<RentalOffer> rentalOffers;

}
