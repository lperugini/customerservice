package com.sap.customerservice.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Table(name = "customers")
public class Customer {

    private @Id @GeneratedValue Long id;
    private String firstName;
    private String lastName;

    Customer() {
    }

    public Customer(String name) {
        String[] split = splitName(name);
        this.firstName = split[0];
        this.lastName = split[1];
    }

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private String[] splitName(String name) {
        return name.split(" ");
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        String[] split = splitName(name);
        this.firstName = split[0];
        this.lastName = split[1];
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.firstName, this.lastName);
    }

    @Override
    public String toString() {
        return "Customer{id=" + this.id + ", First name='" + this.firstName + "', Last name='" + this.lastName + "'}";
    }
}
