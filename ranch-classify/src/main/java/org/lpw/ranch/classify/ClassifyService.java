package org.lpw.ranch.classify;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.ranch.recycle.RecycleService;

/**
 * @author lpw
 */
public interface ClassifyService extends RecycleService {
    /**
     * 检索分类信息集。
     *
     * @param code 编码前缀。
     * @return 分类信息集。
     */
    JSONObject query(String code);

    /**
     * 检索分类信息树。
     *
     * @param code 编码前缀。
     * @return 分类信息树。
     */
    JSONArray tree(String code);

    /**
     * 查找分类信息。
     *
     * @param ids   ID集。
     * @param links 是否解析链接分类。
     * @return 分类信息，如果不存在则返回空JSON。
     */
    JSONObject getJsons(String[] ids, boolean links);

    /**
     * 创建新分类。
     *
     * @param code  编码。
     * @param name  名称。
     * @param label 标签。
     * @return 分类JSON格式数据。
     */
    JSONObject create(String code, String name, String label);

    /**
     * 修改分类信息。
     *
     * @param id    ID值。
     * @param code  编码。
     * @param name  名称。
     * @param label 标签。
     * @return 分类JSON格式数据。
     */
    JSONObject modify(String id, String code, String name, String label);

    /**
     * 刷新缓存。
     */
    void refresh();
}
