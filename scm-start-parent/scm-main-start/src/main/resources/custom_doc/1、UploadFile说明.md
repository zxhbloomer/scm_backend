# 关于文件上传的说明

### - 文件服务器：
文件上传通过file.xinyirunscm.com服务来进行上传，该服务和wms服务不相关，通过用户名密码上传文件后，返回相应的文件地址url，再有其他业务系统进行保存。

### - 文件服务器上传url及其账号：
app_key：f5441a15-38e7-403a-8b77-b01bcffd663e

secret_key：0aec839c-10f0-4e2b-804a-f33b9c11d07b

url：http://file.xinyirunscm.com/fs/api/service/v1/upload

### - 文件服务器调用方式：
![调用方式](http://file.xinyirunscm.com/file/app_monitor/2021/20211224/526/6ff2ff573a454980a6c10945212e73ab/img.png)

### - 文件服务器返回值样例：
```json
{
"code": 0,
"data": {
        "fileName": "img.png",
        "fileUuid": "6ff2ff573a454980a6c10945212e73ab",
        "file_size": 74868,
        "internal_url": "http://172.19.130.79:8888/file/app_monitor/2021/20211224/526/6ff2ff573a454980a6c10945212e73ab/img.png",
        "url": "http://file.xinyirunscm.com/file/app_monitor/2021/20211224/526/6ff2ff573a454980a6c10945212e73ab/img.png"
    },
    "message": "调用成功",
    "path": "http://127.0.0.1:18089/fs/api/service/v1/upload",
    "success": true,
    "timestamp": "2021-12-24 11:44:45"
}
```