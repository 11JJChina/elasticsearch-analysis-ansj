##前言

这是一个elasticsearch的中文分词插件，基于ansj,感谢群内热心的朋友。
并宣传一下我们的群QQ211682609

## 版本对应

| plugin        |     elasticsearch|
| --------      |       -----:  |
| 1.0.0         |     0.90.2    |
| 1.x           |     1.x       |
| 2.1.1         |     2.1.1     |
| 2.3.1         |     2.3.1     |
| master        |     2.3.2     |


##2.3.2 插件安装

进入Elasticsearch目录运行如下命令 

````
进入es目录执行如下命令

./bin/plugin install http://maven.nlpcn.org/org/ansj/elasticsearch-analysis-ansj/2.3.2.1/elasticsearch-analysis-ansj-2.3.2.1-release.zip
````

##2.3.1 插件安装

进入Elasticsearch目录运行如下命令 

````
进入es目录执行如下命令

./bin/plugin install http://maven.nlpcn.org/org/ansj/elasticsearch-analysis-ansj/2.3.1/elasticsearch-analysis-ansj-2.3.1-release.zip
````


##2.1.1 插件安装

进入Elasticsearch目录运行如下命令 

````
进入es目录执行如下命令

./bin/plugin install http://maven.nlpcn.org/org/ansj/elasticsearch-analysis-ansj/2.1.1/elasticsearch-analysis-ansj-2.1.1-release.zip
````

##1.x 插件安装

进入Elasticsearch目录运行如下命令 

````
./bin/plugin -u http://maven.nlpcn.org/org/ansj/elasticsearch-analysis-ansj/1.x.1/elasticsearch-analysis-ansj-1.x.1-release.zip -i ansj
````


==========


## 2016年04月16日
+ elasticsearch更新2.3.1
+ ansj_seg升级至3.7.3


## 1770年01月01日
+ elasticsearch更新2.1.1
+ ansj_seg升级至3.5
+ 新增http的_ansj接口，用于查看ansj分词词性
+ 新增http的_cat/ansj接口,作用同上，显示为cat方式
+ 新增http的_cat/[index]/analyze接口，和_analyze作用一样，显示为cat方式
+ 更方便的配置

先来点配置好的示例 ^ ^ 别吐槽我的格式化 (顺便求一个判断字符串中含有几个中文的方法)


## 测试


* 创建测试索引

```linux
curl -XPUT 127.0.0.1:9200/test -d '{
    "settings" : {
        "number_of_shards" : 1,
        "number_of_replicas" : 0

    },
    "mappings" : {
        "type1" : {
            "_all" : { "enabled" : false },
            "properties" : {
                "name" : { "type" : "string", "analyzer" : "index_ansj", "search_analyzer" : "query_ansj" }
            }
        }
    }
}'
````

* 添加索引内容

````
curl -XPUT 'http://127.0.0.1:9200/test/test/1' -d '{
    "name" : "中国人民万岁",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elasticsearch"
}'
````

* 查询索引

````
浏览器访问:
http://127.0.0.1:9200/test/test/_search?q=name:%E4%B8%AD%E5%9B%BD
````


* 如果你想把ansj作为你的默认分词需要在elasticsearch.yml加入如下配置:

```yaml

#默认分词器,索引
index.analysis.analyzer.default.type: index_ansj

#默认分词器,查询

index.analysis.analyzer.default_search.type: query_ansj
```



## 关于分词器不得不说的那点小事
````
目前默认内置三个分词器

当然如果你有心仔细观察日志看到了实例化了n多分词器如下

 regedit analyzer named : index_ansj
 regedit analyzer named : query_ansj
 regedit analyzer named : to_ansj
 regedit analyzer named : dic_ansj
 regedit analyzer named : user_ansj
 regedit analyzer named : search_ansj

why????
额 只有三个其他都是别名

````


### 索引分词

```shell

index_ansj 是索引分词,尽可能分词处所有结果 example

http://127.0.0.1:9200/_cat/test/analyze?text=%E5%85%AD%E5%91%B3%E5%9C%B0%E9%BB%84%E4%B8%B8%E8%BD%AF%E8%83%B6%E5%9B%8A&analyzer=index_ansj

六味  		0		2		0		word		
地   		2		3		1		word		
黄丸软 		3		6		2		word		
胶囊  		6		8		3		word		
六味地黄		0		4		4		word		
地黄  		2		4		5		word		
地黄丸 		2		5		6		word		
软胶  		5		7		7		word		
软胶囊 		5		8		8		word			


````


### 搜索分词 (search_ansj=to_ansj=query_ansj)

```shell

query_ansj 是搜索分词,是索引分词的子集,保证了准确率 example

http://127.0.0.1:9200/_cat/test/analyze?text=%E5%85%AD%E5%91%B3%E5%9C%B0%E9%BB%84%E4%B8%B8%E8%BD%AF%E8%83%B6%E5%9B%8A&analyzer=query_ansj

六味 		0		2		0		word		
地  			2		3		1		word		
黄丸软		3		6		2		word		
胶囊 		6		8		3		word		

````

### 用户自定义词典优先的分词方式 (user_ansj=dic_ansj)

```shell

dic_ansj 是用户自定义词典优先策略

http://127.0.0.1:9200/_cat/test/analyze?text=%E5%85%AD%E5%91%B3%E5%9C%B0%E9%BB%84%E4%B8%B8%E8%BD%AF%E8%83%B6%E5%9B%8A&analyzer=dic_ansj

六味地黄		0		4		0		word		
丸   		4		5		1		word		
软胶囊 		5		8		2		word		

````



## 编译安装

* 第一步，你要有一个`elasticsearch`的服务器(废话) 版本2.1.1

* 第二步，把代码clone到本地

* 第三步，mvn clean install

* 第四步，进入$Project_Home/target/releases 目录，

* 第五步，拷贝$Project_Home/target/releases/目录下的zip包到解压到$ES_HOME/plugins目录下



 
现在,你的es集群已经有下面三个名字的analyzer

+ index_ansj (建议索引使用)
+ query_ansj (建议搜索使用)
+ dic_ansj

三个名字的tokenizer

+ index_ansj (建议索引使用)
+ query_ansj (建议搜索使用)
+ dic_ansj


## 分词文件配置:
在这里我说一下，在插件里我写了一些默认配置，如果你也可以接受我的默认配置，关于ansj就完全不用配置了，或者只修改你需要的配置。下面的代码目录都是相对es的config目录，有几点需要注意一下:

+ ansj的核心词典是和插件一起安装的在插件目录下面
+ redis使用的jar用默认脚本启动会有权限问题，此问题目前只能加./elasticsearch -Des.security.manager.enabled=false参数来解决 如果谁有更好的方法请q我
+ 因为要使用redis的pubsub功能，需要关闭es的权限控制，请慎重使用。

```yaml
## ansj配置
ansj:
 dic_path: "ansj/dic/user/" ##用户词典位置
 ambiguity_path: "ansj/dic/ambiguity.dic" ##歧义词典
 enable_name_recognition: true ##人名识别
 enable_num_recognition: true ##数字识别
 enable_quantifier_recognition: false ##量词识别
 enabled_stop_filter: true ##是否基于词典过滤
 stop_path: "ansj/dic/stopLibrary.dic" ##停止过滤词典
## redis 不是必需的
 redis:
  pool:
   maxactive: 20
   maxidle: 10
   maxwait: 100
   testonborrow: true
  ip: 10.0.85.51:6379
  channel: ansj_term ## publish时的channel名称
  write:
    dic: "ext.dic" ##如果有使用redis的pubsub方式更新词典，默认使用 这个目录是相对于dic_path
```




* 查询分词

可以使用开头我提供的http接口来查看分词效果

然后通过redis发布一个新词看看
追加新词
```
redis-cli
publish ansj_term u:c:视康

```

是不是分词发生了变化
删除词条
```
redis-cli
publish ansj_term u:d:视康
```

又回来了

然后通过redis发布一个歧义词
追加歧义词
```
redis-cli
publish ansj_term a:c:减肥瘦身-减肥,nr,瘦身,v
```

是不是分词发生了变化
删除歧义词
```
redis-cli
publish ansj_term a:d:减肥瘦身
```

又回来了


## 结束
就写这么多吧，有啥问题，QQ找我
