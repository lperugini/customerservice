package com.sap.customerservice.messaging;

import java.util.Optional;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.customerservice.model.Customer;
import com.sap.customerservice.model.CustomerRepo;

@Service
public class MessageConsumer {

    @Autowired
    private CustomerRepo repository;

    // Questo metodo ascolta i messaggi in arrivo nella coda 'orderQueue'
    @RabbitListener(queues = MessageConfig.QUEUE_NAME)
    public void consumeNewOrder(String message) {
        System.out.println("Ricevuto messaggio: " + message);

        JSONObject jsonObject = new JSONObject(message);
        Customer newCustomer = new Customer(jsonObject.getString("customerFirstName"), jsonObject.getString("customerLastName"));
        Customer customer = newCustomer;

        if (jsonObject.has("id")) {
            Long id = jsonObject.getLong("id");
            Optional<Customer> optionalOrder = repository.findById(id);

            if (optionalOrder.isPresent()) {
                customer = optionalOrder.get();
                customer.setFirstName(newCustomer.getFirstName());
                customer.setLastName(newCustomer.getLastName());
            }
        }

        repository.save(customer);
        System.out.println("Parsed: " + jsonObject.toString());
    }
}
