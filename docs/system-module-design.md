# “药捷”药品管理系统模块拆解

## 1. 系统总拆分

系统可以拆成三层来做：

1. 前端页面模块：负责页面展示、表单录入、图表分析、权限控制。
2. 后端功能模块：负责认证鉴权、业务流程、库存计算、预警逻辑、数据接口。
3. 数据库表设计：负责保存药品、库存、出入库、预警、用户权限等核心数据。

你后面的开发顺序建议也是按这三层推进。

## 2. 前端页面模块

前端建议使用 `Vue 3 + Vue Router + Pinia + Axios + Element Plus`。

### 2.1 页面结构总览

建议前端页面分为以下几类：

- 登录页
- 首页仪表盘
- 药品档案页
- 采购入库页
- 销售出库页
- 库存管理页
- 预警中心页
- 管制药品页
- 统计分析页
- 用户与角色管理页
- 操作日志页

### 2.2 具体页面拆分

#### 1. 登录页

作用：

- 用户登录系统
- 根据角色跳转页面

核心功能：

- 用户名密码登录
- 登录状态保存
- 登录失败提示

对应后端接口：

- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`

#### 2. 首页仪表盘

作用：

- 给管理员或员工一个系统概览

建议展示内容：

- 药品总数
- 当前库存总量
- 低库存药品数量
- 临期药品数量
- 今日销售笔数
- 今日入库笔数
- 近7天销售趋势图
- 预警快捷入口

#### 3. 药品档案页

作用：

- 管理药品基础信息

子功能：

- 药品列表查询
- 新增药品
- 编辑药品
- 查看药品详情
- 启用/停用药品

查询条件建议：

- 药品名称
- 药品编码
- 分类
- 供应商
- 是否管制药品

#### 4. 采购入库页

作用：

- 处理药品采购和入库登记

子功能：

- 新建入库单
- 添加入库明细
- 按批次录入
- 查看历史入库单
- 查看入库详情

关键输入项：

- 药品
- 批次号
- 数量
- 进价
- 供应商
- 生产日期
- 有效期

#### 5. 销售出库页

作用：

- 处理药品销售或手动出库

子功能：

- 药品检索
- 库存选择
- 批次选择
- 出库登记
- 销售单查询

关键校验：

- 库存充足
- 药品未过期
- 当前用户有操作权限

#### 6. 库存管理页

作用：

- 查看实时库存和批次库存

子功能：

- 当前库存查询
- 按批次查看库存
- 库存盘点
- 库存调整
- 库存流水查询

建议展示字段：

- 药品名称
- 批次号
- 当前数量
- 安全库存
- 有效期
- 是否低库存
- 是否临期

#### 7. 预警中心页

作用：

- 集中查看系统风险

预警分类：

- 库存不足预警
- 药品临期预警
- 药品过期预警

建议功能：

- 预警列表
- 按类型筛选
- 标记已处理
- 跳转到对应药品或库存页面

#### 8. 管制药品页

作用：

- 单独查看和管理特殊药品

建议功能：

- 管制药品列表
- 管制药品出入库记录
- 管制药品库存查询
- 管制药品预警查询
- 管制药品操作日志

这页很重要，因为它能突出你的系统特色。

#### 9. 统计分析页

作用：

- 展示经营数据和库存变化

建议图表：

- 近7天或30天销售趋势
- 药品销量排行
- 库存分类占比
- 低库存药品统计
- 临期药品统计

#### 10. 用户与角色管理页

作用：

- 维护系统用户与角色权限

子功能：

- 用户列表
- 新增用户
- 分配角色
- 重置密码
- 角色权限配置

#### 11. 操作日志页

作用：

- 记录关键操作留痕

建议记录范围：

- 登录日志
- 入库操作
- 出库操作
- 库存调整
- 管制药品操作
- 用户权限变更

## 3. 前端推荐目录

```text
frontend/src
├─ api
├─ assets
├─ components
├─ layout
├─ router
├─ store
├─ utils
├─ views
│  ├─ login
│  ├─ dashboard
│  ├─ medicine
│  ├─ purchase
│  ├─ sale
│  ├─ inventory
│  ├─ warning
│  ├─ controlled
│  ├─ stats
│  ├─ user
│  └─ log
└─ App.vue
```

## 4. 后端功能模块

后端建议使用 `Spring Boot + Spring Security + MySQL + Redis`。

### 4.1 后端模块总览

建议拆成以下模块：

- 认证授权模块
- 用户角色模块
- 药品档案模块
- 分类与供应商模块
- 采购入库模块
- 销售出库模块
- 库存管理模块
- 预警管理模块
- 管制药品监管模块
- 统计分析模块
- 日志审计模块

### 4.2 各模块职责

#### 1. 认证授权模块

职责：

- 登录认证
- Token 签发与校验
- 当前用户信息获取
- 接口访问控制

建议实现：

- Spring Security
- JWT 或 Session

#### 2. 用户角色模块

职责：

- 用户管理
- 角色管理
- 菜单权限管理
- 接口权限管理

建议角色：

- `ADMIN` 系统管理员
- `PHARMACY_MANAGER` 药房管理员
- `INVENTORY_MANAGER` 库存管理员
- `SALES_CLERK` 销售人员

#### 3. 药品档案模块

职责：

- 维护药品主数据
- 记录药品基础属性
- 管理药品状态

核心接口建议：

- `GET /api/v1/medicines`
- `POST /api/v1/medicines`
- `PUT /api/v1/medicines/{id}`
- `GET /api/v1/medicines/{id}`

#### 4. 分类与供应商模块

职责：

- 管理药品分类
- 管理供应商信息

核心接口建议：

- `GET /api/v1/categories`
- `POST /api/v1/categories`
- `GET /api/v1/suppliers`
- `POST /api/v1/suppliers`

#### 5. 采购入库模块

职责：

- 创建入库单
- 保存入库明细
- 增加库存
- 生成库存流水

核心业务逻辑：

1. 校验入库单参数
2. 保存入库主表
3. 保存入库明细
4. 按药品批次增加库存
5. 记录库存流水
6. 刷新预警状态

#### 6. 销售出库模块

职责：

- 创建销售单
- 扣减库存
- 记录销售明细
- 校验过期和权限

核心业务逻辑：

1. 检查药品是否存在
2. 检查批次库存是否充足
3. 检查是否过期
4. 检查是否为管制药品
5. 扣减库存
6. 写入销售记录
7. 写入库存流水

#### 7. 库存管理模块

职责：

- 查询实时库存
- 管理库存盘点
- 处理库存调整
- 提供库存流水查询

建议重点：

- 库存一定按“药品 + 批次”管理
- 提供总库存视图和批次库存视图

#### 8. 预警管理模块

职责：

- 低库存预警
- 临期预警
- 过期预警

触发方式建议：

- 入库后触发
- 出库后触发
- 定时任务扫描触发

#### 9. 管制药品监管模块

职责：

- 区分普通药品和管制药品
- 限制操作角色
- 记录特殊药品日志
- 提供专门查询入口

建议第一版不要把流程做得太复杂，先完成：

- 特殊标记
- 权限校验
- 单独日志
- 单独查询

#### 10. 统计分析模块

职责：

- 统计销售数据
- 统计库存数据
- 提供图表接口

建议接口：

- `GET /api/v1/stats/sales-trend`
- `GET /api/v1/stats/top-medicines`
- `GET /api/v1/stats/inventory-summary`
- `GET /api/v1/stats/warning-summary`

#### 11. 日志审计模块

职责：

- 记录关键操作日志
- 记录登录日志
- 记录异常信息

建议重点记录：

- 用户登录
- 药品新增修改
- 入库
- 出库
- 库存调整
- 管制药品操作

## 5. 后端推荐分层结构

```text
backend/src/main/java/com/yaojie
├─ common
├─ config
├─ controller
├─ service
├─ service/impl
├─ mapper
├─ entity
├─ dto
├─ vo
├─ security
├─ task
└─ utils
```

其中：

- `entity`：数据库实体
- `dto`：前端传入参数对象
- `vo`：返回前端的展示对象
- `mapper`：MyBatis 数据访问层
- `task`：预警定时任务

## 6. 数据库表设计

数据库建议使用 `MySQL`。下面这套表结构已经可以覆盖你的毕业设计核心功能。

### 6.1 用户权限相关表

#### 1. `sys_user` 用户表

主要字段：

- `id` bigint 主键
- `username` varchar 用户名
- `password` varchar 密码
- `real_name` varchar 真实姓名
- `phone` varchar 手机号
- `status` tinyint 状态
- `created_at` datetime
- `updated_at` datetime

作用：

- 保存系统登录用户

#### 2. `sys_role` 角色表

主要字段：

- `id`
- `role_name`
- `role_code`
- `remark`

作用：

- 定义角色，如管理员、库存员、销售员

#### 3. `sys_user_role` 用户角色关联表

主要字段：

- `id`
- `user_id`
- `role_id`

作用：

- 实现用户和角色多对多关联

#### 4. `sys_permission` 权限表

主要字段：

- `id`
- `permission_name`
- `permission_code`
- `type`
- `path`

作用：

- 保存菜单权限或接口权限

#### 5. `sys_role_permission` 角色权限关联表

主要字段：

- `id`
- `role_id`
- `permission_id`

作用：

- 实现角色和权限多对多关联

### 6.2 基础资料相关表

#### 6. `medicine_category` 药品分类表

主要字段：

- `id`
- `category_name`
- `category_code`
- `remark`

#### 7. `supplier` 供应商表

主要字段：

- `id`
- `supplier_name`
- `contact_person`
- `phone`
- `address`
- `status`

#### 8. `medicine` 药品信息表

主要字段：

- `id`
- `medicine_code`
- `medicine_name`
- `category_id`
- `specification`
- `unit`
- `manufacturer`
- `supplier_id`
- `purchase_price`
- `sale_price`
- `safe_stock`
- `shelf_life_days`
- `is_controlled`
- `status`
- `remark`
- `created_at`
- `updated_at`

说明：

- `is_controlled` 用来标记是否为管制药品
- `safe_stock` 用来做库存预警阈值

### 6.3 入库业务相关表

#### 9. `purchase_order` 入库单主表

主要字段：

- `id`
- `order_no`
- `supplier_id`
- `total_amount`
- `operator_id`
- `status`
- `remark`
- `created_at`

作用：

- 记录一次入库业务的主单

#### 10. `purchase_order_item` 入库单明细表

主要字段：

- `id`
- `purchase_order_id`
- `medicine_id`
- `batch_no`
- `quantity`
- `purchase_price`
- `production_date`
- `expiry_date`
- `subtotal`

作用：

- 记录每种药品的批次入库明细

### 6.4 销售业务相关表

#### 11. `sale_order` 销售单主表

主要字段：

- `id`
- `order_no`
- `total_amount`
- `operator_id`
- `status`
- `remark`
- `created_at`

#### 12. `sale_order_item` 销售单明细表

主要字段：

- `id`
- `sale_order_id`
- `medicine_id`
- `batch_no`
- `quantity`
- `sale_price`
- `subtotal`

说明：

- 销售明细必须带上 `batch_no`
- 这样才能实现完整追溯

### 6.5 库存相关表

#### 13. `inventory` 当前库存表

主要字段：

- `id`
- `medicine_id`
- `batch_no`
- `current_quantity`
- `locked_quantity`
- `production_date`
- `expiry_date`
- `last_inbound_time`
- `last_outbound_time`
- `updated_at`

作用：

- 保存每个药品每个批次的实时库存

这张表是系统最核心的表之一。

#### 14. `inventory_record` 库存流水表

主要字段：

- `id`
- `medicine_id`
- `batch_no`
- `change_type`
- `change_quantity`
- `before_quantity`
- `after_quantity`
- `source_type`
- `source_id`
- `operator_id`
- `remark`
- `created_at`

说明：

- `change_type`：IN、OUT、ADJUST
- `source_type`：PURCHASE、SALE、CHECK

作用：

- 记录每一次库存变化

#### 15. `inventory_check` 库存盘点表

主要字段：

- `id`
- `check_no`
- `medicine_id`
- `batch_no`
- `system_quantity`
- `actual_quantity`
- `diff_quantity`
- `operator_id`
- `remark`
- `created_at`

作用：

- 处理盘点和库存修正

### 6.6 预警与审计相关表

#### 16. `warning_record` 预警记录表

主要字段：

- `id`
- `medicine_id`
- `batch_no`
- `warning_type`
- `warning_level`
- `warning_message`
- `status`
- `created_at`
- `handled_at`

说明：

- `warning_type`：LOW_STOCK、EXPIRY_SOON、EXPIRED

#### 17. `operation_log` 操作日志表

主要字段：

- `id`
- `user_id`
- `module_name`
- `operation_type`
- `business_no`
- `content`
- `ip`
- `created_at`

作用：

- 记录重要业务操作

## 7. 核心表关系

你可以先这样理解表关系：

- 一个用户可以有多个角色
- 一个角色可以有多个权限
- 一个药品属于一个分类
- 一个药品可以关联一个主要供应商
- 一个入库单有多条入库明细
- 一个销售单有多条销售明细
- 一个药品可以有多个批次库存
- 一个药品可以对应多条预警记录
- 一个库存变化会产生一条库存流水

## 8. 第一版最小可用功能

如果你想先做能跑通的版本，建议第一阶段只做这些：

- 登录
- 用户角色权限
- 药品档案管理
- 供应商管理
- 药品入库
- 药品销售
- 库存查询
- 低库存和临期预警
- 管制药品标记和权限限制
- 销售趋势和预警统计

这套已经足够支撑毕业设计主线。

## 9. 推荐开发顺序

为了避免越做越乱，建议按这个顺序来：

1. 先建数据库基础表和药品表。
2. 做登录、用户、角色权限。
3. 做药品档案和供应商管理。
4. 做入库模块和库存增加逻辑。
5. 做销售模块和库存扣减逻辑。
6. 做预警模块。
7. 做统计图表。
8. 最后补日志和细节优化。

## 10. 结论

如果把你的系统拆开，本质上就是：

- 前端负责把“药品、库存、预警、统计、权限”可视化
- 后端负责把“入库、销售、库存、预警、权限”流程跑通
- 数据库负责把“药品批次、库存变化、业务单据、用户权限”存清楚

其中最核心的设计点只有三个：

- 库存按批次管理
- 管制药品单独加强权限
- 所有出入库都要能追溯

只要这三个点立住了，你这个系统的骨架就稳了。
