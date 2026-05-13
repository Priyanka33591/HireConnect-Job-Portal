package com.hireconnect.subscription.api;

import com.hireconnect.subscription.api.dto.InvoiceResponse;
import com.hireconnect.subscription.api.dto.SubscriptionCreateRequest;
import com.hireconnect.subscription.api.dto.SubscriptionResponse;
import com.hireconnect.subscription.domain.PlanType;
import com.hireconnect.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionsController {
    private final SubscriptionService service;

    @PostMapping("/subscriptions/razorpay-order")
    public ResponseEntity<?> createOrder(@RequestParam Long userId, @RequestParam String userRole, @RequestParam PlanType plan) throws Exception {
        return ResponseEntity.ok(service.createRazorpayOrder(userId, userRole, plan));
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/subscriptions/cancel/{id}")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @GetMapping("/subscriptions/user/{userId}")
    public ResponseEntity<List<SubscriptionResponse>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.byUser(userId));
    }

    @GetMapping("/subscriptions/current")
    public ResponseEntity<SubscriptionResponse> current(@RequestParam Long userId, @RequestParam String userRole) {
        return ResponseEntity.ok(service.currentByUser(userId, userRole));
    }

    @GetMapping("/invoices/user/{userId}")
    public ResponseEntity<List<InvoiceResponse>> invoices(@PathVariable Long userId) {
        return ResponseEntity.ok(service.invoicesByUser(userId));
    }

    @GetMapping("/subscriptions/admin/all")
    public ResponseEntity<List<SubscriptionResponse>> allSubscriptions() {
        return ResponseEntity.ok(service.allSubscriptions());
    }

    @GetMapping("/invoices/admin/all")
    public ResponseEntity<List<InvoiceResponse>> allInvoices() {
        return ResponseEntity.ok(service.allInvoices());
    }
}

