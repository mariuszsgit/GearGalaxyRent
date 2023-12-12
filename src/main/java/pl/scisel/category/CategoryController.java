package pl.scisel.category;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/category")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAllSortByOrder());
        return "category/list";
    }

    // Get
    @RequestMapping("/add")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    // Post
    @PostMapping("/add")
    public String saveCategory(@Valid Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "category/add";
        }
        categoryRepository.save(category);
        return "redirect:/category/list";
    }

    // Edit
    @RequestMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).get();
        model.addAttribute("category", category);
        return "category/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {

        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            categoryRepository.delete(category);
        }

        return "redirect:/category/list";
    }
}