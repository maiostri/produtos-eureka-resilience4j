package com.letscode.produtoseureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProdutosEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdutosEurekaApplication.class, args);
    }

}
