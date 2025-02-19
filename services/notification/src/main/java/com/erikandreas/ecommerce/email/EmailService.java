package com.erikandreas.ecommerce.email;

import com.erikandreas.ecommerce.notification.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements NotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    @Override
    @Async
    public void sendNotification(
            EmailData data
    ) throws MessagingException {

        Context context = prepareTemplateContext(data);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                UTF_8.name()
        );
        String content = templateEngine.process(
                data
                        .template()
                        .getTemplate(),
                context
        );

        helper.setTo(data.customerEmail());
        helper.setSubject(data
                                  .template()
                                  .getSubject());
        helper.setText(
                content,
                true
        );

        mailSender.send(mimeMessage);
        log.info(
                "Email sent to {} using template {}",
                data.customerEmail(),
                data
                        .template()
                        .getTemplate()
        );
    }

    private Context prepareTemplateContext(EmailData data) {
        Context context = new Context();
        context.setVariables(objectMapper.convertValue(
                data,
                Map.class
        ));
        return context;
    }

}