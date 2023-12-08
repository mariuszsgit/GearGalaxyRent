package pl.scisel.product;

import jakarta.persistence.*;
import lombok.*;
import pl.scisel.category.Category;

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
}
