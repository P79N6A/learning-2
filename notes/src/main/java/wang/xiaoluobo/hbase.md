# hbase
## RowKey
### 1. rowkey设计原则
1. rowkey长度原则(16bytes)
    1. 数据的持久化文件HFile中是按照KeyValue存储的，如果Rowkey过长比如100个字节，1000万列数据光Rowkey就要占用100*1000万=10亿个字节，将近1G数据，这会极大影响HFile的存储效率；
    2. MemStore将缓存部分数据到内存，如果Rowkey字段过长内存的有效利用率会降低，系统将无法缓存更多的数据，这会降低检索效率。因此Rowkey的字节长度越短越好。
    3. 目前操作系统是都是64位系统，内存8字节对齐。控制在16个字节，8字节的整数倍利用操作系统的最佳特性。
    
2. rowkey散列原则(分散到多台RegionServer)
    1. 如果Rowkey是按时间戳的方式递增，不要将时间放在二进制码的前面，建议将Rowkey的高位作为散列字段，由程序循环生成，低位放时间字段，这样将提高数据均衡分布在每个Regionserver实现负载均衡的几率。如果没有散列字段，首字段直接是时间信息将产生所有新数据都在一个RegionServer上堆积的热点现象，这样在做数据检索的时候负载将会集中在个别RegionServer，降低查询效率。
    
3. rowkey唯一原则(按照字典顺序排序存储)
    1. 必须在设计上保证其唯一性。

### 2. rowkey设计
    ```text
    userId$orderTime$orderNumber
    orderTime=Long.MAX_VALUE-timeStamp
    startRow=userNum$(Long.MAX_VALUE-stopTime)
    endRow=userNum$(Long.MAX_VALUE-startTime)
    ```
## 表





