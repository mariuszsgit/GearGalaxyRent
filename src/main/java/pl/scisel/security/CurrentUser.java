package pl.scisel.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CurrentUser extends User {
    private final pl.scisel.user.User user;
    public CurrentUser(String username, String password,
                       Collection<? extends GrantedAuthority> authorities,
                       pl.scisel.user.User user) {
        super(username, password, authorities);
        this.user = user;
    }
    public pl.scisel.user.User getUser() {return user;}
    public pl.scisel.user.User getFirstName() {return user;}
}