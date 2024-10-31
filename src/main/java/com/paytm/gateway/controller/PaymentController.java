package com.paytm.gateway.controller;

import com.wfe.rapid.data.config.UserDetailsProvider;
import com.wfe.rapid.data.payment.model.request.PaytmCallbackRequest;
import com.wfe.rapid.data.payment.model.response.ApiResponse;
import com.wfe.rapid.data.payment.model.response.TransactionResponse;
import com.wfe.rapid.data.payment.model.response.TransactionTokenResponse;
import com.wfe.rapid.data.payment.service.PaymentService;
import com.wfe.rapid.data.payment.service.PaymentTransactionService;
import com.wfe.rapid.data.payment.service.PaytmTransactionTokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rapid-data/payment")
public class PaymentController {
    private final UserDetailsProvider userDetailsProvider;
    private final PaymentService paymentService;
    private final PaytmTransactionTokenService paytmTransactionTokenService;
    private final PaymentTransactionService paymentTransactionService;

    @PostMapping("/paytm-callback")
    @Operation(operationId = "paytm-callback")
    public ResponseEntity<ApiResponse> processPaytmResponse(@RequestBody PaytmCallbackRequest paytmCallbackRequest)
            throws Exception {
        return ResponseEntity.ok(paymentService.processPaymentResponse(paytmCallbackRequest));
    }

    @GetMapping("/transaction-status")
    @Operation(operationId = "transaction-status")
    public ResponseEntity<TransactionResponse> getTransactionStatus(@RequestParam String orderId) {
        return ResponseEntity.ok(paymentTransactionService.getTransactionStatus(orderId));
    }

    @GetMapping("/transaction-token")
    @Operation(operationId = "transaction-token")
    public ResponseEntity<TransactionTokenResponse> getTransactionToken(@RequestParam String amount) throws Exception {
        String orderId = paymentTransactionService.saveTransaction(amount);
        return ResponseEntity.ok (paytmTransactionTokenService
                            .generateTransactionToken (orderId, userDetailsProvider.getEmail(), amount));
    }
}
