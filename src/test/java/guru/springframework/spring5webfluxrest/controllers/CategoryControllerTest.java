package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

public class CategoryControllerTest {

    private static final String BASE_URL = "/api/v1/categories";

    private CategoryRepository categoryRepository;

    private WebTestClient webTestClient;

    @Before
    public void setUp() {
        // A non-annotation based way of creating a mock
        categoryRepository = Mockito.mock(CategoryRepository.class);
        CategoryController controller = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void getCategories() {
        given(categoryRepository.findAll())
                .willReturn(Flux.just(
                        Category.builder().description("Category1").build(),
                        Category.builder().description("Category2").build()));

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getCategoryById() {
        Category category = Category.builder().description("Category1").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(category));

        webTestClient.get().uri(BASE_URL + "/dummyId")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category.class)
                .isEqualTo(category);
    }

    @Test
    public void createCategory() {
        Flux<Category> categoryFlux =
                Flux.just(Category.builder().description("Category1").build());

        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(categoryFlux);

        webTestClient.post().uri(BASE_URL)
                .body(Mono.empty(), Category.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void updateCategory() {

        Category category = Category.builder().description("Category1").build();

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.empty());

        webTestClient.put().uri(BASE_URL + "/dummyId")
                .body(Mono.just(category), Category.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void patchCategoryWithChange() {

        Category category = Category.builder().description("Category1").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(category));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.empty());

        webTestClient.patch().uri(BASE_URL + "/dummyId")
                .body(Mono.just(Category.builder().description("Category2").build()), Category.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(categoryRepository).findById(anyString());
        Mockito.verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void patchCategoryNoChange() {

        Category category = Category.builder().description("Category1").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(category));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.empty());

        webTestClient.patch().uri(BASE_URL + "/dummyId")
                .body(Mono.just(Category.builder().description("Category1").build()), Category.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(categoryRepository).findById(anyString());
        Mockito.verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void patchCategoryNotFound() {

        Category category = Category.builder().description("Category1").build();

        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.empty());

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.empty());

        webTestClient.patch().uri(BASE_URL + "/dummyId")
                .body(Mono.just(category), Category.class)
                .exchange()
                .expectStatus().is5xxServerError();

        Mockito.verify(categoryRepository).findById(anyString());
        Mockito.verify(categoryRepository, never()).save(any(Category.class));
    }

}