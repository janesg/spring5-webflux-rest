package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/vendors")
@AllArgsConstructor
public class VendorController {

    private final VendorRepository vendorRepository;

    @GetMapping()
    public Flux<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Vendor> getVendorById(@PathVariable("id") String id) {
        return vendorRepository.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createVendor(@RequestBody Publisher<Vendor> vendorPublisher) {
        return vendorRepository.saveAll(vendorPublisher).then();
    }

    @PutMapping("/{id}")
    public Mono<Vendor> updateCategory(@PathVariable("id") String id,
                                       @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/{id}")
    public Mono<Vendor> patchVendor(@PathVariable("id") String id,
                                    @RequestBody Vendor vendor) {

        // findById returns Mono.empty() when Vendor with id not found
        // On block(), empty Mono returns a null
        Vendor ven = vendorRepository.findById(id).block();

        if (ven != null) {
            boolean changed = false;

            if (vendor.getFirstName() != null &&
                    !ven.getFirstName().equals(vendor.getFirstName())) {
                ven.setFirstName(vendor.getFirstName());
                changed = true;
            }

            if (vendor.getLastName() != null &&
                    !ven.getLastName().equals(vendor.getLastName())) {
                ven.setLastName(vendor.getLastName());
                changed = true;
            }

            if (changed) {
                return vendorRepository.save(ven);
            } else {
                return Mono.just(ven);
            }
        } else {
            throw new RuntimeException(String.format("Vendor with id of <%s> not found", id));
        }
    }

}
