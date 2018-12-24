# dig
```Bash
$ yum -y install bind-utils

wyd@wangyandong:~$ dig api.map.baidu.com

; <<>> DiG 9.10.6 <<>> api.map.baidu.com
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 13715
;; flags: qr rd ra; QUERY: 1, ANSWER: 2, AUTHORITY: 5, ADDITIONAL: 6

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 4096
;; QUESTION SECTION:
;api.map.baidu.com.		IN	A

;; ANSWER SECTION:
api.map.baidu.com.	1175	IN	CNAME	api.map.n.shifen.com.
api.map.n.shifen.com.	86	IN	A	220.181.57.27

;; AUTHORITY SECTION:
n.shifen.com.		86224	IN	NS	ns4.n.shifen.com.
n.shifen.com.		86224	IN	NS	ns3.n.shifen.com.
n.shifen.com.		86224	IN	NS	ns5.n.shifen.com.
n.shifen.com.		86224	IN	NS	ns1.n.shifen.com.
n.shifen.com.		86224	IN	NS	ns2.n.shifen.com.

;; ADDITIONAL SECTION:
ns2.n.shifen.com.	1024	IN	A	220.181.57.168
ns4.n.shifen.com.	1024	IN	A	14.215.178.45
ns3.n.shifen.com.	1024	IN	A	112.80.248.31
ns5.n.shifen.com.	1024	IN	A	180.76.76.96
ns1.n.shifen.com.	1024	IN	A	61.135.185.97

;; Query time: 5 msec
;; SERVER: 172.33.0.10#53(172.33.0.10)
;; WHEN: Mon Dec 24 15:15:02 CST 2016
;; MSG SIZE  rcvd: 263
```
