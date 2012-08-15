package com.chitter.mail;

import com.chitter.model.UserItem;
import com.chitter.utils.MailSender;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import java.util.Properties;

public class PasswordRecoveryMail {

    private final String FROM="webmaster.chitter@gmail.com";
    private final String PASSWORD = "chitterdirecti";
    private final String SUBJECT="Account Recovery Information - Chitter";
    private String to;
    private String text;


    public PasswordRecoveryMail(UserItem userItem, String recoveryUrl){
        this.to = userItem.getEmail();
        setText(userItem,recoveryUrl);
    }

    private void setText(UserItem userItem, String recoveryUrl) {
        this.text="Dear "+userItem.getName()+", \r\n" +
                "There was a request for recovering your password made at http://www.chitter.com \r\n" +
                "Please click on the following link to reset your password: \r\n" +
                recoveryUrl + "\r\n \r\n \r\n" +
                "Please note that the above link will expire in 24 hours for security reasons.";
    }

    public void send() throws AddressException, MessagingException {
            new MailSender(FROM, to, SUBJECT, text, getChitterWebmasterSession(getGmailProperties())).send();
        }

        private Session getChitterWebmasterSession(Properties props) {
            return Session.getDefaultInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM, PASSWORD);
                        }
                    });
        }

        private Properties getGmailProperties() {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            return props;
        }
    }
