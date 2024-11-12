package com.sap.customerservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.lang.NonNull;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.sap.customerservice.model.Customer;

@Component
public class CustomerAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>> {
    @Override
    public @NonNull EntityModel<Customer> toModel(@NonNull Customer customer) {

        return EntityModel.of(customer, //
                linkTo(methodOn(CustomerController.class).one(customer.getId())).withSelfRel(),
                linkTo(methodOn(CustomerController.class).all()).withRel("customers"));
    }

}
