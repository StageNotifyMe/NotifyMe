package be.xplore.notifyme.services;

import be.xplore.notifyme.config.SesConfig;
import be.xplore.notifyme.services.communicationstrategies.IEmailService;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SesService implements IEmailService {

  private final SesConfig sesConfig;

  private Properties getProperties() {
    var props = System.getProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.port", sesConfig.getPort());
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");
    return props;
  }

  private Session getSession(Properties properties) {
    return Session.getDefaultInstance(properties);
  }

  private Transport getTransport(Session session) throws NoSuchProviderException {
    return session.getTransport();
  }

  private MimeMessage createMessage(Session session, String to, String subject, String body)
      throws UnsupportedEncodingException, MessagingException {
    var msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(sesConfig.getFrom(), sesConfig.getFromName()));
    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
    msg.setSubject(subject);
    msg.setContent(body, "text/html");
    msg.setHeader("X-SES-CONFIGURATION-SET", sesConfig.getConfigSet());
    return msg;
  }


  private void sendMessage(MimeMessage message, Transport transport) {
    try (transport) {
      log.info("Trying to send message...");
      transport
          .connect(sesConfig.getHost(), sesConfig.getSmtpUsername(), sesConfig.getSmtpPassword());
      transport.sendMessage(message, message.getAllRecipients());
      log.info("Message successfully sent!");
    } catch (MessagingException e) {
      log.error(e.getMessage());
    }
  }

  private String wrapInHtml(String message) {
    message = message.replaceAll("\n", "<br>");
    return String.format("<p>%s</p>", message);
  }

  @Override
  public void send(Object emailAddress, be.xplore.notifyme.domain.Message message) {
    try {
      var session = getSession(getProperties());
      var transport = getTransport(session);
      var emailMsg = createMessage(session, (String) emailAddress, message.getTitle(),
          wrapInHtml(message.getText()));
      sendMessage(emailMsg, transport);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
