package test.springboot.datar2dbcreactive.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import test.springboot.datar2dbcreactive.data.Transaction;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
}
