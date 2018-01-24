# canal_mysql_elasticsearch_sync

基于 *canal* 的 *Mysql* 与 *Elasticsearch* 实时同步的 *javaweb* 服务。    
canal是阿里巴巴mysql数据库binlog的增量订阅&消费组件。[canal传送门](https://github.com/alibaba/canal)

## 工作原理
### 全量
暴露Http接口（接口定义见[wiki](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki/HttpApi)），待调用后开启后台线程，通过主键分批同步指定数据库中数据到Elasticsearch
> 读取数据库会加**读锁**   
> 主键必须为数字类型
#### 过程
1. 首先会根据所给的数据库主键字段，拿到最大的主键数字max_id；
2. 设*pk*=min_id（默认是数据库中的主键最小值）；
2. 加读锁🔐，从数据库中取出*pk* —— *pk*+*stepSize* 大小的数据（默认500）的数据；
3. 插入到Elasticsearch中；
4. 释放读锁🔐，pk累加*stepSize*，循环3.操作，直到*pk*>*max_id*

### 增量
循环监听canal通过binlog同步过来的event事件，区别增删改进行与之对应的Elasticsearch的操作。
> 目前只解析了 insert、update、delete，其它数据库操作会被忽略

## 默认相关字段映射
<table  class="bbcode"> 
<tr>  
<td>Mysql字段类型</td>
<td>Elasticsearch类型</td>
</tr>
<tr>  
<td>char</td>
<td>{"type": "text", "fields": {"keyword": {"type": "keyword", "ignore_above": 256}}</td>
</tr>
<tr>  
<td>text</td>
<td>{"type": "text", "fields": {"keyword": {"type": "keyword", "ignore_above": 256}}</td>
</tr>
<tr>  
<td>blob</td>
<td>{"type": "text", "fields": {"keyword": {"type": "keyword", "ignore_above": 256}}</td>
</tr>
<tr>  
<td>int</td>
<td>{"type": "long"}</td>
</tr>
<tr>  
<td>date</td>
<td>{"type": "date"}</td>
</tr>
<tr>  
<td>time</td>
<td>{"type": "date"}</td>
</tr>
<tr>  
<td>float</td>
<td>{"type": "float"}</td>
</tr>
<tr>  
<td>double</td>
<td>{"type": "float"}</td>
</tr>
<tr>  
<td>decimal</td>
<td>{"type": "float"}</td>
</tr>
<tr>  
<td>其它</td>
<td>{"type": "text", "fields": {"keyword": {"type": "keyword", "ignore_above": 256}}</td>
</tr>
</table> 

## 注意事项
- Mysql的binlog格式必须为**ROW**
- 由于使用binlog进行增量同步，和数据库主从类似，不可避免的会有一定的主从延迟，延迟时间取决于机房网络、机器负载、数据量大小等
- Elasticsearch支持的版本为**5.x**
- 增量同步只监听了 **INSERT、UPDATE、DELETE**，其它如建表、删表等尚未支持
- 建议Elasticsearch的mapping手动来创建，因为默认的创建方式不能保证满足业务需求

## 相关文档
- [wiki](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki)
- [HttpApi](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki/HttpApi)
- [QuickStart](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki/QuickStart)

## 联系方式
如果有不合理的地方，还请不吝赐教。
- QQ：760823254
- 邮件：wangchao.star@gmail.com   
 
**支持记得star✨**
