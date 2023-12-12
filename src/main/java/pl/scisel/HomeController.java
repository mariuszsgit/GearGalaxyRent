package pl.scisel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.scisel.item.Item;
import pl.scisel.item.ItemRepository;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ItemRepository itemRepository;

    HomeController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @RequestMapping("")
    public String home(Model model) {
        List<Item> all = itemRepository.findAll();
        model.addAttribute("items", itemRepository.findAll());
        return "item/list";
    }
}
