package pl.scisel.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.scisel.category.CategoryRepository;
import pl.scisel.item.Item;
import pl.scisel.item.ItemRepository;
import pl.scisel.rental.RentalRepository;
import pl.scisel.security.CurrentUser;
import pl.scisel.upload.UploadController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/user")
@Controller
public class UserItemController {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService;

    private static final Logger logger = LoggerFactory.getLogger(UserItemController.class);

    UserItemController(UserRepository userRepository,
                       ItemRepository itemRepository,
                       CategoryRepository categoryRepository,
                       RentalRepository rentalRepository,
                       ImageStorageService imageStorageService) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
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
    public String save(@Valid Item item, BindingResult result, @RequestParam("file") MultipartFile file, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "/user/item/add";
        }

        try {
            String filePath = imageStorageService.store(file); // Zapisz obraz i uzyskaj ścieżkę
            item.setImageUrl(filePath); // Ustaw ścieżkę obrazu w obiekcie item
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Błąd podczas zapisywania obrazu");
            return "/user/item/add";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        Long userId = currentUser.getUser().getId();

        // Sprawdzenie, czy użytkownik istnieje
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o ID: " + userId));

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

        // Przekształć ścieżkę pliku na ścieżkę URL
        String filename = Paths.get(item.getImageUrl()).getFileName().toString();
        String imageUrl = MvcUriComponentsBuilder
                .fromMethodName(UploadController.class, "serveFile", filename)
                .build().toUri().toString();

        model.addAttribute("imageUrl", imageUrl); // Dodaj ścieżkę URL do modelu
        model.addAttribute("item", item);
        model.addAttribute("categories", categoryRepository.findAll());
        return "user/item/edit";
    }


    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = imageStorageService.loadAsResource(filename);
        if (file.exists() || file.isReadable()) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + file.getFilename() + "\"").body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Post
    @PostMapping("/item/edit")
    public String update(@Valid Item itemUpdate, BindingResult result, Model model,
                         @RequestParam(name = "id") Long id, @RequestParam("file") MultipartFile file,
                         RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/login";
        }

        // Walidacja
        if (result.hasErrors()) {
            model.addAttribute("item", itemUpdate);
            return "user/item/edit";
        }

        // Sprawdzenie, czy przedmiot istnieje, ok
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "error.item.cannot.edit.notFound");
            return "redirect:/user/item/list";
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User user = currentUser.getUser();
        Long userId = user.getId();

        Item item = itemOptional.get();
        try {
            // Czy zalogowany użytkownik jest właścicielem item
            if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
                throw new SecurityException("User does not have permission to delete this item.");
            }
        } catch (SecurityException e) {
            // Obsługa wyjątku
            redirectAttributes.addFlashAttribute("error", "error.item.no.permission");
            System.out.println("Security exception: " + e.getMessage());
            logger.error("Security exception: " + e.getMessage());
            return "redirect:/user/item/list";
        }

        try {
            if (!file.isEmpty()) {
                String filePath = imageStorageService.store(file);
                item.setImageUrl(filePath); // Zaktualizuj URL obrazu
            }
        } catch (IOException e) {
            // Obsługa wyjątku, np. zapisanie informacji o błędzie w modelu
            model.addAttribute("uploadError", "Error occurred while uploading file.");
            model.addAttribute("item", itemUpdate);
            return "user/item/edit";
        }
        // Aktualizuj dane Item
        item.setId(itemUpdate.getId());
        item.setName(itemUpdate.getName());
        item.setDescription(itemUpdate.getDescription());
        item.setCategory(itemUpdate.getCategory());

        // Zapisz zmiany
        itemRepository.save(item);
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

}
