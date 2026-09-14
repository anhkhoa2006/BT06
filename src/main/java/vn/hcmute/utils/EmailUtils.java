package vn.hcmute.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailUtils {
	public static String generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public static boolean sendEmail(String toEmail, String subject, String messageText) {

		final String fromEmail = "noname1234ey@gmail.com";
		final String password = "ytlk mejt aooc jhbf";

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); 

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(fromEmail));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			msg.setSubject(subject);
			msg.setText(messageText);

			Transport.send(msg);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}
}