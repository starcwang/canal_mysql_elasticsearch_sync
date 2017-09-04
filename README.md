# canal_mysql_elasticsearch_sync

基于 *canal* 的 *Mysql* 与 *Elasticsearch* 实时同步的 *javaweb* 服务。    
canal是阿里巴巴mysql数据库binlog的增量订阅&消费组件。[canal传送门](https://github.com/alibaba/canal)

## 工作原理
### 全量
暴露Http接口（接口定义），待调用后开启后台进程，通过主键分批同步指定数据库中数据到Elasticsearch
> 读取数据库会加**读锁**   
> 主键必须为数字类型

### 增量
循环监听canal通过binlog同步过来的event事件，区别增删改进行与之对应的Elasticsearch的操作。
> 目前只解析了 insert、update、delete，其它数据库操作会被忽略

## 相关文档
- [wiki](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki)
- [QuickStart](https://github.com/starcwang/canal_mysql_elasticsearch_sync/wiki/QuickStart)

## 联系方式
- QQ：760823254
- 邮件：wangchao.star@gmail.com
