package com.rest.demoapi.controller;

import com.rest.demoapi.service.ILambdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lambda")
public class LambdaController {

    @Autowired
    private ILambdaService lambdaService;

    @PostMapping("/trigger")
    public ResponseEntity<Void> trigger() {
        lambdaService.trigger();
        return ResponseEntity.noContent().build();
    }

}
