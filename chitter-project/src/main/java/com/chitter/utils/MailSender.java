package com.chitter.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 15/8/12
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
    public class MailSender{

        private InternetAddress from,to;
        private String subject,text;
        private Session mailSession;

        public MailSender(String from, String to, String subject, String text, Session mailSession) throws AddressException {
            this.from = new InternetAddress(from);
            this.to = new InternetAddress(to);
            this.subject = subject;
            this.text = text;
            this.mailSession = mailSession;
        }

        public void send() throws MessagingException {
            Message simpleMessage = getSimpleMessage(mailSession);
            Transport.send(simpleMessage);
        }

        private Message getSimpleMessage(Session mailSession) throws MessagingException {
            Message simpleMessage = new MimeMessage(mailSession);

            simpleMessage.setFrom(from);
            simpleMessage.setRecipient(Message.RecipientType.TO, to);
            simpleMessage.setSubject(subject);
            simpleMessage.setText(text);
            return simpleMessage;
        }

    }
