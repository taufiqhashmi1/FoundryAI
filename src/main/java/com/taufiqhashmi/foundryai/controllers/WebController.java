package com.taufiqhashmi.foundryai.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String dashboard() {
        return "index";
    }

    @GetMapping("/requests/new")
    public String newRequest() {
        return "requests/new";
    }

    @GetMapping("/workflows/{id}")
    public String workflow(@PathVariable String id) {
        return "workflows/view";
    }
}