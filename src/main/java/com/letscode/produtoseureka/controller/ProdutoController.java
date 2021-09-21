package com.letscode.produtoseureka.controller;

import com.letscode.produtoseureka.domain.Produto;
import com.letscode.produtoseureka.service.ProdutoService;
import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Controller
@RequestMapping(path = "/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final String resilienceId = "produtos";
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final TimeLimiter timeLimiter;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadPoolBulkhead threadPoolBulkhead;

    public ProdutoController(
            ProdutoService produtoService,
            CircuitBreakerRegistry circuitBreakerRegistry,
            BulkheadRegistry bulkheadRegistry,
            RetryRegistry retryRegistry,
            RateLimiterRegistry rateLimiterRegistry,
            TimeLimiterRegistry timeLimiterRegistry,
            ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry
    ) {
        this.produtoService = produtoService;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(resilienceId);
        this.bulkhead = bulkheadRegistry.bulkhead(resilienceId);
        this.retry = retryRegistry.retry(resilienceId);
        this.rateLimiter = rateLimiterRegistry.rateLimiter(resilienceId);
        this.timeLimiter = timeLimiterRegistry.timeLimiter(resilienceId);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(3);
        this.threadPoolBulkhead = threadPoolBulkheadRegistry.bulkhead(resilienceId);

        circuitBreaker
                .getEventPublisher()
                .onSuccess(System.out::println)
                .onError(System.out::println)
                .onStateTransition(System.out::println)
                .onReset(System.out::println);
    }


    @PostMapping(consumes = "application/json")
    public @ResponseBody
    ResponseEntity adicionaNovoProduto(@RequestBody Produto p) {
        Supplier<Boolean> decorate = Decorators.ofSupplier(() -> produtoService.salvarProduto(p))
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .withBulkhead(bulkhead)
                .withRateLimiter(rateLimiter)
                .decorate();

        Supplier<CompletionStage<Boolean>> oriSupplier = () -> CompletableFuture.supplyAsync(decorate);

        CompletionStage<Boolean> booleanCompletionStage = timeLimiter.decorateCompletionStage(scheduledExecutorService, oriSupplier).get();

        try {
            if (booleanCompletionStage.toCompletableFuture().get()) {
                return new ResponseEntity(HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
