package com.techouts.ppe.muser.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.techouts.ppe.muser.dto.PasswordRequestDto;
import com.techouts.ppe.muser.dto.ResponseDto;
import com.techouts.ppe.muser.email.EmailSender;
import com.techouts.ppe.muser.email.EmailValidator;
import com.techouts.ppe.muser.model.ConfirmationToken;
import com.techouts.ppe.muser.model.RoleName;
import com.techouts.ppe.muser.model.User;
import com.techouts.ppe.muser.payload.SignUpRequest;
import com.techouts.ppe.muser.repositories.UserRepository;
import com.techouts.ppe.muser.repositories.UserRepositoryByEmail;
import com.techouts.ppe.muser.utils.Constants;


@Service
//@AllArgsConstructor
public class RegistrationService {

	protected transient Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
    private AppUserService appUserService;
	@Autowired
    private EmailValidator emailValidator;
	@Autowired
    private ConfirmationTokenService confirmationTokenService;
	@Autowired
    private EmailSender emailSender;

    @Autowired
    private UserRepository appUserRepository;//findByUsername
    
    @Autowired
    private UserRepositoryByEmail userRepositoryByEmail;
    
    public RegistrationService(AppUserService appUserService, EmailValidator emailValidator,
			ConfirmationTokenService confirmationTokenService, EmailSender emailSender) {
		super();
		this.appUserService = appUserService;
		this.emailValidator = emailValidator;
		this.confirmationTokenService = confirmationTokenService;
		this.emailSender = emailSender;
	}

    public ResponseDto registerS(SignUpRequest request) {
    	log.info("registerS request={}",request.getEmail());
        boolean isValidEmail = emailValidator.test(request.getEmail());
        ResponseDto internalDto=new ResponseDto();
        Optional<User> userName=appUserRepository.findByUsername(request.getUsername());
        
        User user=new User();
        boolean userExists = appUserRepository.findByEmail(request.getEmail()).isPresent();
        
        if (isValidEmail) {
        if(!userName.isPresent()){
        	if (!userExists) {
                // TODO check of attributes are the same and
                // TODO if email not confirmed send confirmation email.
        		
        		user.setName(request.getName());
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setRoleName(RoleName.ROLE_USER);
                appUserRepository.save(user);
                String token = UUID.randomUUID().toString();
                ConfirmationToken confirmationToken = new ConfirmationToken(
                        token,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(3),
                        user);
               confirmationTokenService.saveConfirmationToken(confirmationToken);
                
               internalDto.setMessage(Constants.REGISTER_SUCCESSFULL+"Token : "+token);
               String link = "http://localhost:9099/api/v1/registration/confirm?token=" + token;
               emailSender.send(
                       request.getEmail(),
                       buildEmail(request.getUsername(), link));
            }else {
            	internalDto.setStatus(Boolean.FALSE);
    			internalDto.setMessage(Constants.EMAIL_ALREADY_TAKEN);
            }
        }else {
        	internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.USER_NAME_ALREADY_EXIST); 
        }
        }else {
        	internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.EMAIL_NOT_VALID);
        }
        
         /*String token =	appUserService.signUpUserS(
                new User(request.getName(),
                        request.getUsername(),
                        request.getEmail(),
                        RoleName.ROLE_USER)
        				);*/
        
        return internalDto;
    }
    
    @Transactional
    public ResponseDto confirmTokenS(String token,PasswordRequestDto request) {
    	ResponseDto internalDto=new ResponseDto();
		log.info("confirmTokenS={}",request.getToken());
        //ConfirmationToken confirmationToken = confirmationTokenService.getToken(request.getToken()).orElseThrow(() -> new IllegalStateException(Constants.TOKEN_NOT_FOUND));
        
        Optional<ConfirmationToken> confirmationToken=confirmationTokenService.getToken(request.getToken());
        
        if(confirmationToken.isPresent()){
        	LocalDateTime expiredAt = confirmationToken.get().getExpiresAt();
        	if (confirmationToken.get().getConfirmedAt() == null) {
        		
        		if (!expiredAt.isBefore(LocalDateTime.now())) {
        			 String userEmail= confirmationToken.get().getUser().getEmail();
        		        User user=userRepositoryByEmail.findByEmail(userEmail);
        		        user.setPassword(request.getPassword());
        		        confirmationTokenService.setConfirmedAt(request.getToken());
        		        appUserService.enableAppUser(confirmationToken.get().getUser().getEmail());
        		        
        		        String userData= appUserService.conformUserPassword(user);
        		        log.info("confirmTokenS={}",userData);
        		        internalDto.setMessage(Constants.CONFORM_PASSWOR_CHANGE);
        		}else {
        			internalDto.setStatus(Boolean.FALSE);
        			internalDto.setMessage(Constants.TOKEN_EXPIRED);
        		}
        	}else {
        		internalDto.setStatus(Boolean.FALSE);
    			internalDto.setMessage(Constants.EMAIL_ALREADY_CONFIRMED);
        	}
        }else {
        	internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.TOKEN_NOT_FOUND);
        }
        return internalDto;
    }
    
    
    public ResponseDto forgotUserPassword(User user) {
    	ResponseDto internalDto=new ResponseDto();
		log.info("confirmTokenS={}",user.getEmail());
		boolean userExists = appUserRepository.findByEmail(user.getEmail()).isPresent();
		
		if(userExists) {
			Optional<User> users=appUserRepository.findByEmail(user.getEmail());
			if(users.isPresent()){
			 String token = UUID.randomUUID().toString();
             ConfirmationToken confirmationToken = new ConfirmationToken(
                     token,
                     LocalDateTime.now(),
                     LocalDateTime.now().plusMinutes(3),
                     users.get());
            confirmationTokenService.saveConfirmationToken(confirmationToken);
             
            internalDto.setMessage(Constants.REQUEST_RESET_PASSWORD_RECEIVED_MAIL + ", Token : " + token);
            String link = "http://localhost:9099/api/v1/forgot-password?token=" + token;
            emailSender.send(
                    users.get().getEmail(),
                    buildEmail(users.get().getUsername(), link));
			}else {
				internalDto.setStatus(Boolean.FALSE);
				internalDto.setMessage(Constants.USER_NAME_DOES_NOT_EXIST);
			}
		}else {
			internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.EMAIL_DOES_NOT_EXIST);
		}
		return internalDto;
    }
    
   

    @Transactional
    public ResponseDto confirmForgotUserPassword(String token,PasswordRequestDto request) {
    	ResponseDto internalDto=new ResponseDto();
		log.info("confirmTokenS={}",request.getToken());
        //ConfirmationToken confirmationToken = confirmationTokenService.getToken(request.getToken()).orElseThrow(() -> new IllegalStateException(Constants.TOKEN_NOT_FOUND));
        
        Optional<ConfirmationToken> confirmationToken=confirmationTokenService.getToken(request.getToken());
        
        if(confirmationToken.isPresent()){
        	LocalDateTime expiredAt = confirmationToken.get().getExpiresAt();
        	if (confirmationToken.get().getConfirmedAt() == null) {
        		
        		if (!expiredAt.isBefore(LocalDateTime.now())) {
        			 String userEmail= confirmationToken.get().getUser().getEmail();
        		        User user=userRepositoryByEmail.findByEmail(userEmail);
        		        user.setPassword(request.getPassword());
        		        confirmationTokenService.setConfirmedAt(request.getToken());
        		        appUserService.enableAppUser(confirmationToken.get().getUser().getEmail());
        		        
        		        String userData= appUserService.conformUserPassword(user);
        		        log.info("confirmTokenS={}",userData);
        		        internalDto.setMessage(Constants.CONFORM_PASSWOR_CHANGE);
        		}else {
        			internalDto.setStatus(Boolean.FALSE);
        			internalDto.setMessage(Constants.TOKEN_EXPIRED);
        		}
        	}else {
        		internalDto.setStatus(Boolean.FALSE);
    			internalDto.setMessage(Constants.EMAIL_ALREADY_CONFIRMED);
        	}
        }else {
        	internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.TOKEN_NOT_FOUND);
        }
        return internalDto;
    }
    
	public String register(SignUpRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException(Constants.EMAIL_NOT_VALID);
        }
        String token = appUserService.signUpUser(
                new User(request.getName(),
                        request.getUsername(),
                        request.getEmail(),
                        RoleName.ROLE_USER)
        				);
        log.info("confirmTokenS token={}",token);
        String link = "http://localhost:2025/api/v1/registration/confirm?token=" + token;
        emailSender.send(
                request.getEmail(),
                buildEmail(request.getUsername(), link));

        return token;
    }

   

	@Transactional
    public String confirmToken(String token,PasswordRequestDto request) {
		log.info("confirmToken token={}",token);
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(request.getToken())
                .orElseThrow(() -> new IllegalStateException(Constants.TOKEN_NOT_FOUND));
        
        String userEmail= confirmationToken.getUser().getEmail();
        User user=userRepositoryByEmail.findByEmail(userEmail);
        user.setPassword(request.getPassword());
       
        
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException(Constants.EMAIL_ALREADY_CONFIRMED);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(Constants.TOKEN_EXPIRED);
        }
        confirmationTokenService.setConfirmedAt(request.getToken());
        appUserService.enableAppUser(confirmationToken.getUser().getEmail());
        
        String userData= appUserService.conformUserPassword(user);
        log.info("confirmTokenS userData={}",userData);
        return "confirmed";
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
