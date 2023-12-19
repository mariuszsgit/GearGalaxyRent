package pl.scisel.item;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.scisel.category.CategoryRepository;
import pl.scisel.user.User;
import pl.scisel.security.CurrentUser;
import pl.scisel.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    ItemController(ItemRepository itemRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.itemRepository=itemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Get
    @RequestMapping("/add")
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
        return "item/add";
    }

    // Post
    @PostMapping("/add")
    public String save(@Valid Item item, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "item/add";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        Long userId = currentUser.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        item.setOwner(user);

        itemRepository.save(item);
        return "redirect:/item/list";
    }

    // Edit
    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id).get();
        model.addAttribute("item", item);
        model.addAttribute("categories", categoryRepository.findAll());
        return "item/edit";
    }

    @PostMapping("/edit")
    public String update(@Valid Item item, BindingResult result, Model model, @RequestParam(name = "id") Long id) {
        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "item/edit";
        }
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item itemEdited = itemOptional.get();
            itemEdited.setId(item.getId());
            itemEdited.setName(item.getName());
            itemEdited.setDescription(item.getDescription());
            itemRepository.save(item);
        }

        return "redirect:/item/list";
    }

    @RequestMapping("/list")
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
        return "item/list";
    }

    // Delete
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            itemRepository.delete(item);
        }
        return "redirect:/item/list";
    }
}
