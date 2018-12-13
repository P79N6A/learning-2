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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信小程度授权校验
 * Created by jiangmin on 2018/3/30.
 */
public class WxAppAuthenticationFilter extends AccessControlFilter {

    private Logger logger = LoggerFactory.getLogger(WxAppAuthenticationFilter.class);

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        Subject subject = SecurityUtils.getSubject();

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();
        if (url.contains("login") || url.contains("/wxapp/cities") || url.contains("/wxapp/users/getEnterpriseAddressByCode")) {//白名单
            return true;
        }
        if (!subject.isAuthenticated()) {
            logger.info("用户未登录。RemoteHost=" + httpServletRequest.getRemoteHost() + "。RemoteAddr=" + httpServletRequest.getRemoteAddr() + "。RequestURL = " + httpServletRequest.getRequestURL());
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
