package com.sushobhan.customer;

import com.sushobhan.clients.fraud.FraudCheckResponse;
import com.sushobhan.clients.fraud.FraudClient;
import com.sushobhan.clients.fraud.NotificationClient;
import com.sushobhan.clients.fraud.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public record CustomerService(CustomerRepository customerRepository, FraudClient fraudClient, NotificationClient notificationClient) {
    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .email(customerRegistrationRequest.email())
                .build();
        // todo: check if email is valid
        // todo: check if email is not taken
        // todo: store customer in db
        customerRepository.saveAndFlush(customer);
        // todo: check if fraudster
        //FraudCheckResponse fraudCheckResponse = restTemplate.getForObject("http://FRAUD/api/v1/fraud-check/{customerId}", FraudCheckResponse.class, customer.getId());

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        assert fraudCheckResponse != null;
        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }
        customerRepository.save(customer);

        // todo: make it sync i.e add to queue
        notificationClient.sendNotificationPings(new NotificationRequest(customer.getId(), customer.getEmail(),
                String.format("Hello %s, Welcome to microservices", customer.getFirstName())));
    }
}
