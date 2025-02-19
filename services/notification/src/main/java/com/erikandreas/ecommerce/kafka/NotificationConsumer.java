package com.erikandreas.ecommerce.kafka;

import com.erikandreas.ecommerce.email.EmailOrderConfirmationData;
import com.erikandreas.ecommerce.email.EmailPaymentConfirmationData;
import com.erikandreas.ecommerce.email.EmailService;
import com.erikandreas.ecommerce.kafka.order.OrderConfirmation;
import com.erikandreas.ecommerce.kafka.payment.PaymentConfirmation;
import com.erikandreas.ecommerce.notification.Notification;
import com.erikandreas.ecommerce.notification.NotificationRepository;
import com.erikandreas.ecommerce.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.erikandreas.ecommerce.notification.NotificationType.ORDER_CONFIRMATION;
import static com.erikandreas.ecommerce.notification.NotificationType.PAYMENT_CONFIRMATION;
import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository repository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotifications(PaymentConfirmation payment) throws MessagingException {
        logConsumption("payment-topic", payment);
        saveNotification(PAYMENT_CONFIRMATION, payment);
        sendPaymentEmail(payment);
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotifications(OrderConfirmation order) throws MessagingException {
        logConsumption("order-topic", order);
        saveNotification(ORDER_CONFIRMATION, order);
        sendOrderEmail(order);
    }

    private void logConsumption(String topic, Object message) {
        log.info("Consuming the message from {} Topic:: {}", topic, message);
    }

    private void saveNotification(NotificationType type, Object confirmation) {
        repository.save(Notification.builder()
                .type(type)
                .notificationDate(LocalDateTime.now())
                .orderConfirmation(type == ORDER_CONFIRMATION ? (OrderConfirmation) confirmation : null)
                .paymentConfirmation(type == PAYMENT_CONFIRMATION ? (PaymentConfirmation) confirmation : null)
                .build());
    }

    private void sendPaymentEmail(PaymentConfirmation payment) throws MessagingException {
        String customerName = payment.customerFirstname() + " " + payment.customerLastname();
        emailService.sendNotification(EmailPaymentConfirmationData.builder()
                .customerEmail(payment.customerEmail())
                .customerName(customerName)
                .amount(payment.amount())
                .orderReference(payment.orderReference())
                .build());
    }

    private void sendOrderEmail(OrderConfirmation order) throws MessagingException {
        String customerName = order.customer().firstname() + " " + order.customer().lastname();
        emailService.sendNotification(EmailOrderConfirmationData.builder()
                .customerEmail(order.customer().email())
                .customerName(customerName)
                .amount(order.totalAmount())
                .orderReference(order.orderReference())
                .products(order.products())
                .build());
    }
}