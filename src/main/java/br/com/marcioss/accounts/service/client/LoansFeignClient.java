package br.com.marcioss.accounts.service.client;

import br.com.marcioss.accounts.model.Customer;
import br.com.marcioss.accounts.model.Loans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.List;

@FeignClient("loans")
public interface LoansFeignClient {

    @PostMapping(value = "myLoans", consumes = "application/json")
    List<Loans> getLoansDetails(@RequestHeader("marciossbank-correlation-id") String correlationId,
                                @RequestBody Customer customer);
}
