package com.sap.customerservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.sap.customerservice.model.Customer;
import com.sap.customerservice.model.CustomerRepo;
import com.sap.customerservice.exceptions.CustomerNotFoundException;

@RestController
class CustomerController {

    private final CustomerRepo repository;
    private final CustomerAssembler assembler;

    CustomerController(CustomerRepo repository, CustomerAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/customers")
    CollectionModel<EntityModel<Customer>> all() {

        List<EntityModel<Customer>> customers = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(customers, linkTo(methodOn(CustomerController.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]

    @PostMapping("/customers")
    ResponseEntity<?> newCustomer(@RequestBody Customer newCustomer) {
        EntityModel<Customer> entityModel = assembler.toModel(repository.save(newCustomer));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    // Single item
    @GetMapping("/customers/{id}")
    EntityModel<Customer> one(@PathVariable Long id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return assembler.toModel(customer);
    }

    @PutMapping("/customers/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Customer newCustomer, @PathVariable Long id) {

        Customer updatedCustomer = repository.findById(id)
                .map(customer -> {
                    customer.setName(newCustomer.getName());
                    return repository.save(customer);
                })
                .orElseGet(() -> {
                    return repository.save(newCustomer);
                });
        EntityModel<Customer> entityModel = assembler.toModel(updatedCustomer);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/customers/{id}")
    ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();

    }
}
