package com.erikandreas.ecommerce.notification;

import com.erikandreas.ecommerce.email.EmailData;
import com.erikandreas.ecommerce.email.EmailTemplate;
import com.erikandreas.ecommerce.kafka.order.Product;
import jakarta.mail.MessagingException;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public interface NotificationService
{
    void sendNotification(EmailData data) throws MessagingException;
}
