package com.erikandreas.ecommerce.email;

import java.math.BigDecimal;

public sealed interface EmailData permits EmailOrderConfirmationData, EmailPaymentConfirmationData {
    // Match exactly with record component names
    EmailTemplate template();
    String customerEmail();
    String customerName();
    BigDecimal amount();
    String orderReference();
}
