package wang.xiaoluobo.webwar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wang.xiaoluobo.webwar.service.UsersService;

@Controller
public class HomeController {

    @Autowired
    private UsersService usersService;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }

    @RequestMapping("/getList")
    @ResponseBody
    public String getList() {
        usersService.getList(null);
        return "getList";
    }
}
