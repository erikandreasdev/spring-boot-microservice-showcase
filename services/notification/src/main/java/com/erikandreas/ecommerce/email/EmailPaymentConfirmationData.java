package com.erikandreas.ecommerce.email;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
public record EmailPaymentConfirmationData(
        EmailTemplate template,
        String customerEmail,
        String customerName,
        BigDecimal amount,
        String orderReference
) implements EmailData {
    public EmailPaymentConfirmationData {
        if (template == null) {
            template = EmailTemplate.PAYMENT_CONFIRMATION;
        }
    }
}
