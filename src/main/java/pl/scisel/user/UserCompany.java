package pl.scisel.user;

import jakarta.persistence.*;
import pl.scisel.user.User;

@Entity
public class UserCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nip;
    private String address;
    private String phoneNumber;

    @OneToOne
    User user;

}
