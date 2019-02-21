# crontab
```bash
# Example of job definition:
# .---------------- minute (0 - 59)
# |  .------------- hour (0 - 23)
# |  |  .---------- day of month (1 - 31)
# |  |  |  .------- month (1 - 12) OR jan,feb,mar,apr ...
# |  |  |  |  .---- day of week (0 - 6) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat
# |  |  |  |  |
# *  *  *  *  * user-name command to be executed
$ sudo crontab -e
0 0 25 2 * /etc/profile;/bin/sh /mnt/opt/stop_api.sh
0 0 25 2 * echo "stop api success." >> /mnt/opt/a.txt

$ service crond restart
```

> stop_api.sh
```bash
#!/bin/bash
ps -ef | grep open-api | grep -v grep | awk '{print $2}' | while read pid
do
    echo "eagle-open-api pid is $pid"
    kill -9 $pid
    echo "kill result is $?"
done

echo "open-api stop success"
```
