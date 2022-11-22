package com.nvd.training;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.nvd.training.entity.Customer;
import com.nvd.training.repository.CustomerRepository;

@SpringBootApplication
public class SpringBootDbDockerApplication {

	private static final Logger LOG = LoggerFactory.getLogger(SpringBootDbDockerApplication.class);
	@Autowired
	private CustomerRepository repository;

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		List<Customer> allCustomers = this.repository.findAll();
		LOG.info("Number of customers: " + allCustomers.size());

		Customer newCustomer = new Customer();
		newCustomer.setFirstName("John");
		newCustomer.setLastName("Doe");
		LOG.info("Saving new customer...");
		this.repository.save(newCustomer);

		allCustomers = this.repository.findAll();
		LOG.info("Number of customers: " + allCustomers.size());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDbDockerApplication.class, args);
	}

}
