package pl.hardstyl3r.pas.v1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/**")
    public String home() {
        return "Library REST API";
    }
}
