package com.erikandreas.ecommerce.payment;

import com.erikandreas.ecommerce.customer.CustomerResponse;
import com.erikandreas.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    CustomerResponse customer
) {
}
