# mybatis-generator
1. 建表
2. mybatis-generator plugin插件生成bean, mapper, xml
3. [访问url](http://localhost:8080/getList)

# quartz cron expression
```text
1. * 表示所有值。在分钟里表示每一分钟触发。在小时，日期，月份等里面表示每一小时，每一日，每一月。
2. ？表示不指定值。表示不关心当前位置设置的值。 比如不关心是周几，则周的位置填写？。
3. - 表示区间。小时设置为10-12表示10,11,12点均会触发。
4. ，表示多个值。小时设置成10,12表示10点和12点会触发。
5. / 表示递增触发。5/15表示从第5秒开始，每隔15秒触发。
6. L 表示最后的意思。日上表示最后一天。星期上表示星期六或7。L前加数据，表示该数据的最后一个。星期上设置6L表示最后一个星期五。6表示星期五
7. W表示离指定日期最近的工作日触发。15W离该月15号最近的工作日触发。
8. #表示每月的第几个周几。 6#3表示该月的第三个周五


例子：
每隔5秒执行一次：/5 * * * ? 
每隔1分钟执行一次：0 /1 * * ? 
每天23点执行一次：0 0 23 * * ? 
每天凌晨1点执行一次：0 0 1 * * ? 
每月1号凌晨1点执行一次：0 0 1 1 * ? 
每月最后一天23点执行一次：0 0 23 L * ? 
每周星期天凌晨1点实行一次：0 0 1 ? * L 
在26分、29分、33分执行一次：0 26,29,33 * * * ? 
每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ? 
每天中午12点触发：0 0 12 * * ? 
每天上午10:15触发：0 15 10 ? * * 
每天上午10:15触发：0 15 10 * * ? 
每天上午10:15触发：0 15 10 * * ? * 
2005年的每天上午10:15触发：0 15 10 * * ? 2005 
在每天下午2点到下午2:59期间的每1分钟触发：0 * 14 * * ? 
在每天下午2点到下午2:55期间的每5分钟触发：0 0/5 14 * * ? 
在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发：0 0/5 14,18 * * ? 
在每天下午2点到下午2:05期间的每1分钟触发：0 0-5 14 * * ? 
每年三月的星期三的下午2:10和2:44触发：0 10,44 14 ? 3 WED 
周一至周五的上午10:15触发：0 15 10 ? * MON-FRI 
每月15日上午10:15触发：0 15 10 15 * ? 
每月最后一日的上午10:15触发：0 15 10 L * ? 
每月的最后一个星期五上午10:15触发：0 15 10 ? * 6L 
2002年至2005年的每月的最后一个星期五上午10:15触发：0 15 10 ? * 6L 2002-2005 
每月的第三个星期五上午10:15触发：0 15 10 ? * 6#3 
每天早上6点：0 6 * * * 
每两个小时：0 /2 * * 
晚上11点到早上8点之间每两个小时，早上八点：0 23-7/2，8 * * * 
每个月的4号和每个礼拜的礼拜一到礼拜三的早上11点：0 11 4 * 1-3 
1月1日早上4点：0 4 1 1 *
```

# spring aop 注解方式配置
