# 发送消息

请求
- Service Key - ranch.message.send
- URI - /message/send

参数
- type 接收者类型：0-好友；1-群组。
- receiver 接收者ID，好友ID、群组ID等。
- format 消息格式：0-文本；1-图片；2-音频；3-视频；4-文件；5-红包；6-公告。
- content 内容。

返回值
```text
""
```