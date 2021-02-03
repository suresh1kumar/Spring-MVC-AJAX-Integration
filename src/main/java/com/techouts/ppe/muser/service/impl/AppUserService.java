package com.techouts.ppe.muser.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techouts.ppe.muser.dto.ResponseDto;
import com.techouts.ppe.muser.model.ConfirmationToken;
import com.techouts.ppe.muser.model.User;
import com.techouts.ppe.muser.repositories.UserRepository;
import com.techouts.ppe.muser.utils.Constants;

@Service
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG ="user with email %s not found";

    @Autowired
    private UserRepository appUserRepository;
    //private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;


	@Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return (UserDetails) appUserRepository.findByEmail(email).orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user) {
        boolean userExists = appUserRepository.findByEmail(user.getEmail()).isPresent();

        System.out.println("userExists------------>:"+userExists);
        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.

            throw new IllegalStateException(Constants.EMAIL_ALREADY_TAKEN);
        }
        appUserRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        TODO : SEND EMAIL

        return token;
    }
    
    public ResponseDto signUpUserS(User user) {
        boolean userExists = appUserRepository.findByEmail(user.getEmail()).isPresent();
        ResponseDto internalDto=new ResponseDto();
        System.out.println("userExists------------>:"+userExists);
        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.

            //throw new IllegalStateException(Constants.EMAIL_ALREADY_TAKEN);
            internalDto.setStatus(Boolean.FALSE);
			internalDto.setMessage(Constants.EMAIL_ALREADY_TAKEN); 
        }
        appUserRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        TODO: SEND EMAIL

        return internalDto;
    }
    
    public String conformUserPassword(User appUser) {
        
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setEnabled(Boolean.TRUE);
        appUser.setLocked(Boolean.TRUE);
        appUserRepository.save(appUser);

        return "password success";
    }
    
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
