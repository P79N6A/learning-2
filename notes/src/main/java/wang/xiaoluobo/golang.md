## golang version 1.10.3
#### 环境配置
```sbtshell
$ vim .bash_profile
export GOROOT=/usr/local/go
export PATH=$PATH:$GOROOT/bin

export GOPATH=/Users/wyd/go
export PATH=$PATH:$GOPATH/bin

$ source .bash_profile
```

#### go 命令行  
[go command](http://wiki.jikexueyuan.com/project/go-command-tutorial/0.0.html)
```text
build       compile packages and dependencies
clean       remove object files and cached files
doc         show documentation for package or symbol
env         print Go environment information
bug         start a bug report
fix         update packages to use new APIs
fmt         gofmt (reformat) package sources
generate    generate Go files by processing source
get         download and install packages and dependencies
install     compile and install packages and dependencies
list        list packages
run         compile and run Go program
test        test packages
tool        run specified go tool
version     print Go version
vet         report likely mistakes in packages
``` 

#### qor
[solution](https://github.com/qor/qor-example/issues/155)  
![qor01](https://github.com/Dongzai1005/learning/blob/master/notes/src/main/java/wang/xiaoluobo/images/qor01.png)

#### [go in action](https://www.kancloud.cn/kancloud/the-way-to-go/72432)  
[go in action](https://github.com/Unknwon/the-way-to-go_ZH_CN)
- 垃圾回收器
    ```text
    垃圾回收器Go拥有简单却高效的标记-清除回收器。
    主要思想来源于IBM的可复用垃圾回收器，旨在打造一个高效、低延迟的并发回收器。
    ```
    
- 下载依赖  
    go get -u github.com/qor/qor-example  

- 包  
    ```text
    1. package main表示一个可独立执行的程序
    2. 所有的包名使用小写字母
    3. _ "github.com/qor/qor-example"  
       下划线是对包做初始化操作(调用包内的所有代码文件里定义的init函数)，并不使用包里的标识符
    4. 引入包
      1) import "fmt"
         import "os"
      2) import "fmt"; import "os"
      3) import (
            "fmt"
            "os"
         )
    5. 别名
       import fm "fmt"   
    ```

- 可见性规则
    ```text
    当标识符（包括常量、变量、类型、函数名、结构字段等等）以一个大写字母开头，如：Group1，那么使用这种形式的标识符的对象就可以被外部包的代码所使用  
    标识符如果以小写字母开头，则对包外是不可见的，但是他们在整个包的内部是可见并且可用的
    ```

- Go程序的执行顺序
    ```text
    1. 按顺序导入所有被main包引用的其它包，然后在每个包中执行如下流程
    2. 如果该包又导入了其它的包，则从第一步开始递归执行，但是每个包只会被导入一次
    3. 然后以相反的顺序在每个包中初始化常量和变量，如果该包含有 init 函数的话，则调用该函数
    4. 在完成这一切之后，main 也执行同样的过程，最后调用 main 函数开始执行程序
    ```

- 引用类型  
    ```text
    通道(channel)、映射(map)和切片(slice)都是引用类型
    1. channel
    
    2. map
    make(map[string]Matcher)
    map是Go语言里的一个引用类型，需要使用make来构造。如果不先构造map并将构造后的值赋值给变量，
    会在试图使用这个map变量时收到出错信息。这是因为map变量默认的零值是nil
    
    3. slice
  
    ```
- 数据类型
    ```text
    基本类型：int、float、bool、string
    结构化类型：struct、array、slice、map、channel；
    描述类型的行为：interface
    Go语言不存在类型继承
    结构化的类型没有真正的值，它使用nil作为默认值
    ```
    
- func  
    ```text
    Go语言使用关键字func声明函数，关键字后面紧跟着函数名、参数以及返回值
    func FunctionName (a typea, b typeb) typeFunc
    func FunctionName (a typea, b typeb) (t1 type1, t2 type2)
    ```

- const
    ```text
    # 常量
    const beef, two, c = "eat", 2, "veg"
    const Monday, Tuesday, Wednesday, Thursday, Friday, Saturday = 1, 2, 3, 4, 5, 6
    const (
        Monday, Tuesday, Wednesday = 1, 2, 3
        Thursday, Friday, Saturday = 4, 5, 6
    )

    # 枚举
    const (
        Unknown = 0
        Female = 1
        Male = 2
    )

    const (
        a = iota
        b = iota
        c = iota
    )

    const (
        a = iota
        b
        c
    )
    第一个iota等于0，每当iota在新的一行被使用时，它的值都会自动加1；所以a=0, b=1, c=2

    iota也可以用在表达式中，每遇到一次const关键字，iota重置为0
    ```
    
- 变量
    ```text
    var a, b *int

    var a int
    var b bool
    var str string
  
    var (
        a int
        b bool
        str string
    )
    ```
    
- fist test
    ```sbtshell
    $ vim test.go
    package main
    
    func main(){
      println("hello go")
    }
    
    $ go run test.go
    hello go

    # 外部方法调用
    package hello
    
    import "fmt"
    
    func HelloGo() {
    	fmt.Println("hello go")
    }

    package main
    
    import (
    	"hello"
    )
    
    func main() {
    	hello.HelloGo()
    }

    # 需要先把hello下的包install，才能调用
    # 在$GOPATH/pkg/darwin_amd64下，生成一个hello.a文件(go编译文件)
    $ go install hello
    $ go run test/test.go
    hello go
    ```

- 变量值交换  
    a, b = b, a

- defer
    类似finally，用于释放资源等，多个defer时，则后进先出(原理同栈)
    ```text
    1. 关闭文件流：
    // open a file defer file.Close()
    
    2. 解锁一个加锁的资源
    mu.Lock() defer mu.Unlock()
    
    3. 打印最终报告
    printHeader() defer printFooter()
    
    4. 关闭数据库链接
    // open a database connection defer disconnectFromDB()
    ```
    
- 内置函数  

    | 名称 | 说明 |  
    | :---- | :---- |  
    | close              | 用于管道通信 |
    | len、cap	         | len用于字符串、数组、切片、map 和管道，cap只能用于切片和map |
    | new、make	         | new 用于值类型和用户定义的类型，make 用户内置引用类型 |
    | copy、append	     | 用于复制和连接切片 |
    | panic、recover		 | 错误处理机制 |
    | print、println		 | 底层打印函数，建议使用 fmt 包 |
    | complex、real imag	 | 创建和操作复数 |








