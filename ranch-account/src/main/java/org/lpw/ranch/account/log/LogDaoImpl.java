package org.lpw.ranch.account.log;

import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Repository(LogModel.NAME + ".dao")
class LogDaoImpl implements LogDao {
    @Inject
    private Validator validator;
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<LogModel> query(String user, Timestamp start, Timestamp end, int pageSize, int pageNum) {
        StringBuilder where = new StringBuilder();
        List<Object> args = new ArrayList<>();
        if (!validator.isEmpty(user)) {
            where.append("c_user=?");
            args.add(user);
        }
        if (start != null) {
            if (args.size() > 0)
                where.append(" and ");
            where.append("c_start>=?");
            args.add(start);
        }
        if (end != null) {
            if (args.size() > 0)
                where.append(" and ");
            where.append("c_start<=?");
            args.add(end);
        }

        return liteOrm.query(new LiteQuery(LogModel.class).where(where.toString()).order("c_start desc").size(pageSize).page(pageNum), args.toArray());
    }

    @Override
    public LogModel findById(String id) {
        return liteOrm.findById(LogModel.class, id);
    }

    @Override
    public void save(LogModel log) {
        liteOrm.save(log);
    }
}
