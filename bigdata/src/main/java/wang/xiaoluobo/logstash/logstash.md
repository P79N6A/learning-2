# logstash version 6.5.4
https://artifacts.elastic.co/downloads/logstash/logstash-6.5.4.tar.gz

https://www.elastic.co/guide/en/logstash/current/plugins-inputs-tcp.html

https://github.com/logstash/logstash-logback-encoder

$ ./bin/logstash-plugin install logstash-input-tcp
Validating logstash-input-tcp
Installing logstash-input-tcp
Installation successful

$ ./bin/logstash-plugin install logstash-codec-json_lines
Validating logstash-codec-json_lines
Installing logstash-codec-json_lines
Installation successful



$ nohup ./bin/logstash -f ./config/logstash.conf > nohup.log &

web ui:
http://localhost:9600/
