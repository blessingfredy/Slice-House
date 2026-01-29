package com.slice.service.impl;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.slice.model.User;
import com.slice.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(User user,String to, String link) throws MessagingException, UnsupportedEncodingException {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Verify your Pizza Shop account");
//        message.setText("Click the link to verify your account:\n" + link);
        
        String toaddr = to;
        String fromaddr = "blessingfredy2000@gmail.com";
    	String senderName = "Slice House";
    	String subject = "Verify Registration";
    	String message =
    		    "Dear [[name]],<br><br>" +
    		    "Please click the below link to verify:<br>" +
    		    "<h3><a href=\"[[URL]]\" target=\"_blank\">VERIFY</a></h3>";
    	MimeMessage msg = mailSender.createMimeMessage();
    	MimeMessageHelper helper = new MimeMessageHelper(msg,true);
    	helper.setFrom(fromaddr,senderName);
    	helper.setTo(toaddr);
    	helper.setSubject(subject);
    	message = message.replace("[[name]]", user.getName());
    	String url = link;
    	message = message.replace("[[URL]]", url);
    	helper.setText(message,true);
    	mailSender.send(msg);

    }
}