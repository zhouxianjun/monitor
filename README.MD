# 部署后台
## 打包
```
mvn clean -DskipTests package -Pprod
```
## 安装sandbox
### 下载
http://ompc.oss-cn-hangzhou.aliyuncs.com/jvm-sandbox/release/sandbox-stable-bin.zip
### 解压
`unzip sandbox-stable-bin.zip`
### 安装(可选)
```
cd sandbox
./install-local.sh
```
## 配置 `filebeat`
`vim /etc/filebeat/filebeat.yml`

```yaml
filebeat.prospectors:
    - input_type: log
        paths:
          - /home/deploy/logs/monitor.log(视情况而定)
        document_type: java_log
        multiline.pattern: ^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))
        multiline.negate: true
        multiline.match: after
    tags: ["all580-monitor"]
    output.logstash:
        hosts: ["10.66.94.203:5044"]
```
## 启动 
```
java -javaagent:/home/sandbox/lib/sandbox-agent.jar -jar monitor-0.0.1-SNAPSHOT.jar > log 2>&1 &
```

# 部署前台
## 配置`nginx`
```
server {
    listen       8081 default_server;
    server_name  _;
    root  /home/wwwroot/monitor;
    
    location / {
    }                                                                                                                                                                                                                                                                     
}
```
## 编译
```
cd front 
npm i
npm run build
```
## 解压
把dist目录中的dist.zip文件复制到nginx目录下
`unzip dist.zip`

# 使用
1. 在数据库tab_spot配置景区
2. http://domain/app/index 创建应用
3. 添加报警联系人

## 报警规则
每个判定脚本都含有以下信息:
- rule 为 tab_alarm_rule 表信息
- history 为 tab_alarm_history 表信息
- data 为 tab_monitor_data 表数组
- log 为 log对象
- dataBatch 按批次分组(data为空则无数据)

脚本返回false则为失败,其它均为成功,如果脚本抛出异常则会终止本次判定。

## 日志搜索
需要配置filebeat

## 日志监控
https://www.elastic.co/guide/en/x-pack/5.6/watcher-getting-started.html


