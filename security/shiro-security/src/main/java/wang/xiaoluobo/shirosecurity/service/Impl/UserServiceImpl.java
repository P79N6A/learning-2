package wang.xiaoluobo.shirosecurity.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import wang.xiaoluobo.shirosecurity.enums.UserTypeEnum;
import wang.xiaoluobo.shirosecurity.model.RoleEntity;
import wang.xiaoluobo.shirosecurity.model.UserEntity;
import wang.xiaoluobo.shirosecurity.model.UserRoleEntity;
import wang.xiaoluobo.shirosecurity.service.UserService;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String USERS_COLLECTION_NAME = "users";
    private static final String USER_ROLE_COLLECTION_NAME = "user_role";
    private static final String ROLE_COLLECTION_NAME = "roles";

    @Override
    public UserEntity addUser(UserEntity userEntity) {
        userEntity.setCreateTime(System.currentTimeMillis());
        mongoTemplate.insert(userEntity, USERS_COLLECTION_NAME);
        return userEntity;
    }

    @Override
    public UserEntity getUser(String username) {
        if(StringUtils.isBlank(username)){
            return null;
        }

        Query query = new Query(Criteria.where("userName").is(username));
        query.limit(1);
        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class, USERS_COLLECTION_NAME);
        return userEntity;
    }

    @Override
    public UserEntity getUser(String username, List<UserTypeEnum> userTypeEnumList) {
        if(StringUtils.isBlank(username)){
            return null;
        }

        Query query = new Query(Criteria.where("userName").is(username));
        query.limit(1);
        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class, USERS_COLLECTION_NAME);
        return userEntity;
    }

    @Override
    public List<UserRoleEntity> getUserRoleByUserId(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        List<UserRoleEntity> userRoleEntityList = mongoTemplate.find(query, UserRoleEntity.class, USER_ROLE_COLLECTION_NAME);
        return userRoleEntityList;
    }

    @Override
    public List<RoleEntity> getRoleList() {
        List<RoleEntity> roleEntityList = mongoTemplate.findAll(RoleEntity.class, ROLE_COLLECTION_NAME);
        return roleEntityList;
    }
}
