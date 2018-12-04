package wang.xiaoluobo.springsecurity.service;

import wang.xiaoluobo.springsecurity.model.RoleEntity;
import wang.xiaoluobo.springsecurity.model.UserEntity;
import wang.xiaoluobo.springsecurity.model.UserRoleEntity;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
public interface UserService {

    UserEntity addUser(UserEntity userEntity);

    UserEntity getUser(String username);

    List<UserRoleEntity> getUserRoleByUserId(Long userId);

    List<RoleEntity> getRoleList();
}
