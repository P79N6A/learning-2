package wang.xiaoluobo.shirosecurity.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author wangyd
 * @date 2018/9/15
 */
public class WebUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);


    /**
     * 返回body报文
     * @param httpResponse
     * @param body json
     * @throws IOException
     */
    public static void bodyReturn( HttpServletResponse httpResponse , String body) throws IOException{
    	httpResponse.setStatus(HttpServletResponse.SC_OK);
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json");
		OutputStream out = null;
		try {
			out = httpResponse.getOutputStream();
			out.write(body.getBytes());
			out.flush();
		} catch (IOException e) {
			logger.error("bodyReturn exception.",e);
		} finally {
			if (out != null) {
				out.close();
			}
		}   
	}
}
