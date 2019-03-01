# cut
## cut 命令
cut 命令从文件的每一行剪切字节、字符和字段并将这些字节、字符和字段写至标准输出。
如果不指定 File 参数，cut 命令将读取标准输入。必须指定 -b、-c 或 -f 标志之一。

参数：
1. -b: 以字节为单位进行分割。这些字节位置将忽略多字节字符边界，除非也指定了 -n 标志。
2. -c: 以字符为单位进行分割。
3. -d: 自定义分隔符，默认为制表符。
4. -f: 与 -d 一起使用，指定显示哪个区域。
5. -n: 取消分割多字节字符。仅和 -b 标志一起使用。如果字符的最后一个字节落在由 -b 标志的 List 参数指示的
范围之内，该字符将被写出；否则，该字符将被排除


## command action
dongzai1005@Dongzai1005-3:~/soft/elasticsearch-5.3.0/bin$ hostname
Dongzai1005-3.local
dongzai1005@Dongzai1005-3:~/soft/elasticsearch-5.3.0/bin$ hostname | cut -d. -f1
Dongzai1005-3











