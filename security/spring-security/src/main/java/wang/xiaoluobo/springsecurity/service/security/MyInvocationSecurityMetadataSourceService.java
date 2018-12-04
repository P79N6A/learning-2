package wang.xiaoluobo.springsecurity.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;
import wang.xiaoluobo.springsecurity.model.MenuEntity;
import wang.xiaoluobo.springsecurity.service.MenuService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author wangyd
 * @date 2018/8/31
 */
@Service
public class MyInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private MenuService menuService;

    private HashMap<String, Collection<ConfigAttribute>> map = null;

    public void loadResourceDefine() {
        map = new HashMap<>();
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;


        List<MenuEntity> menuEntityList = menuService.getMenuList();
        for (MenuEntity menuEntity : menuEntityList) {
            array = new ArrayList<>();
            cfg = new SecurityConfig(menuEntity.getName());
            array.add(cfg);
            map.put(menuEntity.getUrl(), array);
        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (map == null) {
            loadResourceDefine();
        }

        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        AntPathRequestMatcher matcher;
        String resUrl;
        for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if (matcher.matches(request)) {
                return map.get(resUrl);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
