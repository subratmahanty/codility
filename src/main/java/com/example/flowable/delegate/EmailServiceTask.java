package com.example.flowable.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("emailServiceTask")
public class EmailServiceTask implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceTask.class);

    @Override
    public void execute(DelegateExecution execution) {
        String recipient = (String) execution.getVariable("recipient");
        String subject = (String) execution.getVariable("subject");
        String body = (String) execution.getVariable("body");

        // Simulate email sending
        logger.info("Sending email to: {}", recipient);
        logger.info("Subject: {}", subject);
        logger.info("Body: {}", body);

        // Set a variable to indicate the email was sent
        execution.setVariable("emailSent", true);
        execution.setVariable("emailSentTime", new java.util.Date());
        
        logger.info("Email sent successfully to {}", recipient);
    }
}