package com.techouts.ppe.muser.dto;

import java.io.Serializable;

public class PasswordRequestDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String password;
    private String conformPassword;
    private String token;
    
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public PasswordRequestDto() {
		super();
	}
	
	public PasswordRequestDto(String password, String conformPassword, String token) {
		super();
		this.password = password;
		this.conformPassword = conformPassword;
		this.token = token;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConformPassword() {
		return conformPassword;
	}
	public void setConformPassword(String conformPassword) {
		this.conformPassword = conformPassword;
	}
	@Override
	public String toString() {
		return "PasswordRequestDto [password=" + password + ", conformPassword=" + conformPassword + "]";
	}
    
    

}
