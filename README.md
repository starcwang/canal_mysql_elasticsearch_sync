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

## QuickStart
#### 1.canal
本服务基于*canal*，首先确保安装好canal并确保能正常工作。[canal安装说明](https://github.com/alibaba/canal/wiki/QuickStart)

#### 2.下载*canal-mysql-elasticsearch-sync*
访问[https://github.com/starcwang/canal_mysql_elasticsearch_sync/releases](https://github.com/starcwang/canal_mysql_elasticsearch_sync/releases)
并下载。比如以1.0.0为例
```bash
wget https://github.com/starcwang/canal_mysql_elasticsearch_sync/releases/download/1.0.0/canal-mysql-elasticsearch-sync-1.0.0.tar.gz
```
或者自己clone下源码编译
```bash
git clone git@github.com:starcwang/canal_mysql_elasticsearch_sync.git
cd canal_mysql_elasticsearch_sync; 
mvn clean package -Dmaven.test.skip -Denv=release
```
#### 3.解压缩
```bash
mkdir canal_mysql_elasticsearch_sync
tar zxvf canal-mysql-elasticsearch-sync-*.tar.gz -C canal_mysql_elasticsearch_sync
cd canal_mysql_elasticsearch_sync
ls
```
这时可以看到项目目录结构
- bin：启动和关闭服务的脚本
- logs：服务日志
- lib：服务依赖的jar包
- conf：服务配置文件
#### 4.配置
##### 4.1.application.properties
```bash
vim conf/application.properties
```
```text
# 服务名
spring.application.name=canal_mysql_elasticsearch_sync
# 服务端口号
server.port=8828

spring.datasource.driver-class-name=com.mysql.jdbc.Driver
# 数据库配置，不用写database名
spring.datasource.url=jdbc:mysql://127.0.0.1:3306?characterSet=utf8mb4&useSSL=false
# 账号
spring.datasource.username=root
# 密码
spring.datasource.password=123456
spring.datasource.dbcp2.max-idle=10
spring.datasource.dbcp2.min-idle=5
spring.datasource.dbcp2.initial-size=2
spring.datasource.dbcp2.validation-query=SELECT 1
spring.datasource.dbcp2.test-while-idle=true

mybatis.config-location=classpath:mybatis/mybatis-config.xml
mybatis.mapper-locations=classpath:mybatis/mapper/*.xml
        
# canal配置
canal.host=127.0.0.1
canal.port=11111
canal.destination=example
canal.username=
canal.password=

# elasticsearch配置
elasticsearch.cluster.name=my-elasticsearch
elasticsearch.host=127.0.0.1
elasticsearch.port=9300
```
##### 4.2.mapping.properties
```bash
vim conf/mapping.properties
```
```text
# 配置数据库和Elasticsearch所对应的映射关系
# 格式：dbEsMapping[${database}.${table}]=${index}.${type}
dbEsMapping[test_info.user]=test_info.user
dbEsMapping[test_info.admin]=test_info2.admin

# 配置数据库主键列名，默认为id
# 格式：tablePrimaryKeyMap[${database}.${table}]=${primaryKey}
tablePrimaryKeyMap[test_info.user]=id
tablePrimaryKeyMap[test_info.admin]=pk
```
#### 5.启动
```bash
sh bin/start.sh
```
等待大约30s，请求端口查看服务是否启动成功
`http://${ip}:${port}`
#### 6.查看启动日志
```bash
vim logs/info.log
```
#### 7.关闭
```bash
vim bin/stop.sh
```

--------------

enjoy it!

## 全量同步接口说明
#### 1.通过表名同步
url：`http://localhost:8828/sync/byTable      

request：     
| 参数 | 是否必须 | 类型 | 含义 | 例子 |
| ------------- | ------------- | ----- |
| database | 是 | String | 数据库名 | info |
| table | 是 | String | 表名 | user |
| stepSize | 否 | Integer | 每次读取数据量，默认500 | 500 |
