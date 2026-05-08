package pl.hardstyl3r.userservice; // lub rentservice

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "pl.hardstyl3r") 
public class UserServiceApplication { 
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
