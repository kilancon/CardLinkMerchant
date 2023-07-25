package com.crm.cardlinkmerchant;

import java.security.Security;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import gr.sdk.spire.utils.common.email.JSSEProvider;

public class MyEmailSender extends Authenticator {
    private final Session session;

    public MyEmailSender(final String user, final String password) {
        String[] configs = this.emailHostConfigurations(user);
        Properties props = new Properties();
        props.put("mail.smtp.auth", configs[0]);
        props.put("mail.smtp.host", configs[1]);
        props.put("mail.smtp.port", configs[2]);
        props.put("mail.smtp.starttls.enable", configs[3]);
        this.session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }

    public void sendEmailWithAttachments(String subject, String sender, String merchantName, String receiver) throws Exception {
        Message message = new MimeMessage(this.session);
        message.setFrom(new InternetAddress(sender, merchantName, "UTF-8"));
        message.setRecipients(RecipientType.TO, InternetAddress.parse(receiver));
        //tinh sau
        message.setSubject("Landing Page URL for Onboarding");
        String url = "https://sandbox.crm.com/landing-page/60731bad-36a3-405d-ab2d-1fd8d0711167";
        message.setText("Dear "+receiver+",\n\nThis email is aimed at providing you with the landing page URL for onboarding. " +
                "You can access this landing page to continue the process.\n\nURL: "+url+
                "\n\nIf you have any questions or need further assistance, please feel free to reach out to us. We are here to help!\n\n" +
                "Thank you for your cooperation, and we look forward to working together.\n\n"+
                "Best regards,\n" +
                "Cardlink");
        Transport.send(message);
    }

    private String[] emailHostConfigurations(String EmailName) {
        String[] configs = new String[4];
        if (EmailName.split("@")[1].contains("gmail")) {
            configs[0] = "true";
            configs[1] = "smtp.gmail.com";
            configs[2] = "587";
            configs[3] = "true";
        } else if (EmailName.split("@")[1].contains("cardlink")) {
            configs[0] = "true";
            configs[1] = "smtp.office365.com";
            configs[2] = "587";
            configs[3] = "true";
        }

        return configs;
    }
    static {
        Security.addProvider(new JSSEProvider());
    }
}

