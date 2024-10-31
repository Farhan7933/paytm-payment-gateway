package com.paytm.gateway.service;

import com.wfe.rapid.data.config.UserDetailsProvider;
import com.wfe.rapid.data.entity.User;
import com.wfe.rapid.data.payment.entity.PaymentTransaction;
import com.wfe.rapid.data.payment.exception.PaymentException;
import com.wfe.rapid.data.payment.model.request.PaytmCallbackRequest;
import com.wfe.rapid.data.payment.model.response.TransactionResponse;
import com.wfe.rapid.data.payment.repository.PaymentTransactionRepository;
import com.wfe.rapid.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.webjars.NotFoundException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserDetailsProvider userDetailsProvider;

    @Transactional
    @SneakyThrows
    public String saveTransaction(String amount) {
        Optional<User> user = userRepository.findByEmail(userDetailsProvider.getEmail());

        if (user.isEmpty()) {
            throw new NotFoundException("User Not Found for email: " + userDetailsProvider.getEmail());
        }

        PaymentTransaction paymentTransaction = PaymentTransaction
                .builder()
                .customer(user.get())
                .amount(amount)
                .build();

        PaymentTransaction savedTransaction = transactionRepository.save(paymentTransaction);

        if (Double.parseDouble(amount) < 0) {
            throw new IllegalArgumentException("Amount can't be negative");
        }

        return String.valueOf(savedTransaction.getOrderId());
    }

    @Transactional
    @SneakyThrows
    public void saveTransaction(PaytmCallbackRequest paytmCallbackRequest) {
        Optional<User> user = userRepository.findByEmail(userDetailsProvider.getEmail());

        if (user.isEmpty()) {
            throw new NotFoundException("User Not Found for email: " + userDetailsProvider.getEmail());
        }

        PaymentTransaction paymentTransaction = PaymentTransaction
                .builder()
                .orderId(Long.valueOf(paytmCallbackRequest.getOrderId()))
                .customer(user.get())
                .amount(paytmCallbackRequest.getTxnAmount())
                .paytmTransactionId(paytmCallbackRequest.getTxnId())
                .transactionStatus(paytmCallbackRequest.getStatus())
                .gatewayName(paytmCallbackRequest.getGatewayName())
                .currency(paytmCallbackRequest.getCurrency())
                .transactionDate(paytmCallbackRequest.getTxnDate())
                .paymentMode(paytmCallbackRequest.getPaymentMode())
                .bankTransactionId(paytmCallbackRequest.getBankTxnId())
                .bankName(paytmCallbackRequest.getBankName())
                .build();

        transactionRepository.save(paymentTransaction);
    }

    @SneakyThrows
    public TransactionResponse getTransactionStatus(String orderId) {
        try {
            Optional<PaymentTransaction> transaction = transactionRepository.findById(Long.parseLong(orderId));

            if (transaction.isPresent()) {
                String status = transaction.get().getTransactionStatus();
                if (!StringUtils.hasLength(status)) {
                    status = "PENDING";
                }
                return TransactionResponse.builder().status(status).build();
            } else {
                return TransactionResponse.builder().status("orderId not found in database").build();
            }
        } catch (NumberFormatException ex) {
            log.error("Invalid orderId: {}", orderId);
            throw new PaymentException("Invalid orderId", "400");
        }
    }
}
