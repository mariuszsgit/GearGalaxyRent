package pl.scisel.item;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.scisel.category.CategoryRepository;

import java.util.Optional;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    ItemController(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    // Get
    @RequestMapping("/add")
    public String addItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryRepository.findAll());
        return "item/add";
    }

    // Post
    @PostMapping("/add")
    public String saveItem(@Valid Item item, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("item", item);
            return "item/add";
        }
        itemRepository.save(item);
        return "redirect:/item/list";
    }

    // Edit
    @RequestMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
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
    public String listItem(Model model) {
        model.addAttribute("items", itemRepository.findAll());
        return "item/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {

        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            itemRepository.delete(item);
        }
        return "redirect:/item/list";
    }
}
