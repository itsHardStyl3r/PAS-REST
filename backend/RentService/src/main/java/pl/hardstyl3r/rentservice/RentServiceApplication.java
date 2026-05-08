package pl.hardstyl3r.rentservice; // lub rentservice

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "pl.hardstyl3r") 
public class RentServiceApplication { 
    public static void main(String[] args) {
        SpringApplication.run(RentServiceApplication.class, args);
    }
}
