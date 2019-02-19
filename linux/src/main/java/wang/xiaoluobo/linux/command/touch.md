# touch
1. 创建文件 t
    ```bash
    dongzai1005@Dongzai1005-3:~/shell$ touch t
    dongzai1005@Dongzai1005-3:~/shell$ ls -il t
    4492154956 -rw-r--r--  1 dongzai1005  staff  0  2 20 06:31 t
    ```

2. 指定 t1 文件创建时间
    ```bash
    dongzai1005@Dongzai1005-3:~/shell$ touch -t 201802201200 t1
    dongzai1005@Dongzai1005-3:~/shell$ ls -l t1
    -rw-r--r--  1 dongzai1005  staff  0  2 20  2018 t1
    ```