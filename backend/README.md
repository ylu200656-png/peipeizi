# backend

后端工程基于 `Spring Boot + Maven`，当前已经完成第一期主业务链路和管制药品专页接口。

## 启动命令

```powershell
mvn spring-boot:run
```

## 测试命令

```powershell
mvn test
```

## 当前模块

```text
backend/src/main/java/com/yaojie
|-- common
|-- config
|-- modules
|   |-- auth
|   |-- system
|   |-- medicine
|   |-- purchase
|   |-- sale
|   |-- inventory
|   |-- warning
|   |-- controlled
|   |-- stats
|   `-- audit
`-- security
```

## 编码约束

- `controller` 负责参数接收和响应输出
- `service` 负责业务规则和事务
- `mapper` 负责 SQL 访问
- `dto` 负责请求参数
- `vo` 负责响应对象
- `entity` 负责数据库映射

## 当前重点规则

- 库存更新使用事务、行锁和原子 SQL
- 不能移除最后一个启用中的管理员
- 管制药品接口必须加权限控制和审计追溯
