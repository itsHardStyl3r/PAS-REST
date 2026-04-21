package pl.hardstyl3r.pas.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "pl.hardstyl3r.pas")
public class SpringSoapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSoapApplication.class, args);
    }
}

