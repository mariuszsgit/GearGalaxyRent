package pl.scisel.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;

    ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("product", new Product());
        return "product/add";
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("product", productRepository.findAll());
        return "product/list";
    }
}
