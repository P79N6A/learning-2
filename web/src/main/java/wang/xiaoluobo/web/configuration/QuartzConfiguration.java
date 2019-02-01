package wang.xiaoluobo.web.configuration;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import wang.xiaoluobo.web.quartz.MyQuartzJob;

import java.io.IOException;

@Configuration
public class QuartzConfiguration {

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired
    private JobFactory jobFactory;

    @Bean
    public JobDetail testJob() {
        return JobBuilder.newJob(MyQuartzJob.class).withIdentity("testJob")
                .usingJobData("name", "testJob").storeDurably().build();
    }

    @Bean
    public Trigger testJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("/30 * * * * ?");

        return TriggerBuilder.newTrigger().forJob(testJob())
                .withIdentity("testJobTrigger").withSchedule(cronScheduleBuilder).build();
    }


    @Bean(name = "SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        JobDetail[] jobDetails = new JobDetail[]{testJob()};
        Trigger[] triggers = new Trigger[]{testJobTrigger()};

        // 获取配置属性
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource("application-" + environment + ".yml"));
        yamlPropertiesFactoryBean.afterPropertiesSet();

        // 创建SchedulerFactoryBean
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(yamlPropertiesFactoryBean.getObject());
        factory.setJobDetails(jobDetails);
        factory.setJobFactory(jobFactory);
        factory.setTriggers(triggers);
        // 使用数据源
        return factory;
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }
}