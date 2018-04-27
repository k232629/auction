package com.karaiman.shoppingcart.form;

import com.karaiman.shoppingcart.entity.Account;

public class RegistrationForm {
	
    private String userName;
    private String password;
    
    public RegistrationForm(Account account) {
    		this.userName = account.getUserName();
    		this.password = account.getEncrytedPassword();
    	
    }
    
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    

}
