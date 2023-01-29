package cz.uhk.ppro.album.category;

import cz.uhk.ppro.album.comment.Comment;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCommentById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name){return categoryRepository.findByName(name);}

    public void saveCategory(Category category){categoryRepository.save(category);}

    public void deleteCategoryById(Long id){
        categoryRepository.deleteById(id);
    }
}
