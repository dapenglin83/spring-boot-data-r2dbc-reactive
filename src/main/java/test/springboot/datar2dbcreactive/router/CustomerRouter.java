package test.springboot.datar2dbcreactive.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import test.springboot.datar2dbcreactive.data.Customer;
import test.springboot.datar2dbcreactive.data.CustomerAccount;
import test.springboot.datar2dbcreactive.repo.CustomerAccountRepository;
import test.springboot.datar2dbcreactive.repo.CustomerRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CustomerRouter {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerAccountRepository customerAccountRepository;

    @Bean("customer")
    public RouterFunction<ServerResponse> routes() {
        return nest(path("/customer"),
                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)),
                    nest(path("/{id}"),
                        nest(path("/account"),
                            route(method(HttpMethod.GET), request -> {
                                Long id = Long.parseLong(request.pathVariable("id"));
                                return customerRepository.findById(id).flatMap(customer ->
                                        customerAccountRepository.findByCustomerId(id)
                                            .collectList()
                                            .flatMap(list -> ServerResponse.ok().body(BodyInserters.fromValue(list)))
                                        ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
                            }).andRoute(method(HttpMethod.POST), request -> {
                                Long id = Long.parseLong(request.pathVariable("id"));
                                return customerRepository.findById(id).flatMap(customer -> {
                                    return request.bodyToMono(CustomerAccount.class).flatMap(account -> {
                                        account.setCustomerId(id);
                                        return customerAccountRepository.save(account).flatMap(acc ->
                                                ServerResponse.ok().body(BodyInserters.fromValue(acc)));
                                    }).switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).build());
                                }).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
                            })
                        ).andRoute(method(HttpMethod.GET), request -> {
                            Long id = Long.parseLong(request.pathVariable("id"));
                            return customerRepository.findById(id).flatMap(customer ->
                                    ServerResponse.ok().body(BodyInserters.fromValue(customer))
                                ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
                        })
                    ).andRoute(method(HttpMethod.GET), request ->
                            customerRepository.findAll().collectList().flatMap(list -> ServerResponse.ok().contentType(APPLICATION_JSON)
                                .body(BodyInserters.fromValue(list)))
                    ).andRoute(method(HttpMethod.POST), request ->
                            request.bodyToMono(Customer.class).flatMap(customer ->
                                customerRepository.save(customer).flatMap(cust ->
                                    ServerResponse.ok().body(BodyInserters.fromValue(cust)))
                            )
                    )
                ));
    }
}
