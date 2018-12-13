package wang.xiaoluobo.shirosecurity.service.security.filters;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.xiaoluobo.shirosecurity.utils.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截器
 *
 * @author wangyd
 * @date 2018/9/15
 */
public class PermsFilter extends AuthorizationFilter {
    private Logger logger = LoggerFactory.getLogger(PermsFilter.class);

    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        Subject subject = SecurityUtils.getSubject();
        String[] perms = (String[]) mappedValue;

        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }

        if (!isPermitted) {
            logger.info("没有权限！");
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1001);
            jsonObject.put("msg", "用户未登录，请先登录");
            WebUtils.bodyReturn(httpResponse, jsonObject.toJSONString());
            return false;
        }

        return isPermitted;
    }

}
