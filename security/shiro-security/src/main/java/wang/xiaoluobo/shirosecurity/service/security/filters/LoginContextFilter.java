package wang.xiaoluobo.shirosecurity.service.security.filters;

import com.google.common.base.Throwables;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.xiaoluobo.shirosecurity.model.UserEntity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wangyd
 * @date 2018/9/15
 */
public class LoginContextFilter extends PathMatchingFilter {

    private Logger logger = LoggerFactory.getLogger(LoginContextFilter.class);

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                UserEntity userEntity = (UserEntity) subject.getPrincipal();
            }
        } catch (UnavailableSecurityManagerException e) {
            if (request instanceof HttpServletRequest) {
                logger.error("LoginContextFilter UnavailableSecurityManagerException url: {}", ((HttpServletRequest) request).getRequestURI());
            }
            logger.error("LoginContextFilter UnavailableSecurityManagerException: {}",
                    Throwables.getStackTraceAsString(e));
        } catch (Exception e) {
            logger.error("LoginContextFilter Exception: {}",
                    Throwables.getStackTraceAsString(e));
        }
        return true;
    }
}
