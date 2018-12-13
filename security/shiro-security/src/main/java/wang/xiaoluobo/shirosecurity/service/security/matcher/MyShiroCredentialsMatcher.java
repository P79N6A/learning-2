package wang.xiaoluobo.shirosecurity.service.security.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import wang.xiaoluobo.shirosecurity.service.security.SuccessAuthenticationInfo;

/**
 * @author wangyd
 * @date 2018/9/16
 */
public class MyShiroCredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if (info instanceof SuccessAuthenticationInfo) {
            return true;
        }

        UsernamePasswordToken utoken = (UsernamePasswordToken) token;
        String plainPassword = new String(utoken.getPassword());
        String salt = new String(((SimpleAuthenticationInfo) info).getCredentialsSalt().getBytes());
        // 获得数据库中的密码
        String dbPassword = (String) info.getCredentials();
        // 进行密码的比对

        return true;
    }
}
