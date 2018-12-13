package wang.xiaoluobo.shirosecurity.service.security;

import org.apache.shiro.authc.SimpleAuthenticationInfo;

public class SuccessAuthenticationInfo extends SimpleAuthenticationInfo {

	private static final long serialVersionUID = 799158861736764602L;

	public SuccessAuthenticationInfo(Object principal, Object hashedCredentials, String realmName) {
		super(principal,hashedCredentials,realmName);
	}

}
