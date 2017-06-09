package org.lpw.ranch.payment;

import com.alibaba.fastjson.JSONObject;
import org.lpw.ranch.lock.LockHelper;
import org.lpw.ranch.util.Pagination;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.DateTime;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Service(PaymentModel.NAME + ".service")
public class PaymentServiceImpl implements PaymentService {
    private static final String LOCK_ORDER_NO = PaymentModel.NAME + ".service.order-no:";

    @Inject
    private DateTime dateTime;
    @Inject
    private Generator generator;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Json json;
    @Inject
    private Http http;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private PaymentDao paymentDao;
    private Set<String> ignores;

    @Override
    public JSONObject query(String type, String user, String orderNo, String tradeNo, int state, String start, String end) {
        return paymentDao.query(type, user, orderNo, tradeNo, state, dateTime.getStart(start),
                dateTime.getEnd(end), pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject success(String id, Map<String, String> map) {
        PaymentModel payment = find(id);
        if (payment.getState() != 1)
            complete(payment, 1, "success", map);

        return modelHelper.toJson(payment);
    }

    @Override
    public JSONObject notice(String id) {
        PaymentModel payment = find(id);
        notice(payment);

        return modelHelper.toJson(payment);
    }

    @Override
    public PaymentModel find(String uk) {
        PaymentModel payment = paymentDao.findById(uk);

        return payment == null ? paymentDao.findByOrderNo(uk) : payment;
    }

    @Override
    public JSONObject create(String type, String user, int amount, String notify, Map<String, String> map) {
        PaymentModel payment = new PaymentModel();
        payment.setType(type);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setStart(dateTime.now());
        payment.setOrderNo(newOrderNo(payment.getStart()));
        payment.setTradeNo("");
        payment.setNotify(notify);
        JSONObject json = new JSONObject();
        json.put("create", putToJson(new JSONObject(), map));
        payment.setJson(json.toJSONString());
        paymentDao.save(payment);

        return modelHelper.toJson(payment);
    }

    private String newOrderNo(Timestamp now) {
        String time = dateTime.toString(now, "yyyyMMddHHmmssSSS");
        while (true) {
            String orderNo = time + generator.random(4);
            if (paymentDao.findByOrderNo(orderNo) == null)
                return orderNo;
        }
    }

    @Override
    public JSONObject complete(String orderNo, int amount, String tradeNo, int state, Map<String, String> map) {
        String lock = lockHelper.lock(LOCK_ORDER_NO + orderNo, 1000L);
        PaymentModel payment = find(orderNo);
        if (payment.getState() == 0) {
            payment.setAmount(amount);
            payment.setTradeNo(tradeNo);
            complete(payment, state, "complete", map);
        }
        lockHelper.unlock(lock);

        return modelHelper.toJson(payment);
    }

    private void complete(PaymentModel payment, int state, String name, Map<String, String> map) {
        payment.setState(state);
        payment.setEnd(dateTime.now());
        JSONObject json = this.json.toObject(payment.getJson());
        json.put(name, putToJson(new JSONObject(), map));
        payment.setJson(json.toJSONString());
        paymentDao.save(payment);
        notice(payment);
    }

    private JSONObject putToJson(JSONObject object, Map<String, String> map) {
        if (ignores == null) {
            ignores = new HashSet<>();
            ignores.add("id");
            ignores.add("type");
            ignores.add("user");
            ignores.add("amount");
            ignores.add("orderNo");
            ignores.add("tradeNo");
            ignores.add("notify");
            ignores.add("state");
            ignores.add("sign");
            ignores.add("sign-time");
        }

        map.forEach((key, value) -> {
            if (ignores.contains(key))
                return;

            object.put(key, value);
        });

        return object;
    }

    private void notice(PaymentModel payment) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", payment.getType());
        parameters.put("user", payment.getUser());
        parameters.put("amount", converter.toString(payment.getAmount(), "0"));
        parameters.put("orderNo", payment.getOrderNo());
        parameters.put("state", converter.toString(payment.getState(), "0"));
        parameters.put("start", dateTime.toString(payment.getStart()));
        parameters.put("end", dateTime.toString(payment.getEnd()));
        http.post(payment.getNotify(), null, parameters);
    }
}
