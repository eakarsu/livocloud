package tr.com.eno.livo.cloud.viewBean;

public class LoginBean {

	private String userMail;

	private String password;

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.userMail;
	}

	public void setUsername(String username) {
		this.userMail = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
