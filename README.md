### 额度管理功能

#### 简介:

独立完成基于java spring boot的项目，开发工具可以用IntelliJ，包含文档说明、源码、测试用例.

#### 功能:

实现一个额度管理功能，模拟多用户进行不用类型额度申请、扣减的功能。
具体包括初始化额度、新增额度、扣减额度、查询额度，用例和代码需要包含额度管理模块、模拟多用户操作的定时任务代码、测试用例。
db可以用sqlite或者mysql。

#### 实现项:

1. 额度管理模块(注:额度为double类型即可)

* 支持多个额度账户管理
* 支持额度初始化
* 支持额度增加
* 支持额度扣减
* 支持额度查询

2. 定时任务功能

* 定时进行对额度操作
* 模拟多用户发起

3. 测试用例

* 覆盖管理额度管理功能

### 具体实现方案

#### 项目思路

首先根据项目相关介绍，代入用户申请信用账户场景。
该场景下设计两个用户类型，一个是普通用户，一个是业务。

1. 普通用户

* 对于个人已申请账户设计消费不同账户余额
* 查看不同账户的总额度，和所剩余额

2. 业务

* 初始化用户不同账户
* 调整(增加或减少)用户余额账户上限额度

#### 注意事项

* 权限隔离，业务操作应与普通用户进行操作隔离
* 用户不得申请相同账户类型
* 对于停止使用的用户，不得进行额度账户初始化，不得进行其他业务操作。用户个人不允许做任何操作。
* 对于停止使用的账户，不得进行调整余额和扣款等操作

#### 技术选型

| 基础信息  | 选型         |
|-------|------------|
| 基础框架  | springboot |
| JDK版本 | 1.8        |
| 数据库选型 | Mysql      |
| 数据库版本 | 8.0        |

#### 表设计

1. 用户信息记录表---client

| 字段名         | 类型           | 注释             |
|-------------|--------------|----------------|
| client_id   | int          |                |
| username    | varchar(255) | 用户名            |
| create_time | datetime     | 创建时间           |
| update_time | datetime     | 更新时间           |
| status      | int          | 账户状态 0 停用 1 正常 |
| type        | int          | 用户类型 0 用户 1 业务 |

2. 用户额度账户记录表---quota

| 字段名           | 类型            | 注释                              |
|---------------|---------------|---------------------------------|
| quota_id      | int           |                                 |
| type          | varchar(255)  | 用户额度账户类型 0:额度账户0,1:额度账户1 ...... |
| create_time   | datetime      | 创建时间                            |
| update_time   | datetime      | 更新时间                            |
| client_id     | int           | 用户id                            |
| status        | int           | 账户状态 0 停用 1 正常                  |
| account_quota | decimal(20,6) | 账户额度                            |
| current_quota | decimal(20,6) | 剩余额度                            |

3. 额度流水表---quota

| 字段名         | 类型            | 注释     |
|-------------|---------------|--------|
| detail_id   | int           |        |
| create_time | datetime      | 创建时间   |
| quota_id    | int           | 额度账户id |
| quota_value | decimal(20,6) | 额度流水值  |

4. 额度操作表---quota

| 字段名         | 类型            | 注释                 |
|-------------|---------------|--------------------|
| operate_id  | int           |                    |
| create_time | datetime      | 创建时间               |
| type        | int           | 操作类型 0 减少额度 1 增加额度 |
| quota_id    | int           | 额度账户id             |
| quota_value | decimal(20,6) | 操作额度值              |
#### 项目结构介绍
* src/test/java/com/accountquota/facade/impl 单元测试
* src/main/java/com/accountquota/annotations 自定义注解
* src/main/java/com/accountquota/aspect 自定义切面
* src/main/java/com/accountquota/config 系统相关配置
* src/main/java/com/accountquota/constant 系统常量
* src/main/java/com/accountquota/enums 系统枚举
* src/main/java/com/accountquota/exception 自定义异常
* src/main/java/com/accountquota/mybatisplus mybatisplus相关
* src/main/java/com/accountquota/task 定时任务
* src/main/java/com/accountquota/util 系统工具包
* src/main/java/com/accountquota/task 定时任务
* dbsql 数据库执行脚本
* 测试用例 测试用例
* swagger文档地址 http://localhost:8100/accountquota/swagger-ui.html#/


