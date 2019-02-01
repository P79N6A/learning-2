package wang.xiaoluobo.web.quartz;

import com.alibaba.fastjson.JSON;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import wang.xiaoluobo.web.entity.UsersEntityExample;
import wang.xiaoluobo.web.service.UsersService;

import java.util.Date;

@DisallowConcurrentExecution
public class MyQuartzJob extends QuartzJobBean {

    @Autowired
    private UsersService usersService;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(String.format("job(%s) start: %s!", this.name, new Date()));
        System.out.println(JSON.toJSONString(this.usersService.getList(new UsersEntityExample())));
        System.out.println(String.format("job(%s) end: %s!", this.name, new Date()));
    }
}
