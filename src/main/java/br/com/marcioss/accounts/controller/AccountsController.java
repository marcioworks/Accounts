package br.com.marcioss.accounts.controller;

import br.com.marcioss.accounts.model.Accounts;
import br.com.marcioss.accounts.model.Customer;
import br.com.marcioss.accounts.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {
    @Autowired
    private AccountsRepository accountsRepository;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer){
        return accountsRepository.findByCustomerId(customer.getCustomerId());
    }
}
