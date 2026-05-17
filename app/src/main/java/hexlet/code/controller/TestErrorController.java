package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestErrorController {

    @GetMapping("/api/test-error")
    public void testError() {
        throw new RuntimeException("Test Sentry error");
    }
}
