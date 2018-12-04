package wang.xiaoluobo.springsecurity.service.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wangyd
 * @date 2018/8/31
 */
public class MyAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public MyAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setContinueChainBeforeSuccessfulAuthentication(true);
    }

    public MyAuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";


    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String username = httpServletRequest.getParameter(USER_NAME);
        String password = httpServletRequest.getParameter(PASSWORD);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        return token;
    }

    public SimpleUrlAuthenticationFailureHandler getFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler = (SimpleUrlAuthenticationFailureHandler)super.getFailureHandler();
        failureHandler.setUseForward(true);
        return failureHandler;
    }
}
