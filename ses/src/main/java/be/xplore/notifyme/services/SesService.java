package be.xplore.notifyme.communication;

import be.xplore.notifyme.config.SesConfig;
import be.xplore.notifyme.communication.communicationstrategies.IEmailService;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
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

  public Properties getProperties() {
    var props = System.getProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.port", sesConfig.getPort());
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");
    return props;
  }

  public Session getSession(Properties properties) {
    return Session.getDefaultInstance(properties);
  }

  public Transport getTransport(Session session) throws Exception {
    return session.getTransport();
  }

  public MimeMessage createMessage(Session session, String to, String subject, String body)
      throws Exception {
    var msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(sesConfig.getFrom(), sesConfig.getFromName()));
    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
    msg.setSubject(subject);
    msg.setContent(body, "text/html");
    msg.setHeader("X-SES-CONFIGURATION-SET", sesConfig.getConfigSet());
    return msg;
  }

  public void sendMessage(MimeMessage message, Transport transport) throws MessagingException {
    try {
      log.info("Trying to send message...");
      transport
          .connect(sesConfig.getHost(), sesConfig.getSmtpUsername(), sesConfig.getSmtpPassword());
      transport.sendMessage(message, message.getAllRecipients());
      log.info("Message successfully sent!");
    } catch (MessagingException e) {
      log.error(e.getMessage());
    } finally {
      transport.close();
    }
  }

  private String wrapInHtml(String message) {
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
