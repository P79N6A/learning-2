package wang.xiaoluobo.springsecurity.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wang.xiaoluobo.springsecurity.model.UserEntity;
import wang.xiaoluobo.springsecurity.service.UserService;

import java.util.Date;
import java.util.Map;

/**
 * @author wangyd
 * @date 2018/9/1
 */
@Log4j2
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String home(Map<String, Object> map) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.put("username", user.getUsername());
        map.put("date", new Date());
        return "home";
    }

    @RequestMapping("/login")
    public UserEntity userLogin(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("username");
        return userEntity;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/test")
    @ResponseBody
    public UserEntity test() {
        UserEntity userEntity = userService.getUser("user");
        return userEntity;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/admin")
    @ResponseBody
    public String admin() {
        return "admin";
    }

    @RequestMapping("/errorPage")
    public String errorPage() {
        return "error";
    }
}
