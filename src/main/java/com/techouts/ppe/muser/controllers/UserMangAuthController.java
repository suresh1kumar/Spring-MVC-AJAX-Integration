package com.techouts.ppe.muser.controllers;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.techouts.ppe.muser.dto.PasswordRequestDto;
import com.techouts.ppe.muser.dto.ResponseDto;
import com.techouts.ppe.muser.exceptions.AppException;
import com.techouts.ppe.muser.model.ConfirmationToken;
import com.techouts.ppe.muser.model.Role;
import com.techouts.ppe.muser.model.RoleName;
import com.techouts.ppe.muser.model.User;
import com.techouts.ppe.muser.payload.ApiResponse;
import com.techouts.ppe.muser.payload.JwtAuthenticationResponse;
import com.techouts.ppe.muser.payload.LoginRequest;
import com.techouts.ppe.muser.payload.SignUpRequest;
import com.techouts.ppe.muser.repositories.RoleRepository;
import com.techouts.ppe.muser.repositories.UserRepository;
import com.techouts.ppe.muser.security.JwtTokenProvider;
import com.techouts.ppe.muser.service.impl.RegistrationService;


/**
 * Created by pavan on 11/11/20.
 */
@RestController
@RequestMapping("/api/auth")
public class UserMangAuthController {
	
	protected transient Logger log = LoggerFactory.getLogger(getClass());
	
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    private RegistrationService registrationService;

    
    
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    	log.info(" inside sign in==="+loginRequest.getUsernameOrEmail()+" loginRequest.getPassword()=="+loginRequest.getPassword());
    	Authentication authentication=null;
    	try{
    		authentication= authenticationManager.authenticate(
    				new UsernamePasswordAuthenticationToken(
    						loginRequest.getUsernameOrEmail(),
    						loginRequest.getPassword()
    						));
    	}
    	catch(BadCredentialsException b){
    		return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Bad credentials!"),
                    HttpStatus.UNAUTHORIZED);
    	}
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
    	log.info("Leaving authenticateUser with token value=="+jwt);
       //todo need to move to API gateway
       return  ResponseEntity.ok().header("Access-Control-Expose-Headers","*").body("success");
    }

   
    
    @PostMapping(consumes = "application/json", produces = "application/json", path = "/register")  
    public ResponseEntity<?> userRegister( @RequestBody SignUpRequest registrationRequest) 
    {
		log.info("userRegister ={}",registrationRequest.getEmail());
		ResponseDto statusDto=registrationService.registerS(registrationRequest);
		log.debug("statusDto userRegister ={}",statusDto.getStatus());
		return ResponseEntity.status(statusDto.getStatus()?HttpStatus.OK:HttpStatus.UNPROCESSABLE_ENTITY).
				body(statusDto.getStatus()?
				new ApiResponse(true, statusDto.getMessage()):
					new ApiResponse(statusDto.getStatus(), statusDto.getMessage()));
    }
    
    @PostMapping(consumes = "application/json", produces = "application/json", path = "/confirm")  
    public ResponseEntity<?> userRegisterConfirmPassword(@RequestBody PasswordRequestDto registrationRequest) 
    {
		log.info("userRegisterConfirmPassword ={}",registrationRequest.getToken());
		String token="token";
		ResponseDto statusDto=registrationService.confirmTokenS(token,registrationRequest);
		log.debug("statusDto userRegister ={}",statusDto.getStatus());
		return ResponseEntity.status(statusDto.getStatus()?HttpStatus.OK:HttpStatus.UNPROCESSABLE_ENTITY).
				body(statusDto.getStatus()?
				new ApiResponse(true, statusDto.getMessage()):
					new ApiResponse(statusDto.getStatus(), statusDto.getMessage()));
    }
    
    
    @PostMapping(consumes = "application/json", produces = "application/json", path = "/forgot-password")  
    public ResponseEntity<?> userForgotPassword(@RequestBody User user) 
    {
		log.info("userForgotPassword ={}",user.getEmail());
		ResponseDto statusDto=registrationService.forgotUserPassword(user);
		log.debug("statusDto userForgotPassword ={}",statusDto.getStatus());
		return ResponseEntity.status(statusDto.getStatus()?HttpStatus.OK:HttpStatus.UNPROCESSABLE_ENTITY).
				body(statusDto.getStatus()?
				new ApiResponse(true, statusDto.getMessage()):
					new ApiResponse(statusDto.getStatus(), statusDto.getMessage()));
    }
    
    @PostMapping(consumes = "application/json", produces = "application/json", path = "/confirm-forgot-password")  
    public ResponseEntity<?> confirmUserForgotPassword(@RequestBody PasswordRequestDto registrationRequest) 
    {
		log.info("userRegisterConfirmPassword ={}",registrationRequest.getToken());
		String token="token";
		ResponseDto statusDto=registrationService.confirmForgotUserPassword(token,registrationRequest);
		log.debug("statusDto userRegister ={}",statusDto.getStatus());
		return ResponseEntity.status(statusDto.getStatus()?HttpStatus.OK:HttpStatus.UNPROCESSABLE_ENTITY).
				body(statusDto.getStatus()?
				new ApiResponse(true, statusDto.getMessage()):
					new ApiResponse(statusDto.getStatus(), statusDto.getMessage()));
    }
    
  
    
    @PostMapping("/register1")
    public String register(@RequestBody SignUpRequest registrationRequest) {
		System.out.println("----------------------request>::"+registrationRequest.toString());
		log.info("register ={}",registrationRequest.getEmail());
        return registrationService.register(registrationRequest);
    }

    @PostMapping(path = "confirm1")
    //public String confirm(@RequestParam("token") String token,@RequestBody PasswordRequestDto registrationRequest) {
    public String confirm(@RequestBody PasswordRequestDto registrationRequest) {
    	log.info("register ={}",registrationRequest.getToken());
    	String token="token";
    	return registrationService.confirmToken(token,registrationRequest);
    }
    
    
    
    
    
    
    
    
    
    
    /*@PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    	
    	log.info(" Enter insdie registerUser=="+signUpRequest.getUsername());
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
        
        

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        log.info(" insdie registerUser role checking");
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        
        log.info(" insdie registerUser role checking done");
        //user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")  //api/auth/register
                .buildAndExpand(result.getUsername()).toUri();
        
        log.info(" leaving  insdie registerUserlocation=="+location);
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }*/
}
