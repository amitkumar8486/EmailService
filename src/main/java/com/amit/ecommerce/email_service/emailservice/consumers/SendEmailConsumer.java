package com.amit.ecommerce.email_service.emailservice.consumers;

import com.amit.ecommerce.email_service.emailservice.dtos.EmailDto;
import com.amit.ecommerce.email_service.emailservice.utils.EmailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Service
public class SendEmailConsumer {

    @Autowired
    private ObjectMapper objectMapper; // to deserialize the message i.e. get to, from, body etc. from message.

    @Autowired
    private EmailUtil emailUtil;
    /*groupId ensures that only one service will send email if we have multiple services running. */
    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmail(String message) throws JsonProcessingException {

        EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailDto.getFrom(), "pporgztfmqzklzhg");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());


    }

}
