package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class Bootstrap implements CommandLineRunner {

    private CategoryRepository categoryRepository;
    private VendorRepository vendorRepository;

    @Override
    public void run(String... args) {

        log.info("Bootstrapping data...");

        if (categoryRepository.count().block() == 0) {
            loadCategories();
        }

        if (vendorRepository.count().block() == 0) {
            loadVendors();
        }
    }

    private void loadCategories() {

        log.info("Loading category data...");

        categoryRepository.save(Category.builder()
                .description("Fruits").build())
                .block();
        categoryRepository.save(Category.builder()
                .description("Nuts").build())
                .block();
        categoryRepository.save(Category.builder()
                .description("Breads").build())
                .block();
        categoryRepository.save(Category.builder()
                .description("Meats").build())
                .block();
        categoryRepository.save(Category.builder()
                .description("Eggs").build())
                .block();

        log.info(String.format("Loaded %s categories", categoryRepository.count().block()));
    }

    private void loadVendors() {

        log.info("Loading vendor data...");

        vendorRepository.save(Vendor.builder()
                .firstName("Ian").lastName("Gillan").build())
                .block();
        vendorRepository.save(Vendor.builder()
                .firstName("Ronnie James").lastName("Dio").build())
                .block();
        vendorRepository.save(Vendor.builder()
                .firstName("Ozzy").lastName("Osbourne").build())
                .block();
        vendorRepository.save(Vendor.builder()
                .firstName("Rob").lastName("Halford").build())
                .block();
        vendorRepository.save(Vendor.builder()
                .firstName("Jim").lastName("Morrison").build())
                .block();

        log.info(String.format("Loaded %s vendors", vendorRepository.count().block()));
    }

}
