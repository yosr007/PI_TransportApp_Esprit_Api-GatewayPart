package tn.youssef.api_gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/payment")
    public ResponseEntity<Map<String, String>> paymentFallback() {
        return ResponseEntity.status(503).body(Map.of(
                "status", "unavailable",
                "message", "Payment service is temporarily unavailable. Try again later."
        ));
    }

    // Add one per service
    @GetMapping("/{service}")
    public ResponseEntity<Map<String, String>> genericFallback(@PathVariable String service) {
        return ResponseEntity.status(503).body(Map.of(
                "status", "unavailable",
                "message", service + " service is down."
        ));
    }
}