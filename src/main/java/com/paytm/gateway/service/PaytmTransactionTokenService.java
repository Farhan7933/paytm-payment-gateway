package com.paytm.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.pg.merchant.*;
import com.wfe.rapid.data.payment.config.PaymentConfig;
import com.wfe.rapid.data.payment.exception.PaymentException;
import com.wfe.rapid.data.payment.model.request.*;
import com.wfe.rapid.data.payment.model.response.PaytmResponse;
import com.wfe.rapid.data.payment.model.response.TransactionTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaytmTransactionTokenService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final PaymentConfig paymentConfig;

    public TransactionTokenResponse generateTransactionToken(String orderId, String custId, String txnAmount)
            throws Exception {
        TransactionRequest transactionRequest = getTransactionRequest(orderId, custId, txnAmount);

        RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(transactionRequest),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(paymentConfig.getTransactionTokenUrl()
                        + "?mid=" + paymentConfig.getMerchantId()
                        + "&orderId=" + orderId)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (Objects.nonNull(response.body())) {
                    PaytmResponse paytmResponse = objectMapper.readValue(response.body().string(), PaytmResponse.class);

                    if ("S".equalsIgnoreCase(paytmResponse.getBody().getResultInfo().getResultStatus())) {
                        return TransactionTokenResponse.builder()
                                .merchantId(paymentConfig.getMerchantId())
                                .orderId(orderId)
                                .transactionToken(paytmResponse.getBody().getTxnToken())
                                .build();
                    } else {
                        String errorMessage = "Failed to create transaction token for orderId: "
                                + orderId + ", customerId: "
                                + custId + ". Msg: "
                                + response.message();

                        log.error("Failed Paytm API response: {}", response.body());
                        log.error(errorMessage);

                        throw new PaymentException("Failed to create transaction token for orderId="
                                + orderId + ", custId="
                                + custId, "500");
                    }
                } else {
                    log.info("Exception occurred while creating transaction token for orderId: {}, custId: {}",
                            orderId, custId);
                }
            } else {
                String errorMessage = "Exception occurred while creating transaction token for orderId: "
                        + orderId + ", customerId: "
                        + custId + "-> "
                        + response.message();

                throw new IOException(errorMessage);
            }
        }

        throw new PaymentException("Failed to create transaction token for orderId: "
                + orderId + ", customerId: "
                + custId, "500");
    }

    private TransactionRequest getTransactionRequest(String orderId, String custId, String txnAmount) throws Exception {
        Body body = Body.builder()
                .requestType(paymentConfig.getRequestType())
                .websiteName(paymentConfig.getWebsiteName())
                .mid(paymentConfig.getMerchantId())
                .orderId(orderId)
                .txnAmount(TxnAmount.builder().value(txnAmount).currency(paymentConfig.getCurrency()).build())
                .userInfo(UserInfo.builder().custId(custId).build())
                .build();

        return TransactionRequest.builder()
                .body(body)
                .head(Head.builder().signature(getChecksum(body)).build())
                .build();
    }

    private String getChecksum(Body body) throws Exception {
        return PaytmChecksum.generateSignature(objectMapper.writeValueAsString(body), paymentConfig.getMerchantKey());
    }
}
