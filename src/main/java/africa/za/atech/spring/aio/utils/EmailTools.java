package africa.za.atech.spring.aio.utils;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailTools {

    @Value("${atech.app.email.from.display-name}")
    private String fromDisplayName;
    private final JavaMailSender mailSender;

    public OutputTool send(
            List<String> to,
            String subject,
            @Nullable String body,
            @Nullable List<String> cc,
            @Nullable List<String> bcc,
            @Nullable List<MultipartFile> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper emailBuilder = new MimeMessageHelper(message, true);
            emailBuilder.setFrom(fromDisplayName);
            emailBuilder.setTo(to.toArray(String[]::new));
            if (cc != null) {
                if (!cc.isEmpty()) {
                    emailBuilder.setCc(cc.toArray(String[]::new));
                }
            }
            if (bcc != null) {
                if (!bcc.isEmpty()) {
                    emailBuilder.setBcc(bcc.toArray(String[]::new));
                }
            }
            if (subject != null) {
                emailBuilder.setSubject(subject);
            }
            if (body != null) {
                emailBuilder.setText(body, true);
            }
            if (attachments != null) {
                if (!attachments.isEmpty()) {
                    for (MultipartFile f : attachments) {
                        emailBuilder.addAttachment(f.getName(), f);
                    }
                }
            }
            mailSender.send(message);
            return new OutputTool().build(OutputTool.Result.SUCCESS, "", null);
        } catch (MessagingException e) {
            return new OutputTool().build(
                    OutputTool.Result.EXCEPTION,
                    "Email sending failed with error: " + e.getMessage(),
                    null);
        }

    }

}

