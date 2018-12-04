package wang.xiaoluobo.springsecurity.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wang.xiaoluobo.springsecurity.model.RoleEntity;
import wang.xiaoluobo.springsecurity.model.UserEntity;
import wang.xiaoluobo.springsecurity.model.UserRoleEntity;
import wang.xiaoluobo.springsecurity.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userService.getUser(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("用户名或密码不正确！");
        }

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList();
        List<UserRoleEntity> userRoleEntityList = userService.getUserRoleByUserId(userEntity.getUserId());

        List<RoleEntity> roleEntityList = userService.getRoleList();
        for (UserRoleEntity userRoleEntity : userRoleEntityList) {
            String roleName = "";
            for(RoleEntity roleEntity: roleEntityList){
                if(userRoleEntity.getRoleId() == roleEntity.getRoleId()){
                    roleName = roleEntity.getName();
                    break;
                }
            }
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + roleName));
        }

        User userDetails = new User(userEntity.getUserName(), userEntity.getPassword(), authorities);
        return userDetails;
    }
}
