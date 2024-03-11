package podsofkon.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class SendMail {

    @Autowired
    ResourceLoader resourceLoader;
//    @Bean
    public String send(String toEmail, String messageText) {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);

            mailSender.setUsername("paul.parkinson@gmail.com");
            mailSender.setPassword("asdf");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            // https://docs.spring.io/spring-framework/docs/3.0.x/spring-framework-reference/html/mail.html
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setReplyTo("asdf@asdf.com");
            helper.setFrom("asdf@asdf.com");
            helper.setSubject("Welcome to asdf!");
            helper.setText("<html><body>" +
                    "<br>" +
                    messageText +
                    "<br>" +
//                    "<img src='cid:identifier1234'>" +
                    "</body></html>", true);
        //todo mail doesn't send when adding this...
//            Resource resource = resourceLoader.getResource("classpath:xasdflogo.png");
//            InputStream input = resource.getInputStream();
//            File file = resource.getFile();
//            FileSystemResource res = new FileSystemResource(file);
//            FileSystemResource res = new FileSystemResource(new File("c:/Sample.jpg"));
//            helper.addInline("identifier1234", res);

//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom("support@xrcloudservices.com");
//            message.setSubject("Welcome to XRCloud Services!");
//            message.setText(messageText);
//            message.setTo(toEmail);
            mailSender.send(message);
        } catch (Exception ex) {
            return "unsuccessful ex:" + ex.getMessage(); //com.sun.mail.smtp.SMTPAddressFailedException
        }
        return "successful";
    }
}
