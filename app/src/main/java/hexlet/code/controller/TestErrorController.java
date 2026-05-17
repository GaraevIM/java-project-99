package hexlet.code.controller;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestErrorController {

    @GetMapping("/api/test-error")
    public String testError() {
        var exception = new RuntimeException("Test Sentry error");
        Sentry.captureException(exception);
        throw exception;
    }
}
