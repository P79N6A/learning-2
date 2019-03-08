package wang.xiaoluobo.webwar.service.impl;

import org.springframework.stereotype.Service;
import wang.xiaoluobo.webwar.entity.UsersEntity;
import wang.xiaoluobo.webwar.entity.UsersEntityExample;
import wang.xiaoluobo.webwar.log.annotation.OperateLog;
import wang.xiaoluobo.webwar.service.UsersService;

import java.util.List;

/**
 * @author wangyd
 * @date 2019/2/1
 */
@Service
public class UsersServiceImpl implements UsersService {

//    @Autowired
//    private UsersEntityMapper usersEntityMapper;

    @OperateLog("get user list")
    @Override
    public List<UsersEntity> getList(UsersEntityExample example) {
//        return usersEntityMapper.selectByExample(example);
        System.out.println("get user list");
        return null;
    }

    @OperateLog("delete user")
    @Override
    public void delete(Long id) {
        System.out.println("delete user " + id);
    }
}
