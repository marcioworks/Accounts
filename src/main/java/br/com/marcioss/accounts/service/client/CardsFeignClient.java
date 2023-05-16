package br.com.marcioss.accounts.service.client;

import br.com.marcioss.accounts.model.Cards;
import br.com.marcioss.accounts.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("cards")
public interface CardsFeignClient {
    @PostMapping(value = "myCards",consumes = "application/json")
    List<Cards> getCardDetails(@RequestBody Customer customer);
}
