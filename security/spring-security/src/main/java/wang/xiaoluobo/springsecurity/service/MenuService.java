package wang.xiaoluobo.springsecurity.service;

import wang.xiaoluobo.springsecurity.model.MenuEntity;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/9/1
 */
public interface MenuService {

    List<MenuEntity> getMenuList();
}
