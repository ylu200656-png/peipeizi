# backend

后端工程已完成 `Spring Boot + Maven` 初始化，当前可以直接进入业务代码开发。

## 启动命令

```powershell
mvn spring-boot:run
```

## 测试命令

```powershell
mvn test
```

## 当前目标结构

```text
backend/src/main/java/com/yaojie
├─ common
├─ config
├─ modules
│  ├─ auth
│  ├─ system
│  ├─ medicine
│  ├─ purchase
│  ├─ sale
│  ├─ inventory
│  ├─ warning
│  ├─ stats
│  └─ audit
└─ security
```

## 编码约束

- `controller` 只做参数接收和响应输出
- `service` 负责业务规则和事务
- `mapper` 负责 SQL 访问
- `dto` 负责请求参数
- `vo` 负责响应对象
- `entity` 负责数据库映射

## 开发顺序

1. 公共返回体、异常处理、认证框架
2. 用户角色模块
3. 药品档案模块
4. 入库模块
5. 销售模块
6. 库存与预警模块
7. 统计模块
8. 审计日志模块
