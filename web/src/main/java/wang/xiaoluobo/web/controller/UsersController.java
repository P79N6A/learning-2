package wang.xiaoluobo.web.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.xiaoluobo.web.entity.UsersEntityExample;
import wang.xiaoluobo.web.service.UsersService;

/**
 * @author wangyd
 * @date 2019/2/1
 */
@RestController
public class UsersController {

    @Autowired
    private UsersService usersService;

    @RequestMapping("/getList")
    public String getList() {
        UsersEntityExample example = new UsersEntityExample();
        example.setOrderByClause(" id asc ");
        return JSON.toJSONString(usersService.getList(example));
    }
}
