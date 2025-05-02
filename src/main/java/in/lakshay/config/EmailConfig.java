package in.lakshay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

// email config for sending receipts etc
@Configuration
public class EmailConfig {

    // email server settings from properties
    @Value("${spring.mail.host}")
    private String host;  // smtp server address

    @Value("${spring.mail.port}")
    private int port;  // usually 587 for TLS

    @Value("${spring.mail.username}")
    private String username;  // email address

    @Value("${spring.mail.password}")
    private String password;  // app password (not regular password!)

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;  // almost always true

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttls;  // enable TLS encryption

    // create the mail sender bean
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // additional mail properties
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "true");  // shows debug output - remove in prod?

        return mailSender;
    }

    // TODO: add email templates for receipts
}
