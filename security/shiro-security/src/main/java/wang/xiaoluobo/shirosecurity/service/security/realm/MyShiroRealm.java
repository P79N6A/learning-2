package wang.xiaoluobo.shirosecurity.service.security.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import wang.xiaoluobo.shirosecurity.enums.UserTypeEnum;
import wang.xiaoluobo.shirosecurity.model.RoleEntity;
import wang.xiaoluobo.shirosecurity.model.UserEntity;
import wang.xiaoluobo.shirosecurity.service.UserService;
import wang.xiaoluobo.shirosecurity.service.security.SuccessAuthenticationInfo;
import wang.xiaoluobo.shirosecurity.service.security.token.MyShiroUsernamePasswordToken;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/16
 */
public class MyShiroRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(MyShiroRealm.class);

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserEntity userEntity = (UserEntity) principals.getPrimaryPrincipal();
        try {
            List<RoleEntity> roles = userService.getRoleList();
            for (RoleEntity role : roles) {
                authorizationInfo.addRole(String.valueOf(role.getRoleId()));
                authorizationInfo.addStringPermission(String.valueOf(role.getId()));
            }
        } catch (Exception e) {
            logger.error("doGetAuthorizationInfo err:", e);
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token instanceof MyShiroUsernamePasswordToken) {
            return doGetAuthenticationInfo(token);
        }

        throw new AuthenticationException("无效的登录信息，缺少相应参数");
    }

    /**
     * 通过用户名密码登陆
     */
    private AuthenticationInfo doGetAuthenticationInfo(MyShiroUsernamePasswordToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        List<UserTypeEnum> userTypeEnumList = token.getUserTypeEnumList();

        UserEntity userEntity = userService.getUser(userName, userTypeEnumList);
        if (userEntity == null) {
            return null;
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userEntity,
                userEntity.getPassword(),
                ByteSource.Util.bytes(userEntity.getSalt()),
                getName()
        );

        return authenticationInfo;
    }

    /**
     * 返回成功认证
     */
    public static AuthenticationInfo createSuccessAuthenticationInfo(Object principal, String realmName) {
        SimpleAuthenticationInfo successAuthenticationInfo = new SuccessAuthenticationInfo(
                principal,
                "",
                realmName
        );
        return successAuthenticationInfo;
    }

}
