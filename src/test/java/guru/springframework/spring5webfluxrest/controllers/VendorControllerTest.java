package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

public class VendorControllerTest {

    private static final String BASE_URL = "/api/v1/vendors";

    private VendorRepository vendorRepository;

    private WebTestClient webTestClient;

    @Before
    public void setUp() {
        vendorRepository = Mockito.mock(VendorRepository.class);
        VendorController controller = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void getAllVendors() {
        Vendor jim = Vendor.builder().firstName("Jim").lastName("Jimmins").build();

        given(vendorRepository.findAll())
                .willReturn(Flux.just(
                        Vendor.builder().firstName("Bob").lastName("Bobbins").build(), jim));

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Vendor.class)
                .hasSize(2)
                .contains(jim);
    }

    @Test
    public void getVendorById() {
        Vendor jim = Vendor.builder().firstName("Jim").lastName("Jimmins").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(jim));

        webTestClient.get().uri(BASE_URL + "/dummyId")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(jim);
    }

    @Test
    public void createVendor() {
        Flux<Vendor> vendorFlux =
                Flux.just(Vendor.builder().firstName("Bob").lastName("Bobbins").build());

        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(vendorFlux);

        webTestClient.post().uri(BASE_URL)
                .body(Mono.empty(), Vendor.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void updateVendor() {

        Vendor vendor = Vendor.builder().firstName("Bob").lastName("Bobbins").build();

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.empty());

        webTestClient.put().uri(BASE_URL + "/dummyId")
                .body(Mono.just(vendor), Vendor.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void patchVendorWithChange() {

        Vendor vendor = Vendor.builder().firstName("Bob").lastName("Bobbins").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(vendor));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.empty());

        webTestClient.patch().uri(BASE_URL + "/dummyId")
                .body(Mono.just(Vendor.builder().firstName("Bob").lastName("Miggins").build()), Vendor.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(vendorRepository).findById(anyString());
        Mockito.verify(vendorRepository).save(any(Vendor.class));
    }

    @Test
    public void patchVendorNoChange() {

        Vendor vendor = Vendor.builder().firstName("Bob").lastName("Bobbins").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(vendor));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.empty());

        webTestClient.patch().uri(BASE_URL + "/dummyId")
                .body(Mono.just(vendor), Vendor.class)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(vendorRepository).findById(anyString());
        Mockito.verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    public void patchVendorNotFound() {

        Vendor vendor = Vendor.builder().firstName("Bob").lastName("Bobbins").build();

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.empty());

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.empty());

        webTestClient.patch().uri(BASE_URL + "/dummyId")
                .body(Mono.just(vendor), Vendor.class)
                .exchange()
                .expectStatus().is5xxServerError();

        Mockito.verify(vendorRepository).findById(anyString());
        Mockito.verify(vendorRepository, never()).save(any(Vendor.class));
    }

}