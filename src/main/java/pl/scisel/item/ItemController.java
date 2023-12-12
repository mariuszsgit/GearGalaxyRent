package pl.scisel.item;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemRepository itemRepository;

    ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("item", new Item());
        return "item/add";
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("item", itemRepository.findAll());
        return "item/list";
    }
}
