package wang.xiaoluobo.webwar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.xiaoluobo.webwar.entity.UsersEntity;
import wang.xiaoluobo.webwar.entity.UsersEntityExample;
import wang.xiaoluobo.webwar.mapper.UsersEntityMapper;
import wang.xiaoluobo.webwar.service.UsersService;

import java.util.List;

/**
 * @author wangyd
 * @date 2019/2/1
 */
@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersEntityMapper usersEntityMapper;

    @Override
    public List<UsersEntity> getList(UsersEntityExample example) {
        return usersEntityMapper.selectByExample(example);
    }
}
