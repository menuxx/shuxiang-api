# 书享项目资料
api 服务，提供 大V送树，一书一码，后台管理，相关接口

### 测试服务器资料
> 阿里云 ECS  root: usj627!0ls#z

### nginx 代理基本配置
```
server {
        server_name wxtest.qurenjia.com;
        listen 80 http2;
        access_log /var/log/nginx/access_shuxiang.log vhost;
        # 微信应用
        location / {
          proxy_pass http://127.0.0.1:8080;
        }
        # 应用调用的 api 集合
        location /api/ {
          proxy_pass http://127.0.0.1:8070;
        }
        # 商户出版社
        location /merchant/ {
            proxy_pass http://127.0.0.1:8083;
        }
        # 只支持其他域名的 wx 授权 code 回调
        location = /wxauthcodetestredirect_2017 {
            rewrite ^ http://wxdev.qurenjia.com permanent;
        }
}
```

### 设置 rabbitmq 为 web mangement 用户
```
rabbitmqctl add_user test test
rabbitmqctl set_user_tags test administrator
rabbitmqctl set_permissions -p / test ".*" ".*" ".*"
```

### mongodb
```js
db.createUser({
    user: 'api-server',
    pwd: '&asNh$80d!asd',
    roles: [ { role: "readWrite", db: 'xuren_read' }, { role: "dbAdmin", db: 'xuren_read' }]
})
```

### 微信验证文件
> 小程序二维码规则验证文件 static/bk/odtdiKTRCA.txt

> 公众号域名验证文件 static/MP_verify_HbqV7XuhqgeCEAv9.txt