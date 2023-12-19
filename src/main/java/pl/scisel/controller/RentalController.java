package pl.scisel.rental;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.scisel.category.CategoryRepository;
import pl.scisel.entity.Rental;
import pl.scisel.item.ItemRepository;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/rental")
public class RentalController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final RentalRepository rentalRepository;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    RentalController(RentalRepository rentalRepository, ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.rentalRepository = rentalRepository;
    }

    // Add Get
    @RequestMapping("/add")
    public String add(Model model) {
        Rental rental = new Rental();
        model.addAttribute("rental", rental);
        rental.setRentFrom(LocalDateTime.now());
        rental.setRentTo(LocalDateTime.now());
        model.addAttribute("allStatuses", RentalStatus.values());
        model.addAttribute("items", itemRepository.findAll());
        return "rental/add";
    }

    // Add Post
    @PostMapping("/add")
    public String save(@Valid Rental rental, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rental", rental);
            return "rental/edit";
        }

        rentalRepository.save(rental);
        return "redirect:/rental/list";
    }

    // Edit GET
    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Rental rental = rentalRepository.findById(id).get();
        model.addAttribute("rental", rental);
        model.addAttribute("allStatuses", RentalStatus.values());
        model.addAttribute("items", itemRepository.findAll());
        return "rental/edit";
    }

    //Edit Post
    @PostMapping("/edit")
    public String update(@Valid Rental rental,
                         BindingResult result, Model model,
                         @RequestParam(name = "id") Long id) {

        if (rental.getRentFrom() != null && rental.getRentTo() != null) {
            if (rental.getRentTo().isBefore(rental.getRentFrom())) {
                Locale currentLocale = LocaleContextHolder.getLocale();
                String errorMessage = messageSource.getMessage("rentTo.after.rentFrom", null, currentLocale);
                result.rejectValue("rentTo", "error.rentTo", errorMessage);
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("rental", rental);
            model.addAttribute("allStatuses", RentalStatus.values());
            model.addAttribute("items", itemRepository.findAll());
            return "rental/edit";
        }
        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        if (rentalOptional.isPresent()) {
            Rental rentalEdited = rentalOptional.get();
            rentalEdited.setId(rental.getId());
            rentalEdited.setPrice(rental.getPrice());
            rentalEdited.setRentFrom(rental.getRentFrom());
            rentalEdited.setRentTo(rental.getRentTo());
            rentalEdited.setRentalStatus(rental.getRentalStatus());
            rentalEdited.setItem(rental.getItem());
            rentalRepository.save(rentalEdited);
        }

        return "redirect:/rental/list";
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("rentals", rentalRepository.findAll());
        return "rental/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();
            rentalRepository.delete(rental);
        }
        return "redirect:/rental/list";
    }

}
