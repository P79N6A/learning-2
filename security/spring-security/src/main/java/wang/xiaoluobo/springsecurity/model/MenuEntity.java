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
public class MenuEntity implements Serializable {

    private static final long serialVersionUID = -1675223732909397817L;

    private Long id;
    private String name;
    private String url;
    private String roleIds;
    private long createTime;
}
