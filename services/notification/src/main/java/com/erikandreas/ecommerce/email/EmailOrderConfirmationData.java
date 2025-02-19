package com.erikandreas.ecommerce.email;

import com.erikandreas.ecommerce.kafka.order.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record EmailOrderConfirmationData(
        EmailTemplate template,
        String customerEmail,
        String customerName,
        BigDecimal amount,
        String orderReference,
        List<Product> products
) implements EmailData {
    public EmailOrderConfirmationData {
        if (template == null) {
            template = EmailTemplate.ORDER_CONFIRMATION;
        }
    }
}
