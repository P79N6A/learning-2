/**
 * @Project: sy_oa
 * @Title: OperateLog.java
 * @Package: com.sy.common.annotation
 * @Description: 日志操作类
 * @author: WangYandong
 * @date: 2013-8-6 下午9:56:50
 * @version: v1.0
 */
package wang.xiaoluobo.web.log.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    String value() default "";

    String key() default "";

    int[] param() default {};
}
