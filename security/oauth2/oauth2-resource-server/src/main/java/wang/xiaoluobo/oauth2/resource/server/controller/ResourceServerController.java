package wang.xiaoluobo.oauth2.resource.server.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyd
 * @date 2018/8/23
 */
@Log4j2
@RestController
public class ResourceServerController {

    @RequestMapping(value = "/api")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String success() {
        log.info("-------------------------------- success --------------------------------");
        return "SUCCESS";
    }
}
