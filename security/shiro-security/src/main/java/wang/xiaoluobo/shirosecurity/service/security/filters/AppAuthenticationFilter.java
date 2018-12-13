package wang.xiaoluobo.shirosecurity.service.security.filters;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.xiaoluobo.shirosecurity.utils.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangyd
 * @date 2018/9/15
 */
public class AppAuthenticationFilter extends AccessControlFilter {

    private Logger logger = LoggerFactory.getLogger(AppAuthenticationFilter.class);

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1001);
            jsonObject.put("msg", "用户未登录，请先登录");
            WebUtils.bodyReturn(httpResponse, jsonObject.toJSONString());
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }
}
