package pl.scisel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.scisel.product.ProductRepository;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ProductRepository productRepository;

    HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RequestMapping("")
    public String home(Model model) {
        model.addAttribute("product", productRepository.findAll());
        return "product/list";
    }
}
