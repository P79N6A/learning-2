package wang.xiaoluobo.shirosecurity.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wangyd
 * @date 2018/9/1
 */
@Getter
@Setter
public class UserRoleEntity implements Serializable {

    private static final long serialVersionUID = 5246740254759813260L;

    private Object id;

    private Long userRoleId;

    private Long userId;

    private Long roleId;
}
