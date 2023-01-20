package cz.uhk.ppro.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;


@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Override
    @Async //abysme neblokovali klienta
    public void send(String to, String email) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Ověření emailu");
            helper.setFrom("lukasjanou8@seznam.cz");
            mailSender.send(mimeMessage);
        }catch(MessagingException e){
            LOGGER.error("Nepodařilo se odeslat email", e);//abysme neposílali uživateli původní znění výjimky
            throw new IllegalStateException("Nepodařilo se odeslat email");
        }
    }
    public void sendEmailToChangePassword(String to, String link){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("lukasjanou8@seznam.cz", "Galerie modelů F1 Support");
            helper.setSubject("Obnova hesla");
            helper.setTo(to);

            String content = "<p>Ahoj,</p>"
                    + "<p>Zažádal/a jsi obnovu hesla.</p>"
                    + "<p>Pro změnu hesla klikni na následující odkaz:</p>"
                    + "<p><a href=\"" + link + "\">Změnit heslo</a></p>"
                    + "<br>"
                    + "<p>Pokud si heslo pamatuješ nebo jsi nezažádal/a o nové, "
                    + "tak tento email ignoruj.</p>";

            helper.setText(content, true);

            mailSender.send(message);
        }catch(Exception e){
            LOGGER.error("Nepodařilo se odeslat email", e);//abysme neposílali uživateli původní znění výjimky
            throw new IllegalStateException("Nepodařilo se odeslat email");
        }
        }
}
