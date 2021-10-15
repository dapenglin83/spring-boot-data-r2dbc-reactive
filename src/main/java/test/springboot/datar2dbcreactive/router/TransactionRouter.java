package test.springboot.datar2dbcreactive.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import test.springboot.datar2dbcreactive.data.Customer;
import test.springboot.datar2dbcreactive.data.Transaction;
import test.springboot.datar2dbcreactive.repo.CustomerRepository;
import test.springboot.datar2dbcreactive.repo.TransactionRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TransactionRouter {

    @Autowired
    TransactionRepository transactionRepository;

    @Bean("transaction")
    public RouterFunction<ServerResponse> routes() {
        return nest(path("/transaction"),
                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)),
                        route(method(HttpMethod.GET), request ->
                                transactionRepository.findAll().collectList().flatMap(list ->
                                        ServerResponse.ok().contentType(APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(list)))
                        ).andRoute(method(HttpMethod.POST), request ->
                                request.bodyToMono(Transaction.class).flatMap(transaction ->
                                        transactionRepository.save(transaction).flatMap(tranx ->
                                                ServerResponse.ok().body(BodyInserters.fromValue(tranx)))
                                )
                        )
                ));
    }
}
