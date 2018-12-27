## golang version 1.10.3
[golang标准库](https://gowalker.org/search?q=gorepos)

[golang code](https://github.com/Dongzai1005/go-learning/blob/master/golang.md)

#### 环境配置
```Bash
$ vim .bash_profile
export GOROOT=/usr/local/go
export PATH=$PATH:$GOROOT/bin

export GOPATH=/Users/wyd/go
export PATH=$PATH:$GOPATH/bin

$ source .bash_profile
```
#### golang ide debug
> $ xcode-select --install

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
- go工作区目录结构
    ```text
    src   引用的外部库
    pkg   编译时，生成的对象文件
    bin   编译后的程序
    ```

- 垃圾回收器
    ```text
    垃圾回收器Go拥有简单却高效的标记-清除回收器。
    主要思想来源于IBM的可复用垃圾回收器，旨在打造一个高效、低延迟的并发回收器。
    ```
    
- 下载依赖  
    go get -u github.com/qor/qor-example  

- [包](https://gowalker.org/search?q=gorepos)  
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
    当标识符(包括常量、变量、类型、函数名、结构字段等等)以一个大写字母开头，如：Group1，那么使用这种形式的标识符的对象就可以被外部包的代码所使用  
    标识符如果以小写字母开头，则对包外是不可见的，但是他们在整个包的内部是可见并且可用的
    ```

- Go程序的执行顺序
    ```text
    1. 按顺序导入所有被main包引用的其它包，然后在每个包中执行如下流程
    2. 如果该包又导入了其它的包，则从第一步开始递归执行，但是每个包只会被导入一次
    3. 然后以相反的顺序在每个包中初始化常量和变量，如果该包含有 init 函数的话，则调用该函数
    4. 在完成这一切之后，main 也执行同样的过程，最后调用 main 函数开始执行程序
    ```
    
- 数据类型
    ```text
    基本类型：int、float、bool、string
    结构化类型：struct、array、slice、map、channel；
    描述类型的行为：interface
    Go语言不存在类型继承
    结构化的类型没有真正的值，它使用nil作为默认值
    ```
    
    ```golang
    # 数组
    var arr [5]int
    var arrKeyValue = [5]string{3: "Chris", 4: "Ron"}
    var arrAge = [5]int{18, 20, 15, 22, 16}
    var arrLazy = [...]int{5, 6, 7, 8, 22}
    var screen [WIDTH][HEIGHT]pixel
    
    seasons := []string{"Spring", "Summer", "Autumn", "Winter"}
    for i, season := range seasons {
        fmt.Printf("Season %d is: %s\n", i, season)
    }
    ```
    
- 引用类型  
    通道(channel)、映射(map)和切片(slice)都是引用类型
    1. channel
    
    2. map
    make(map[string]Matcher)
    map是Go语言里的一个引用类型，需要使用make来构造。如果不先构造map并将构造后的值赋值给变量，
    会在试图使用这个map变量时收到出错信息。这是因为map变量默认的零值是nil
    
    3. slice
    切片(slice)是对数组一个连续片段的引用，所以切片是一个引用类型  
    切片提供了一个相关数组的动态窗口  
    切片是可索引的，并且可以由len()函数获取长度  
    多个切片如果表示同一个数组的片段，它们可以共享数据；因此一个切片和相关数组的其他切片是共享存储的，相反，不同的数组总是代表不同的存储。数组实际上是切片的构建块  
    **优点**因为切片是引用，所以它们不需要使用额外的内存并且比使用数组更有效率   
    声明切片的格式是： var identifier []type(不需要说明长度)  
    一个切片在未初始化之前默认为nil，长度为0  
    切片的初始化格式是：var slice1 []type = arr[start:end]  
    切片的底层指向一个数组，该数组的实际体积可能要大于切片所定义的体积。只有在没有任何切片指向的时候，底层的数组内层才会被释放，这种特性有时会导致程序占用多余的内存。  
    
    ```golang
    var arr = [6]int{0, 1, 2, 3, 4, 5}
    var slice1 = arr[2:5]    // 2, 3, 4
    fmt.Println(slice1)
    var slice2 = slice1[0:2] // 2, 3
    fmt.Println(slice2)
    var slice3 = slice1[3:4] // 5
    fmt.Println(slice3)
    var slice4 = slice1[3:6] // panic: runtime error: slice bounds out of range
    fmt.Println(slice4)
    var slice5 = slice1[4:5] // panic: runtime error: slice bounds out of range
    fmt.Println(slice5)
    
    slice6 := make([]int, 5)
    
    // 扩容1位
    sl = sl[0:len(sl)+1]
    
    // 复制和追加
    slFrom := []int{1, 2, 3}
    slTo := make([]int, 10)
    n := copy(slTo, slFrom)
    slTo = append(slTo, 4, 5, 6)
    ```
    
    ```golang
    // 这段代码可以顺利运行，但返回的 []byte 指向的底层是整个文件的数据。只要该返回的切片不被释放，
    // 垃圾回收器就不能释放整个文件所占用的内存。换句话说，一点点有用的数据却占用了整个文件的内存。
    func FindDigits(filename string) []byte {
        b, _ := ioutil.ReadFile(filename)
        return digitRegexp.Find(b)
    }
    
    func FindDigits(filename string) []byte {
       b, _ := ioutil.ReadFile(filename)
       b = digitRegexp.Find(b)
       
       // 新建切片并将数据拷贝到新切片中，避免切片占用底层数组，不释放内存(垃圾回收器无法回收内存)
       c := make([]byte, len(b))
       copy(c, b)
       
       return c
    }
    ```
    
- map
    ```text
    var map1 map[keytype]valuetype
    var map1 map[string]int
  
    ([keytype]``和valuetype` 之间允许有空格，但是gofmt移除了空格)
    未初始化的 map 的值是 nil
    
    // map
    var mapLit map[string]int
    var mapAssigned map[string]int

    mapLit = map[string]int{"one": 1, "two": 2}
    mapCreated := make(map[string]float32)
    mapAssigned = mapLit

    mapCreated["key1"] = 4.5
    mapCreated["key2"] = 3.14159
    mapAssigned["two"] = 3
    fmt.Println(len(mapCreated))  // 2

    mp1 := make(map[int][]int)
    mp2 := make(map[int]*[]int)

    capitals := map[string] string {"France":"Paris", "Italy":"Rome", "Japan":"Tokyo" }
    for key := range capitals {
        fmt.Println("Map item: Capital of", key, "is", capitals[key])
    }
  
    items := make([]map[int]int, 5)
    for i:= range items {
        items[i] = make(map[int]int, 1)
        items[i][1] = 2
    }
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
    ```Bash
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

- 回调
    ```golang
    package main
    
    import (
        "fmt"
    )
    
    func main() {
        callback(1, Add)
    }
    
    func Add(a, b int) {
        fmt.Printf("The sum of %d and %d is: %d\n", a, b, a+b)
    }
    
    func callback(y int, f func(int, int)) {
        f(y, 2) // this becomes Add(1, 2)
    }
    ```

- 闭包
    ```golang
    # 匿名函数同样被称之为闭包：它们被允许调用定义在其它环境下的变量
    # 闭包可使得某个函数捕捉到一些外部状态
    # 一个闭包继承了函数所声明时的作用域。这种状态都被共享到闭包的环境中，因此这些变量可以在闭包中被操作，直到被销毁
    # 闭包经常被用作包装函数：它们会预先定义好1个或多个参数以用于包装
    # 使用闭包来完成更加简洁的错误检查
    package main
    
    import "fmt"
    
    func main() {
        f()
    }
    
    func f() {
        for i := 0; i < 4; i++ {
            g := func(i int) { fmt.Printf("%d ", i) }
            g(i)
            fmt.Printf(" - g is of type %T and has value %v\n", g, g)
        }
    }
    
    4 3 2 1 0
    ```

- 结构与方法
    ```golang
    type Interval struct {
        start int
        end   int
    }
    
    // 初始化方式
    // (A)值必须以字段在结构体定义时的顺序给出，&不是必须的
    // (B)字段名加一个冒号放在值的前面
    // (C)值的顺序不必一致，并且某些字段还可以被忽略掉
    intr := Interval{0, 3}            (A)
    intr := Interval{end:5, start:1}  (B)
    intr := Interval{end:5}           (C)
    
    // 匿名字段和内嵌结构体
    // 结构体可以包含一个或多个匿名(或内嵌)字段，即这些字段没有显式的名字，只有字段的类型是必须的，此时类型也就是字段的名字。匿名字段本身可以是一个结构体类型，即结构体可以包含内嵌结构体
    package main
    
    import "fmt"
    
    type innerS struct {
        in1 int
        in2 int
    }
    
    type outerS struct {
        b    int
        c    float32
        int  // anonymous field
        innerS //anonymous field
    }
    
    func main() {
        outer := new(outerS)
        outer.b = 6
        outer.c = 7.5
        outer.int = 60
        outer.in1 = 5
        outer.in2 = 10
    
        fmt.Printf("outer.b is: %d\n", outer.b)
        fmt.Printf("outer.c is: %f\n", outer.c)
        fmt.Printf("outer.int is: %d\n", outer.int)
        fmt.Printf("outer.in1 is: %d\n", outer.in1)
        fmt.Printf("outer.in2 is: %d\n", outer.in2)
    
        // 使用结构体字面量
        outer2 := outerS{6, 7.5, 60, innerS{5, 10}}
        fmt.Printf("outer2 is:", outer2)
    }
    
    // 方法
    package main
    
    import "fmt"
    
    type TwoInts struct {
        a int
        b int
    }
    
    func main() {
        two1 := new(TwoInts)
        two1.a = 12
        two1.b = 10
    
        fmt.Printf("The sum is: %d\n", two1.AddThem())
        fmt.Printf("Add them to the param: %d\n", two1.AddToParam(20))
    
        two2 := TwoInts{3, 4}
        fmt.Printf("The sum is: %d\n", two2.AddThem())
    }
    
    func (tn *TwoInts) AddThem() int {
        return tn.a + tn.b
    }
    
    func (tn *TwoInts) AddToParam(param int) int {
        return tn.a + tn.b + param
    }
    ```

- 接口
    ```golang
    package main
    
    import "fmt"
    
    type Shaper interface {
        Area() float32
    }
    
    type Square struct {
        side float32
    }
    
    func (sq *Square) Area() float32 {
        return sq.side * sq.side
    }
    
    type Rectangle struct {
        length, width float32
    }
    
    func (r Rectangle) Area() float32 {
        return r.length * r.width
    }
    
    func main() {
        r := Rectangle{5, 3} // Area() of Rectangle needs a value
        q := &Square{5}      // Area() of Square needs a pointer
        // shapes := []Shaper{Shaper(r), Shaper(q)}
        // or shorter
        shapes := []Shaper{r, q}
        fmt.Println("Looping through shapes for area ...")
        for n, _ := range shapes {
            fmt.Println("Shape details: ", shapes[n])
            fmt.Println("Area of this shape is: ", shapes[n].Area())
        }
    }
    
    // 接口嵌套接口
    type ReadWrite interface {
        Read(b Buffer) bool
        Write(b Buffer) bool
    }
    
    type Lock interface {
        Lock()
        Unlock()
    }
    
    type File interface {
        ReadWrite
        Lock
        Close()
    }
    
    type Any interface {}
    ```
    
#### qor问题
```text
(sql: Scan error on column index 1: unsupported Scan, storing driver.Value type []uint8 into type *time.Time)

database = "root:1234@tcp(localhost:3306)/test?charset=utf8&loc=Local&parseTime=true"
``` 
