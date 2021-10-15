package test.springboot.datar2dbcreactive.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import test.springboot.datar2dbcreactive.data.CustomerAccount;

public interface CustomerAccountRepository extends ReactiveCrudRepository<CustomerAccount, Long> {
    Flux<CustomerAccount> findByCustomerId(Long id);
}
