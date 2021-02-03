package com.techouts.ppe.muser.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.techouts.ppe.muser.dto.ResponseDto;
import com.techouts.ppe.muser.utils.Constants;

@Service
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    

	@Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("surajmca60@gmail.com.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
	//@Override
    @Async
    public ResponseDto sendS(String to, String email) {
    	ResponseDto internalDto=new ResponseDto();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(Constants.CONFIRM_YOUR_MAIL);
            helper.setFrom("surajmca60@gmail.com.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
           // throw new IllegalStateException("failed to send email");
            internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.EMAIL_NOT_VALID); 
        }
		return internalDto;
    }

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
    
    
}
