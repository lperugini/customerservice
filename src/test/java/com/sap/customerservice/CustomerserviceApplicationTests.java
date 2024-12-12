package com.sap.customerservice;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sap.customerservice.messaging.MessageConsumer;
import com.sap.customerservice.model.Customer;
import com.sap.customerservice.model.CustomerRepo;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // per usare Mockito
class CustomerserviceApplicationTests {

	@Autowired
	private MockMvc mockMvc; // Oggetto MockMvc per simulare le richieste HTTP

	@MockBean // Mock del repository JPA
	private CustomerRepo customerRepo; // Mock del repository JPA

	@InjectMocks
	private MessageConsumer customerConsumer; // L'oggetto da testare

	@Test
	public void testGetSingleOrder() throws Exception {
		Customer customer = new Customer("Bilbo", "Baggins");
		Optional<Customer> optionalCustomer = Optional.of(customer);

		when(customerRepo.findById(1L)).thenReturn(optionalCustomer);

		// Eseguiamo la richiesta GET alla route /orders
		mockMvc.perform(get("/customers/1"))
				.andExpect(status()
						.isOk()) // Verifica che la risposta sia 200 OK
				.andExpect(jsonPath("$.firstName")
						.value("Bilbo")); // Verifica il primo ordine
	}

	@Test
	public void testGetAllOrders() throws Exception {
		// Simuliamo i dati restituiti dal repository
		Customer customer1 = new Customer("Bilbo", "Baggins");
		Customer customer2 = new Customer("Frodo", "Baggins");
		List<Customer> orders = Arrays.asList(customer1, customer2);

		// Definiamo cosa deve restituire il mock del repository
		when(customerRepo.findAll()).thenReturn(orders);

		// Eseguiamo la richiesta GET alla route /orders
		mockMvc.perform(get("/orders"))
				.andExpect(status().isOk()) // Verifica che la risposta sia 200 OK
				.andExpect(jsonPath("$._embedded.customerList[0].firstName").value("Bilbo")) // Verifica il primo ordine
				.andExpect(jsonPath("$._embedded.customerList[1].firstName").value("Frodo"));

		// Verifica che il metodo findAll() sia stato chiamato una sola volta
		verify(customerRepo, times(1)).findAll();
	}

	@Test
	public void testReceiveAndSave() {
		// Simuliamo il messaggio in formato JSON
		String message = "{\"firstName\":\"Gandalf\",\"lastName\":\"The Grey\"}";

		ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

		// Chiamata al metodo che riceve e processa il messaggio
		// NON SALVA IN QUANTO MOCK
		customerConsumer.consumeNewOrder(message);

		// Verifico che sia il 4 inserimento - dopo i 3 in LoadDatabase
		verify(customerRepo, times(4)).save(customerCaptor.capture());

		Customer savedCustomer = customerCaptor.getValue();
		assertEquals("Gandalf The Grey", savedCustomer.getName());
	}

}
