# 检索作者评论

请求
- Service Key - ranch.comment.query-by-author
- URI - /comment/query-by-author

参数
- author 作者ID值。
- pageSize 每页显示记录数。
- pageNum 当前显示页数。

返回值
```json
{
  "count":"总记录数",
  "size":"每页显示记录数",
  "number":"当前显示页数",
  "list":[{
    "id": "ID值",
    "owner": {},
    "subject": "标题",
    "label": "标签",
    "content": "内容",
    "audit":"审核状态：0-待审核；1-审核通过；2-审核不通过"
  }]
}
```
- owner 所有者信息，未找到则仅包含id属性。
