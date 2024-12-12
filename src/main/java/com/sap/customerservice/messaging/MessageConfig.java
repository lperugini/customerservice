package com.sap.customerservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {
    public static final String EXCHANGE_NAME = "orderservice.exchange";

    public static final String ROUTING_KEY = "orderservice.routingKey";

    public static final String QUEUE_NAME = "orderservice.queue";

    // Dichiarazione dell'Exchange
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Dichiarazione della Coda
    @Bean
    public Queue orderQueue() {
        return new Queue(QUEUE_NAME, true); // La coda è persitente
    }

    // Binding tra l'Exchange e la Coda
    @Bean
    public Binding binding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ROUTING_KEY);
    }

}
