# cp
1. 保留源文件的访问时间和修改时间
    ```bash
    dongzai1005@Dongzai1005-3:~/shell$ ll t*
    -rw-r--r--  1 dongzai1005  staff  0  2 20 06:31 t
    -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t1
    dongzai1005@Dongzai1005-3:~/shell$ cp -p t1 t2
    dongzai1005@Dongzai1005-3:~/shell$ ll t*
    -rw-r--r--  1 dongzai1005  staff  0  2 20 06:31 t
    -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t1
    -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t2
    ```

2. 硬链接(文件索引相同，但会创建新独立文件)
    ```bash
    dongzai1005@Dongzai1005-3:~/shell$ cp -l t1 t3
    dongzai1005@Dongzai1005-3:~/shell$ ls -il t*
    4492154956 -rw-r--r--  1 dongzai1005  staff  0  2 20 06:31 t
    4492155028 -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t1
    4492155302 -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t2
    4492155028 -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t3
    ```

3. 软链接
    ```bash
    dongzai1005@Dongzai1005-3:~/shell$ cp -s t1 t4
    dongzai1005@Dongzai1005-3:~/shell$ ls -il t*
    4492154956 -rw-r--r--  1 dongzai1005  staff  0  2 20 06:31 t
    4492155028 -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t1
    4492155302 -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t2
    4492155028 -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t3
    4492155028 lrwxrwxrwx  1 dongzai1005  staff  0  2 20  2018 t4
    ```
