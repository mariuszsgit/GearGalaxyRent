package pl.scisel.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.scisel.category.CategoryRepository;
import pl.scisel.email.EmailService;
import pl.scisel.item.Item;
import pl.scisel.item.ItemRepository;
import pl.scisel.rental.Rental;
import pl.scisel.rental.RentalRepository;
import pl.scisel.rental.RentalStatus;
import pl.scisel.security.CurrentUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequestMapping("/user")
@Controller
public class UserRentalController {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserRentalService userRentalService;

    private static final Logger logger = LoggerFactory.getLogger(UserRentalController.class);

    UserRentalController(UserRepository userRepository,
                         ItemRepository itemRepository,
                         CategoryRepository categoryRepository,
                         RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.rentalRepository = rentalRepository;
    }

    // Add Get
    @RequestMapping(value = {"/rental/add", "/rental/add/{itemId}"}, method = RequestMethod.GET)
    public String addRental(@PathVariable Optional<Long> itemId, Model model, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/login";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User user = currentUser.getUser();
        Long userId = user.getId();

        Rental rental = new Rental();
        rental.setRentFrom(LocalDateTime.now());
        rental.setRentTo(LocalDateTime.now());
        rental.setPrice(BigDecimal.valueOf(0));

        List<Item> userItems = itemRepository.findByOwnerId(userId);

        // itemId istnieje
        if (itemId.isPresent()) {
            Optional<Item> optionalItem = itemRepository.findByIdAndOwnerId(itemId.get(), userId);
            if (optionalItem.isPresent()) {
                rental.setItem(optionalItem.get());
                model.addAttribute("selectedItemId", optionalItem.get().getId());
            } else {
                return "redirect://user/rental/list";
            }
        }

        model.addAttribute("items", userItems); // itemy użytkownika
        model.addAttribute("rental", rental);
        model.addAttribute("allStatuses", RentalStatus.values());

        return "user/rental/add";
    }

    //
    // Edit GET
    //
    @RequestMapping("/rental/edit/{rentalId}")
    public String editRental(@PathVariable Long rentalId, Model model, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/login";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        // to zostaje

        User user = currentUser.getUser();
        Long userId = user.getId();

        // TODO: Usera przekazać do service, w service rzucić wyjątek, i tutaj go obsłużyć

        // Sprawdź, czy użytkownik ma dostęp do edycji tego wynajmu
        Optional<Rental> optionalRental = rentalRepository.findById(rentalId);

        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            Item rentalItem = rental.getItem();

            if (rentalItem == null || rentalItem.getOwner() == null || !rentalItem.getOwner().getId().equals(userId)) {
                // Przedmiot nie należy do zalogowanego użytkownika
                return "redirect:/user/rental/list";
            }

            // Uzyskaj listę przedmiotów użytkownika
            List<Item> userItems = itemRepository.findByOwnerId(user.getId());

            model.addAttribute("selectedItemId", rentalItem.getId());
            model.addAttribute("items", userItems); // itemy użytkownika
            model.addAttribute("rental", rental);
            model.addAttribute("allStatuses", RentalStatus.values());

            return "user/rental/edit";
        } else {
            // Wynajem o podanym ID nie istnieje
            return "redirect:/user/rental/list";
        }
    }

    //
    // Edit Post
    //
    @PostMapping("/rental/edit")
    public String updateRental(@Valid Rental rental,
                               BindingResult result, Model model,
                               @RequestParam(name = "id") Long id) {

        // 1. tą walidację można zrobić już w Adnotacji
        // komunikaty ustawić w adnotacji ,
        // 2. albo sprawdzać w serwisie,
        // w serwisie łapać wyjątek i obsłużyć
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
            return "user/rental/edit";
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

        return "redirect:/user/rental/list";
    }

    // Add Post
    @PostMapping("/rental/add")
    public String save(@Valid Rental rental, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rental", rental);
            return "user/rental/add";
        }

        rentalRepository.save(rental);
        return "redirect:/user/rental/list";
    }


    @RequestMapping("/rental/list")
    public String listRentals(Model model, @AuthenticationPrincipal CurrentUser currentUser) {

        Long userId = currentUser.getUser().getId();

        List<Rental> rentals = rentalRepository.findByItemOwnerId(userId); // Pobierz wynajmy, które zawierają przedmioty należące do zalogowanego użytkownika

        // Dla każdego wynajmu, pobierz przypisany przedmiot i dodaj jego nazwę i opis do wynajmu
        for (Rental rental : rentals) {
            Item rentalItem = rental.getItem();
            if (rentalItem != null) {
                //rental.setRentalItemName(rentalItem.getName());
                //rental.setRentalItemDescription(rentalItem.getDescription());
            }
        }

        model.addAttribute("rentals", rentals);
        return "user/rental/list";
    }

    @RequestMapping("/rental/lease/{rentalId}")
    public String leaseRental(@PathVariable Long rentalId, Model model, @AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (userRentalService.rentalLease(rentalId, currentUser)) {
            return "redirect:/user/lease/list";
        }

        // Jeśli wynajem nie istnieje lub użytkownik jest właścicielem wynajmu, przekieruj na /
        return "redirect:/";
    }

    @RequestMapping("/lease/list")
    public String listLeasedItems(Model model, @AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            Long leaserId = currentUser.getUser().getId();
            User owner = currentUser.getUser(); // To może być inny użytkownik niż zalogowany
            //TODO: W kontrolerze nie wywoływać rentalRepository, przenieść do Service i tam wywoływać
            List<Rental> leasedItems = rentalRepository.findByLeaserIdAndItemOwnerNot(leaserId, owner);
            model.addAttribute("leases", leasedItems);

            return "user/lease/list";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/rental/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CurrentUser currentUser, RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to perform this action.");
            return "redirect:/login";
        }

        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();

            rentalRepository.delete(rental);
            redirectAttributes.addFlashAttribute("success", "Rental deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Rental not found.");
        }
        return "redirect:/user/rental/list";
    }

    @PostMapping("/rental/return/{rentalId}")
    public String returnRental(@PathVariable Long rentalId, @AuthenticationPrincipal CurrentUser currentUser, RedirectAttributes redirectAttributes) {
        boolean isReturned = userRentalService.returnRental(rentalId, currentUser);

        if (!isReturned) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to return this rental.");
            return "redirect:/user/lease/list";
        }

        redirectAttributes.addFlashAttribute("success", "Item returned successfully.");
        return "redirect:/user/lease/list";
    }

}
