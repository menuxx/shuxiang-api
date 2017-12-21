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
        location / {
          proxy_pass http://127.0.0.1:8080;
        }
        location /api/ {
          proxy_pass http://127.0.0.1:8070;
        }
}
```