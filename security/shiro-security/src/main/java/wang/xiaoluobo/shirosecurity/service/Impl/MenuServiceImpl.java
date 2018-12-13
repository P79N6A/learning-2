package wang.xiaoluobo.shirosecurity.service.Impl;

import org.springframework.stereotype.Service;
import wang.xiaoluobo.shirosecurity.model.MenuEntity;
import wang.xiaoluobo.shirosecurity.service.MenuService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Override
    public List<MenuEntity> getMenuList() {
        List<MenuEntity> list = new ArrayList<>();
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(1L);
        menuEntity.setName("admin");
        menuEntity.setRoleIds("1,2");
        menuEntity.setUrl("/admin/**");
        list.add(menuEntity);

        MenuEntity menuEntity1 = new MenuEntity();
        menuEntity1.setId(2L);
        menuEntity1.setName("test");
        menuEntity1.setRoleIds("2");
        menuEntity1.setUrl("/test/**");
        list.add(menuEntity1);
        return list;
    }
}
