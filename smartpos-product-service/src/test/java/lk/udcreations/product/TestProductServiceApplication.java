package lk.udcreations.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import lk.udcreations.product.config.TestConfig;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = { "lk.udcreations.product", "lk.udcreations.common" })
@Import(TestConfig.class)
public class TestProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestProductServiceApplication.class, args);
    }
}
