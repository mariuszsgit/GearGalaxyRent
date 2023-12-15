package pl.scisel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.scisel.rental.RentalRepository;

@Controller
public class HomeController {

    private final RentalRepository rentalRepository;

    HomeController(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("rentals", rentalRepository.findAll());
        return "home/list";
    }

    @GetMapping("/about")
    @ResponseBody
    public String about() { return "Here you can find some details for logged users"; }
}
