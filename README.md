# canal_mysql_elasticsearch_sync

基于 *canal* 的 *Mysql* 与 *Elasticsearch* 实时同步的 *javaweb* 服务。    
canal是阿里巴巴mysql数据库binlog的增量订阅&消费组件。[canal传送门](https://github.com/alibaba/canal)

## 工作原理
### 全量
暴露Http接口（接口定义），待调用后开启后台进程，通过主键分批同步指定数据库中数据到Elasticsearch
> 读取数据库会加**读锁**   
> 主键必须为数字类型
#### 过程
1. 首先会根据所给的数据库主键字段，拿到最大的主键数字；
2. 设*pk*=1；
2. 加读锁🔐，从数据库中取出*pk* —— *pk*+*stepSize* 大小的数据（默认500）的数据；
3. 插入到Elasticsearch中；
4. 释放读锁🔐，pk累加*stepSize*，循环2.操作

### 增量
循环监听canal通过binlog同步过来的event事件，区别增删改进行与之对应的Elasticsearch的操作。
> 目前只解析了 insert、update、delete，其它数据库操作会被忽略

## 相关映射
<table  class="bbcode"> 
<tr>  
<td>Mysql字段类型</td>
<td>Elasticsearch类型</td>
</tr>
<tr>  
<td>*char*</td>
<td>{
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
</td>
</tr>
<tr>  
<td>table</td>
<td>是</td>
</tr>
<tr>  
<td>stepSize</td>
<td>否</td>
</tr>
<tr>  
<td>stepSize</td>
<td>否</td>
</tr>
<tr>  
<td>stepSize</td>
<td>否</td>
</tr>
<tr>  
<td>stepSize</td>
<td>否</td>
</tr>
<tr>  
<td>stepSize</td>
<td>否</td>
</tr>
</table> 

## 相关文档
- [wiki](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki)
- [QuickStart](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki/QuickStart)

## 联系方式
- QQ：760823254
- 邮件：wangchao.star@gmail.com
