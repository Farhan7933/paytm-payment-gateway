package com.paytm.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.pg.merchant.PaytmChecksum;
import com.wfe.rapid.data.payment.config.PaymentConfig;
import com.wfe.rapid.data.payment.model.request.PaytmCallbackRequest;
import com.wfe.rapid.data.payment.model.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentConfig paymentConfig;
    private final PaymentTransactionService paymentTransactionService;
    private final ObjectMapper objectMapper;

    public ApiResponse processPaymentResponse(PaytmCallbackRequest paytmCallbackRequest) throws Exception {
        String paytmChecksum = paytmCallbackRequest.getChecksumHash();

        boolean isChecksumValid = PaytmChecksum.verifySignature (objectMapper.writeValueAsString (paytmCallbackRequest),
                paymentConfig.getMerchantKey (), paytmChecksum);

        if (isChecksumValid) {
            String orderId = paytmCallbackRequest.getOrderId();
            String status = paytmCallbackRequest.getStatus();

            paymentTransactionService.saveTransaction(paytmCallbackRequest);

            if ("TXN_SUCCESS".equalsIgnoreCase(status)) {
                log.info("Transaction successful for orderId: {}", orderId);
                return ApiResponse.builder().message("SUCCESS").build();
            } else {
                log.error("Transaction failed for orderId: {}", orderId);
                return ApiResponse.builder().message("FAIL").build();
            }
        } else {
            log.error("Checksum validation failed for request: {}", paytmCallbackRequest);
            return ApiResponse.builder().message("FAIL").build();
        }
    }
}
