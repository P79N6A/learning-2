package wang.xiaoluobo.webwar.service;

import wang.xiaoluobo.webwar.entity.UsersEntity;
import wang.xiaoluobo.webwar.entity.UsersEntityExample;

import java.util.List;

/**
 * @author wangyd
 * @date 2019/2/1
 */
public interface UsersService {

    List<UsersEntity> getList(UsersEntityExample example);

}
