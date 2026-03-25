# 药捷一期接口清单

## 1. 说明

这批接口只覆盖一期最小业务闭环。

统一前缀：

- `/api/v1`

统一返回结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 2. 认证接口

### `POST /api/v1/auth/login`

用途：

- 用户登录

请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

### `GET /api/v1/auth/me`

用途：

- 获取当前登录用户和角色

## 3. 用户与角色接口

### `GET /api/v1/users`

用途：

- 分页查询用户列表

### `POST /api/v1/users`

用途：

- 新增用户

### `PUT /api/v1/users/{id}`

用途：

- 修改用户

### `POST /api/v1/users/{id}/roles`

用途：

- 绑定用户角色

请求体：

```json
{
  "roleIds": [1, 2]
}
```

### `GET /api/v1/roles`

用途：

- 获取角色列表

## 4. 分类与供应商接口

### `GET /api/v1/categories`

用途：

- 查询药品分类

### `POST /api/v1/categories`

用途：

- 新增药品分类

### `GET /api/v1/suppliers`

用途：

- 查询供应商

### `POST /api/v1/suppliers`

用途：

- 新增供应商

## 5. 药品档案接口

### `GET /api/v1/medicines`

用途：

- 药品列表查询

查询参数建议：

- `keyword`
- `categoryId`
- `supplierId`
- `isControlled`
- `status`

### `GET /api/v1/medicines/{id}`

用途：

- 查询药品详情

### `POST /api/v1/medicines`

用途：

- 新增药品

请求体关键字段：

```json
{
  "medicineCode": "MED-001",
  "medicineName": "阿莫西林胶囊",
  "categoryId": 1,
  "specification": "0.25g*24粒",
  "unit": "盒",
  "manufacturer": "示例制药厂",
  "supplierId": 1,
  "purchasePrice": 12.5,
  "salePrice": 18.0,
  "safeStock": 20,
  "expiryWarningDays": 30,
  "isControlled": 0,
  "remark": "普通抗生素"
}
```

### `PUT /api/v1/medicines/{id}`

用途：

- 修改药品

## 6. 入库接口

### `GET /api/v1/purchases`

用途：

- 查询入库单列表

### `GET /api/v1/purchases/{id}`

用途：

- 查询入库单详情

### `POST /api/v1/purchases`

用途：

- 创建入库单并更新库存

请求体：

```json
{
  "orderNo": "PO-20260325001",
  "supplierId": 1,
  "remark": "首批入库",
  "items": [
    {
      "medicineId": 1,
      "batchNo": "AMX-202603",
      "quantity": 50,
      "purchasePrice": 12.5,
      "productionDate": "2026-03-01",
      "expiryDate": "2027-03-01"
    }
  ]
}
```

## 7. 销售接口

### `GET /api/v1/sales`

用途：

- 查询销售单列表

### `GET /api/v1/sales/{id}`

用途：

- 查询销售单详情

### `POST /api/v1/sales`

用途：

- 创建销售单并扣减库存

请求体：

```json
{
  "orderNo": "SO-20260325001",
  "remark": "柜台销售",
  "items": [
    {
      "medicineId": 1,
      "batchNo": "AMX-202603",
      "quantity": 5,
      "salePrice": 18.0
    }
  ]
}
```

## 8. 库存接口

### `GET /api/v1/inventories`

用途：

- 查询当前库存

查询参数建议：

- `keyword`
- `batchNo`
- `isControlled`
- `warningStatus`

### `GET /api/v1/inventories/{medicineId}/batches`

用途：

- 查询某药品所有批次库存

### `GET /api/v1/inventory-records`

用途：

- 查询库存流水

## 9. 预警接口

### `GET /api/v1/warnings`

用途：

- 查询预警记录

查询参数建议：

- `warningType`
- `status`
- `medicineId`

### `POST /api/v1/warnings/refresh`

用途：

- 手动刷新预警

说明：

- 实际生产环境可以只开放给管理员或改为定时任务。

## 10. 统计接口

### `GET /api/v1/stats/dashboard`

用途：

- 首页统计概览

返回建议字段：

- 药品总数
- 当前库存总量
- 今日入库单数
- 今日销售单数
- 低库存数量
- 临期数量
- 过期数量

### `GET /api/v1/stats/sales-trend`

用途：

- 销售趋势图

### `GET /api/v1/stats/warning-summary`

用途：

- 预警统计图

## 11. 日志接口

### `GET /api/v1/operation-logs`

用途：

- 查询操作日志

## 12. 开发顺序建议

接口开发顺序：

1. 认证接口
2. 药品档案接口
3. 入库接口
4. 销售接口
5. 库存接口
6. 预警接口
7. 统计接口
8. 用户角色接口

原因：

- 认证之后才能接业务
- 药品、入库、销售、库存、预警是一条主线
- 统计接口依赖前面业务数据
