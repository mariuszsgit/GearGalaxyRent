package pl.scisel.category;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
/*        model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Order.asc("id"))));*/
        model.addAttribute("categories", categoryRepository.findAllSortByOrder());

        return "category/list";
    }

    // Get
    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    // Post
    @PostMapping("/add")
    public String save(@Valid Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "category/add";
        }
        categoryRepository.save(category);
        return "redirect:/category/list";
    }

    // Edit
    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).get();
        model.addAttribute("category", category);
        return "category/edit";
    }

    @PostMapping("/edit")
    public String update(@Valid Category category, BindingResult result, Model model, @RequestParam(name = "id") Long id) {
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "category/edit";
        }
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category categoryEdited = categoryOptional.get();
            categoryEdited.setId(category.getId());
            categoryEdited.setName(category.getName());
            categoryEdited.setDescription(category.getDescription());
            categoryEdited.setCategoryOrder(category.getCategoryOrder());
            categoryEdited.setCategoryOrder(category.getCategoryOrder());
            categoryRepository.save(categoryEdited);
        }

        return "redirect:/category/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            categoryRepository.delete(category);
        }

        return "redirect:/category/list";
    }
}
