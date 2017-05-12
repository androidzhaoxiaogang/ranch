package org.lpw.ranch.account.log;

import com.alibaba.fastjson.JSONObject;
import org.lpw.ranch.account.AccountModel;

import java.sql.Date;
import java.util.Map;

/**
 * @author lpw
 */
public interface LogService {
    /**
     * 状态。
     */
    enum State {
        /**
         * 待处理。
         */
        New,
        /**
         * 审核通过。
         */
        Pass,
        /**
         * 审核不通过。
         */
        Refuse,
        /**
         * 已完成。
         */
        Complete
    }

    /**
     * 检索账户操作日志集。
     *
     * @param uid   用户UID。
     * @param start 开始日期。
     * @param end   结束日期。
     * @return 操作日志集。
     */
    JSONObject query(String uid, Date start, Date end);

    /**
     * 新增日志。
     *
     * @param account 账户信息。
     * @param type    类型。
     * @param amount  数量。
     * @param state   状态。
     * @param map     参数集。
     * @return 日志ID值。
     */
    String create(AccountModel account, String type, int amount, State state, Map<String, String> map);

    /**
     * 设置为完成。
     *
     * @param id ID值。
     */
    void complete(String id);
}
