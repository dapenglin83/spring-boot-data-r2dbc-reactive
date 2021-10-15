package test.springboot.datar2dbcreactive.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import test.springboot.datar2dbcreactive.data.Customer;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}
