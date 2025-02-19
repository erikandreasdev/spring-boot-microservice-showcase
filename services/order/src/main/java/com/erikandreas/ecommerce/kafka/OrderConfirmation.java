package com.erikandreas.ecommerce.kafka;

import com.erikandreas.ecommerce.customer.CustomerResponse;
import com.erikandreas.ecommerce.order.PaymentMethod;
import com.erikandreas.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
