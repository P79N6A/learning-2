package wang.xiaoluobo.springsecurity.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wangyd
 * @date 2018/9/1
 */
@Getter
@Setter
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -1621655895366711095L;

    private long userId;
    private String userName;

    /**
     * BCrypt 加密
     */
    private String password;

    /**
     * 单位：毫秒
     */
    private long createTime;
}