package wang.xiaoluobo.shirosecurity.service.security.context;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import wang.xiaoluobo.shirosecurity.constants.Constants;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * session管理
 *
 * @author wangyd
 * @date 2018/9/16
 */
public class MySessionManager extends DefaultWebSessionManager {

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    private static final Logger logger = LoggerFactory.getLogger(MySessionManager.class);


    public MySessionManager() {
        super();
        Cookie cookie = new SimpleCookie(Constants.COOKIE_NAME);
        this.setSessionIdCookie(cookie);
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String tokenId = WebUtils.toHttp(request).getHeader(Constants.COOKIE_NAME);

        // app端取值
        if (!StringUtils.isEmpty(tokenId)) {
            logger.info("user token:" + tokenId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, tokenId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return tokenId;
        } else {
            // 按默认从cookie取sessionId
            return super.getSessionId(request, response);
        }
    }

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);

        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        if (request != null && null != sessionId) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                return (Session) sessionObj;
            }
        }

        Session session = super.retrieveSession(sessionKey);
        if (request != null && null != sessionId) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }
}