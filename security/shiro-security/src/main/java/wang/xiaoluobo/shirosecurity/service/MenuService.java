package wang.xiaoluobo.shirosecurity.service;


import wang.xiaoluobo.shirosecurity.model.MenuEntity;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
public interface MenuService {

    List<MenuEntity> getMenuList();
}
