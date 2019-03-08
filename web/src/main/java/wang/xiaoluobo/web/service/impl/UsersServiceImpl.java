package wang.xiaoluobo.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.xiaoluobo.web.entity.UsersEntity;
import wang.xiaoluobo.web.entity.UsersEntityExample;
import wang.xiaoluobo.web.log.annotation.OperateLog;
import wang.xiaoluobo.web.mapper.UsersEntityMapper;
import wang.xiaoluobo.web.service.UsersService;

import java.util.List;

/**
 * @author wangyd
 * @date 2019/2/1
 */
@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersEntityMapper usersEntityMapper;

    @OperateLog("get user list")
    @Override
    public List<UsersEntity> getList(UsersEntityExample example) {
        System.out.println("execute service method getList");
        return usersEntityMapper.selectByExample(example);
    }

    @OperateLog("delete user")
    @Override
    public void delete(Long id) {
        System.out.println("delete user " + id);
    }
}
