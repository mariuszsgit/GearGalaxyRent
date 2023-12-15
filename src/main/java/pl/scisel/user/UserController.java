package pl.scisel.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/create-user")
    @ResponseBody
    public String createUser() {
        User user = new User();
        user.setUsername("user3");
        user.setPassword("user3");
        user.setEmail("user3@emil.com");
        user.setFirstName("User3");
        user.setLastName("User3");
        user.setEnabled(1);

        userService.saveUser(user);
        return "admin";
    }

    @GetMapping("/admin1")
    @ResponseBody
    public String userInfo(@AuthenticationPrincipal UserDetails customUser) {
        //log.info("customUser class {} " , customUser.getClass());
        return "You are logged as " + customUser;
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin(@AuthenticationPrincipal CurrentUser customUser) {
        User entityUser = customUser.getUser();
        return "Hello " + entityUser.getUsername();
    }

}