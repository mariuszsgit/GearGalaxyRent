package pl.scisel.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Digits(integer = 10, fraction = 0, message = "{category.categoryOrder.digits.message}")
    private Integer categoryOrder;

    @NotBlank(message = "{category.name.notBlank.message}")
    @Size(min = 3, max = 100, message="{category.name.size.message}")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "{category.description.notBlank.message}")
    @Size(min = 10, max = 600, message = "{category.description.size.message}")
    @Column(length = 600)
    private String description;

}

