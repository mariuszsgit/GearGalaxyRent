package pl.scisel.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.scisel.entity.Item;
import pl.scisel.entity.Rental;
import pl.scisel.entity.User;
import pl.scisel.repository.CategoryRepository;
import pl.scisel.repository.ItemRepository;
import pl.scisel.repository.RentalRepository;
import pl.scisel.util.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/user")
@Controller
public class UserActivityController {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserActivityController.class);

    UserActivityController(UserRepository userRepository,
                           ItemRepository itemRepository,
                           CategoryRepository categoryRepository,
                           RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.rentalRepository = rentalRepository;
    }

    // Get
    @RequestMapping("/item/add")
    public String add(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            Long userId = currentUser.getUser().getId();

        } else {
            return "redirect:/login";
        }

        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryRepository.findAll());
        return "/user/item/add";
    }

    // Post
    @PostMapping("/item/add")
    public String save(@Valid Item item, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "/user/item/add";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        Long userId = currentUser.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        item.setOwner(user);

        itemRepository.save(item);
        return "redirect:/user/item/list";
    }

    @RequestMapping("/item/list")
    public String list(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            Long userId = currentUser.getUser().getId();

            // Pobierz przedmioty należące do zalogowanego użytkownika
            List<Item> items = itemRepository.findByOwnerId(userId);
            model.addAttribute("items", items);
        } else {
            return "redirect:/login";
        }
        return "user/item/list";
    }

    // Get
    @RequestMapping("/item/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CurrentUser)) {
            // Jeśli użytkownik nie jest zalogowany, przekieruj do strony logowania
            return "redirect:/login";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User loggedInUser = currentUser.getUser();

        Optional<Item> itemOptional = itemRepository.findById(id);
        if (!itemOptional.isPresent()) {
            // Jeśli przedmiot nie istnieje
            redirectAttributes.addFlashAttribute("error", "error.item.cannot.edit.notFound");
            return "redirect:/user/item/list";
        }

        Item item = itemOptional.get();
        if (!item.getOwner().getId().equals(loggedInUser.getId())) {
            // Jeśli użytkownik nie jest właścicielem przedmiotu
            redirectAttributes.addFlashAttribute("error", "error.item.no.permission");
            return "redirect:/user/item/list";
        }

        model.addAttribute("item", item);
        model.addAttribute("categories", categoryRepository.findAll());
        return "user/item/edit";
    }

    // Post
    @PostMapping("/item/edit")
    public String update(@Valid Item item, BindingResult result, Model model, @RequestParam(name = "id") Long id, RedirectAttributes redirectAttributes) {

        // Walidacja
        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "user/item/edit";
        }

        // Uwierzytelnienie
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/login";
        }

        // Sprawdzenie, czy przedmiot istnieje, ok
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "error.item.cannot.edit.notFound");
            return "redirect:/user/item/list";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User user = currentUser.getUser();
        Long userId = currentUser.getUser().getId();

        try {
            Item itemEdited = itemOptional.get();

            item = itemOptional.get();
            User owner = item.getOwner();
            if (owner == null || !owner.getId().equals(userId)) {
                throw new SecurityException("User does not have permission to delete this item.");
            }

            // Sprawdź, czy zalogowany użytkownik jest właścicielem elementu
            if (itemEdited.getOwner() == null || !itemEdited.getOwner().getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "error.item.no.permission");
                return "redirect:/user/item/list";
            }

            // Aktualizuj dane elementu
            itemEdited.setId(item.getId());
            itemEdited.setName(item.getName());
            itemEdited.setDescription(item.getDescription());
            itemEdited.setCategory(item.getCategory());

            // Zapisz zmiany
            itemRepository.save(itemEdited);
        } catch (SecurityException e) {
            // Obsługa wyjątku
            redirectAttributes.addFlashAttribute("error", "error.item.no.permission");
            System.out.println("Security exception: " + e.getMessage());
            logger.error("Security exception: " + e.getMessage());
            return "redirect:/user/item/list";
        }

        return "redirect:/user/item/list";
    }

    // Delete
    @GetMapping("/item/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Map<String, String> itemErrors = new HashMap<>();

        // Uwierzytelnienie
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/login";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        Long userId = currentUser.getUser().getId();

        // Sprawdzenie, czy przedmiot istnieje, ok
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "error.item.cannot.delete.notFound");
            return "redirect:/user/item/list";
        }

        // Sprawdzenie, czy przedmiot należy do zalogowanego użytkownika
        // Wyjątek dla security exception
        try {
            Item item = itemOptional.get();
            User owner = item.getOwner();
            User user = currentUser.getUser();
            String username = user.getUsername();
            if (owner == null || !owner.getId().equals(userId)) {
                throw new SecurityException("User " + username + " (ID: " + userId + ") does not have permission to delete this item.");
            }

            // Sprawdzenie, czy istnieją powiązane wynajmy
            boolean hasRentals = rentalRepository.existsByItemId(item.getId());
            if (hasRentals) {
                itemErrors.put("errorDeleteItemMessage", "error.item.cannot.delete.rentals");
                itemErrors.put("errorDeleteItemId", id.toString());
                redirectAttributes.addFlashAttribute("itemErrors", itemErrors);
                return "redirect:/user/item/list";
            }

            itemRepository.delete(item);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", "error.item.no.permission");
            System.out.println("Security exception: " + e.getMessage());
            logger.error("Security exception: " + e.getMessage());
        }

        return "redirect:/user/item/list";
    }

    // Add Get
    @RequestMapping(value = {"/rental/add", "/rental/add/{itemId}"}, method = RequestMethod.GET)
    public String addRental(@PathVariable Optional<Long> itemId,Model model, Authentication authentication) {
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
            }
            else {
                return "redirect://user/rental/list";
            }
        }

        model.addAttribute("items", userItems); // itemy użytkownika
        model.addAttribute("rental", rental);
        model.addAttribute("allStatuses", RentalStatus.values());

        return "user/rental/add";
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
    public String listRentals(Model model) {

        // Uwierzytelnienie
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/login";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        Long userId = currentUser.getUser().getId();

        List<Rental> rentals = rentalRepository.findByItemOwnerId(userId); // Pobierz wynajmy, które zawierają przedmioty należące do zalogowanego użytkownika
        model.addAttribute("rentals", rentals);
        return "user/rental/list";
    }

}
