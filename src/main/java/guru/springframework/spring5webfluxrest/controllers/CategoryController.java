package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public Flux<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Category> getCategoryById(@PathVariable("id") String id) {
        return categoryRepository.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createCategory(@RequestBody Publisher<Category> categoryPublisher) {
        return categoryRepository.saveAll(categoryPublisher).then();
    }

    @PutMapping("/{id}")
    public Mono<Category> updateCategory(@PathVariable("id") String id,
                                         @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("/{id}")
    public Mono<Category> patchCategory(@PathVariable("id") String id,
                                        @RequestBody Category category) {

        // findById returns Mono.empty() when Category with id not found
        // On block(), empty Mono returns a null
        Category cat = categoryRepository.findById(id).block();

        if (cat != null) {
            boolean changed = false;

            if (category.getDescription() != null &&
                    !cat.getDescription().equals(category.getDescription())) {
                cat.setDescription(category.getDescription());
                changed = true;
            }

            if (changed) {
                return categoryRepository.save(cat);
            } else {
                return Mono.just(cat);
            }
        } else {
            throw new RuntimeException(String.format("Category with id of <%s> not found", id));
        }
    }

}
