package wang.xiaoluobo.shirosecurity.service;

import wang.xiaoluobo.shirosecurity.enums.UserTypeEnum;
import wang.xiaoluobo.shirosecurity.model.RoleEntity;
import wang.xiaoluobo.shirosecurity.model.UserEntity;
import wang.xiaoluobo.shirosecurity.model.UserRoleEntity;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
public interface UserService {

    UserEntity addUser(UserEntity userEntity);

    UserEntity getUser(String username);

    UserEntity getUser(String username, List<UserTypeEnum> userTypeEnumList);

    List<UserRoleEntity> getUserRoleByUserId(Long userId);

    List<RoleEntity> getRoleList();
}
