package com.paytm.gateway.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaytmCallbackRequest {
    @JsonAlias("ORDERID")
    private String orderId;

    @JsonAlias("MID")
    private String mid;

    @JsonAlias("TXNID")
    private String txnId;

    @JsonAlias("TXNAMOUNT")
    private String txnAmount;

    @JsonAlias("RESPCODE")
    private String respCode;

    @JsonAlias("GATEWAYNAME")
    private String gatewayName;

    @JsonAlias("CURRENCY")
    private String currency;

    @JsonAlias("STATUS")
    private String status;

    @JsonAlias("RESPMSG")
    private String respMsg;

    @JsonAlias("TXNDATE")
    private String txnDate;

    @JsonAlias("PAYMENTMODE")
    private String paymentMode;

    @JsonAlias("CHECKSUMHASH")
    private String checksumHash;

    @JsonAlias("BANKTXNID")
    private String bankTxnId;

    @JsonAlias("BANKNAME")
    private String bankName;

    @Override
    public String toString() {
        return "PaytmCallbackRequest {"
                + "ORDERID='" + orderId + '\''
                + ", TXNID='" + txnId + '\''
                + ", TXNAMOUNT='" + txnAmount + '\''
                + ", RESPCODE='" + respCode + '\''
                + ", GATEWAYNAME='" + gatewayName + '\''
                + ", BANKTXNID='" + bankTxnId + '\''
                + ", BANKNAME='" + bankName + '\''
                + ", CURRENCY='" + currency + '\''
                + ", STATUS='" + status + '\''
                + ", RESPMSG='" + respMsg + '\''
                + ", TXNDATE='" + txnDate + '\''
                + ", PAYMENTMODE='" + paymentMode + '\''
                + ", CHECKSUMHASH='" + checksumHash + '\''
                + '}';
    }
}
