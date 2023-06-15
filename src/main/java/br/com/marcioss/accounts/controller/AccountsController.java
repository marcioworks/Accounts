package br.com.marcioss.accounts.controller;

import br.com.marcioss.accounts.config.AccountsServiceConfig;
import br.com.marcioss.accounts.model.*;
import br.com.marcioss.accounts.repository.AccountsRepository;
import br.com.marcioss.accounts.service.client.CardsFeignClient;
import br.com.marcioss.accounts.service.client.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountsController {

    private Logger logger = LoggerFactory.getLogger(AccountsController.class);
    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsServiceConfig serviceConfig;

    @Autowired
    private LoansFeignClient loansFeignClient;
    @Autowired
    private CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        return accountsRepository.findByCustomerId(customer.getCustomerId());
    }

    @GetMapping("/accounts/properties")
    public String getPropertiesDetails() throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(serviceConfig.getMsg(), serviceConfig.getBuildVersion(),
                serviceConfig.getMailDetails(), serviceConfig.getActiveBranches());
        return objectWriter.writeValueAsString(properties);
    }

    @PostMapping("/myCustomerDetails")
//    @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomersDetailsFallBack")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomersDetailsFallBack")
    public CustomerDetails myCustomerDetails(@RequestHeader("marciossbank-correlation-id") String correlationId,
                                             @RequestBody Customer customer) {
        logger.info("myCustomerDetails() method started");
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(correlationId,customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);
        logger.info("myCustomerDetails() method ended");
        return customerDetails;
    }

    private CustomerDetails myCustomersDetailsFallBack(@RequestHeader("marciossbank-correlation-id") String correlationId,
                                                       Customer customer, Throwable t) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationId,customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        return customerDetails;
    }

    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        return "Hello, Welcome to Marcioss Bank";
    }

    private String sayHelloFallback(Throwable t) {
        return "Hi,Welcome to Marcioss bank";
    }
}
