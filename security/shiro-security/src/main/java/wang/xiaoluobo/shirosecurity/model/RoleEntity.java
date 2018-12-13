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
public class RoleEntity implements Serializable {

    private static final long serialVersionUID = -3604590005380570844L;

    private Object id;
    private Long roleId;
    private String name;
    private String description;
    private long createTime;
}
