package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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
}