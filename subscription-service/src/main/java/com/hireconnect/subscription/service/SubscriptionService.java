package com.hireconnect.subscription.service;

import com.hireconnect.subscription.api.dto.InvoiceResponse;
import com.hireconnect.subscription.api.dto.SubscriptionCreateRequest;
import com.hireconnect.subscription.api.dto.SubscriptionResponse;
import com.hireconnect.subscription.domain.*;
import com.hireconnect.subscription.repo.InvoiceRepository;
import com.hireconnect.subscription.repo.SubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepo;
    private final InvoiceRepository invoiceRepo;

    @Value("${hireconnect.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${hireconnect.razorpay.key-secret}")
    private String razorpayKeySecret;

    @Transactional
    public Map<String, Object> createRazorpayOrder(Long userId, String userRole, PlanType plan) throws Exception {
        int amount = getAmount(plan);
        if (amount == 0) {
            // Free plan doesn't need Razorpay
            throw new IllegalArgumentException("FREE plan doesn't require payment");
        }

        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount); // amount in the smallest currency unit (paise)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("key", razorpayKeyId);
        return response;
    }

    @Transactional
    public SubscriptionResponse create(SubscriptionCreateRequest req) {
        log.info("Creating subscription for user: {}, role: {}, plan: {}", req.getUserId(), req.getUserRole(), req.getPlan());
        // Verification of Razorpay signature
        if (req.getPlan() != PlanType.FREE) {
            verifyPayment(req);
        }

        Subscription s = subscriptionRepo.findByUserIdAndUserRole(req.getUserId(), req.getUserRole())
                .orElse(Subscription.builder()
                        .userId(req.getUserId())
                        .userRole(req.getUserRole())
                        .build());

        s.setPlan(req.getPlan());
        s.setStatus(SubscriptionStatus.ACTIVE);
        s.setRenewedAt(Instant.now());
        
        s = subscriptionRepo.save(s);
        log.info("Subscription saved: {}", s.getId());
        createInvoice(s, req.getRazorpayOrderId());
        return toResponse(s);
    }

    private void verifyPayment(SubscriptionCreateRequest req) {
        log.info("Verifying payment for order: {}, payment: {}", req.getRazorpayOrderId(), req.getRazorpayPaymentId());
        try {
            String secret = razorpayKeySecret;
            String orderId = req.getRazorpayOrderId();
            String paymentId = req.getRazorpayPaymentId();
            String signature = req.getRazorpaySignature();

            String data = orderId + "|" + paymentId;
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String generatedSignature = sb.toString();

            if (!generatedSignature.equals(signature)) {
                log.error("Signature mismatch! Generated: {}, Received: {}", generatedSignature, signature);
                throw new RuntimeException("Invalid payment signature");
            }
            log.info("Payment signature verified successfully");
        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    @Transactional
    public SubscriptionResponse cancel(Long id) {
        Subscription s = subscriptionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));
        s.setStatus(SubscriptionStatus.CANCELLED);
        return toResponse(s);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> byUser(Long userId) {
        return subscriptionRepo.findByUserIdOrderByRenewedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse currentByUser(Long userId, String userRole) {
        return subscriptionRepo.findByUserIdAndUserRole(userId, userRole)
                .map(this::toResponse)
                .orElse(SubscriptionResponse.builder()
                        .userId(userId)
                        .userRole(userRole)
                        .plan(PlanType.FREE)
                        .status(SubscriptionStatus.ACTIVE)
                        .build());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> invoicesByUser(Long userId) {
        return invoiceRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> allSubscriptions() {
        return subscriptionRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> allInvoices() {
        return invoiceRepo.findAll().stream().map(this::toResponse).toList();
    }

    private void createInvoice(Subscription s, String razorpayOrderId) {
        int amount = getAmount(s.getPlan());
        Invoice inv = Invoice.builder()
                .userId(s.getUserId())
                .userRole(s.getUserRole())
                .subscriptionId(s.getId())
                .plan(s.getPlan())
                .amountCents(amount)
                .razorpayOrderId(razorpayOrderId != null ? razorpayOrderId : "N/A")
                .build();
        invoiceRepo.save(inv);
    }

    private int getAmount(PlanType plan) {
        return switch (plan) {
            case FREE -> 0;
            case MONTHLY_99 -> 9900; // 99.00 in paise
            case MONTHLY_199 -> 19900; // 199.00 in paise
        };
    }

    private SubscriptionResponse toResponse(Subscription s) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .userRole(s.getUserRole())
                .plan(s.getPlan())
                .status(s.getStatus())
                .startedAt(s.getStartedAt())
                .renewedAt(s.getRenewedAt())
                .build();
    }

    private InvoiceResponse toResponse(Invoice i) {
        return InvoiceResponse.builder()
                .id(i.getId())
                .userId(i.getUserId())
                .userRole(i.getUserRole())
                .subscriptionId(i.getSubscriptionId())
                .plan(i.getPlan())
                .amountCents(i.getAmountCents())
                .razorpayOrderId(i.getRazorpayOrderId())
                .createdAt(i.getCreatedAt())
                .build();
    }
}

