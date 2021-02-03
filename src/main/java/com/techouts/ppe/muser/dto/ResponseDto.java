package com.techouts.ppe.muser.dto;

/**
 * 
 * 
 * don't use this dto for UI response. use this dto inside java8-lamda expression
 *   as we can't assign value to variable out side lamda expression  
 *
 */
public class ResponseDto {
	
	private boolean status=true;
	private String errorCode;
	private String message;

	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	

}
