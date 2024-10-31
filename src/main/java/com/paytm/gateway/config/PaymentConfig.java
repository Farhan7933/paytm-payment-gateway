package com.paytm.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment.paytm")
public class PaymentConfig {
    private String merchantId;
    private String merchantKey;
    private String callbackUrl;
    private String transactionTokenUrl;
    private String requestType;
    private String websiteName;
    private String currency;
}
