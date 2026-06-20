# 农贸市场计量公平秤监管平台（fair-scale）

面向农贸市场计量监管的纯后端管理平台。围绕摊位经营户、计量器具（电子秤）台账与公平秤复称记录，
提供登录鉴权、角色权限与基础的台账管理能力，作为后续功能迭代的基座。

## 技术栈

- Java 17 + Spring Boot 3.2
- MyBatis 3（注解方式）
- MySQL 8（utf8mb4，中文无乱码）
- JWT（jjwt）鉴权 + BCrypt 口令哈希
- Docker / Docker Compose 一键启动

## 一键启动

```bash
docker compose up --build
```

启动后：
- MySQL 监听宿主机 `13399` 端口（容器内 3306），首次启动自动执行 `db/schema.sql`
- API 监听 `5120` 端口
- 服务起来后访问健康检查：`GET http://localhost:5120/api/health`

停止并清理数据卷：

```bash
docker compose down -v
```

## 默认账号

| 用户名 | 密码 | 角色 | 权限范围 |
|--------|------|------|----------|
| admin | admin123 | admin | 全部，含用户管理、删除 |
| inspector | inspect123 | inspector | 摊位/器具/复称的增改 |
| viewer | viewer123 | viewer | 只读查询 |

## 主要接口

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| POST | /api/auth/login | 登录获取 JWT | 公开 |
| GET | /api/auth/me | 当前登录信息 | 登录 |
| GET | /api/health | 健康检查 | 公开 |
| GET | /api/stalls | 摊位分页查询（keyword/status） | 登录 |
| POST | /api/stalls | 新建摊位 | admin/inspector |
| PUT | /api/stalls/{id} | 编辑摊位 | admin/inspector |
| DELETE | /api/stalls/{id} | 删除摊位 | admin |
| GET | /api/scales | 计量器具分页 | 登录 |
| GET | /api/scales/{id} | 器具详情（含检定是否过期） | 登录 |
| POST | /api/scales | 新建器具 | admin/inspector |
| PUT | /api/scales/{id} | 编辑器具 | admin/inspector |
| DELETE | /api/scales/{id} | 删除器具 | admin |
| GET | /api/rechecks | 公平秤复称记录分页 | 登录 |
| POST | /api/rechecks | 录入复称记录（自动算短缺量并判定） | admin/inspector |
| GET | /api/users | 用户列表 | admin |
| POST | /api/users | 新建用户 | admin |

请求示例：

```bash
# 登录
curl -X POST http://localhost:5120/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 携带 token 访问
curl http://localhost:5120/api/stalls -H "Authorization: Bearer <token>"
```

## 本地运行单元测试

```bash
mvn clean test
```

不依赖数据库，覆盖 JWT 签发/校验与复称短缺量计算逻辑。
```
