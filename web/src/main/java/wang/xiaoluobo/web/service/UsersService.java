package wang.xiaoluobo.web.service;

import wang.xiaoluobo.web.entity.UsersEntity;
import wang.xiaoluobo.web.entity.UsersEntityExample;

import java.util.List;

/**
 * @author wangyd
 * @date 2019/2/1
 */
public interface UsersService {

    List<UsersEntity> getList(UsersEntityExample example);

}
