package wang.xiaoluobo.shirosecurity.service.security.token;

import org.apache.shiro.authc.UsernamePasswordToken;
import wang.xiaoluobo.shirosecurity.enums.UserTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 解决不同平台的用户登录
 */
public class MyShiroUsernamePasswordToken extends UsernamePasswordToken {

    private List<UserTypeEnum> userTypeEnumList;

    public List<UserTypeEnum> getUserTypeEnumList() {
        return userTypeEnumList;
    }

    public void setUserTypeEnumList(List<UserTypeEnum> userTypeEnumList) {
        this.userTypeEnumList = userTypeEnumList;
    }

    public MyShiroUsernamePasswordToken(String username, String password, UserTypeEnum userTypeEnum) {
        super(username, password);
        this.userTypeEnumList = new ArrayList<>();
        this.userTypeEnumList.add(userTypeEnum);
    }

    public MyShiroUsernamePasswordToken(String username, String password) {
        super(username, password);
    }

    public void addUserEnumType(UserTypeEnum userTypeEnum) {
        this.userTypeEnumList.add(userTypeEnum);
    }
}
