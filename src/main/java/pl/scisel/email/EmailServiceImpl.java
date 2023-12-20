package pl.scisel.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private MessageSource messageSource;

    @Value("${spring.mail.from.address}")
    private String fromAddress;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String to, String subjectKey, String bodyKey) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage(subjectKey, null, locale);
        String body = messageSource.getMessage(bodyKey, null, locale);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress); // Opcjonalnie, adres nadawcy
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }

    }
}
