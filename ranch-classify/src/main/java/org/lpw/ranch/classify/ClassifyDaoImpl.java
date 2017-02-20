package org.lpw.ranch.classify;

import org.lpw.ranch.recycle.Recycle;
import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lpw
 */
@Repository(ClassifyModel.NAME + ".dao")
class ClassifyDaoImpl implements ClassifyDao {
    @Inject
    private DataSource dataSource;
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<ClassifyModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ClassifyModel.class).where(Recycle.No.getSql()).order("c_code").size(pageSize).page(pageNum), null);
    }

    @Override
    public PageList<ClassifyModel> query(String code, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ClassifyModel.class).where(Recycle.No.getSql() + " and c_code like ?").order("c_code").size(pageSize).page(pageNum),
                new Object[]{dataSource.getDialect(null).getLike(code, false, true)});
    }

    @Override
    public ClassifyModel findById(String id) {
        return liteOrm.findById(ClassifyModel.class, id);
    }

    @Override
    public void save(ClassifyModel classify) {
        liteOrm.save(classify);
    }

    @Override
    public void delete(String code) {
        liteOrm.update(new LiteQuery(ClassifyModel.class).set(Recycle.Yes.getSql()).where("c_code like ?"),
                new Object[]{dataSource.getDialect(null).getLike(code, false, true)});
    }

    @Override
    public void restore(Set<String> codes) {
        StringBuilder where = new StringBuilder(Recycle.Yes.getSql()).append(" and c_code in(");
        List<Object> args = new ArrayList<>();
        codes.forEach(code -> {
            if (args.size() > 0)
                where.append(',');
            where.append('?');
            args.add(code);
        });
        liteOrm.update(new LiteQuery(ClassifyModel.class).set(Recycle.No.getSql()).where(where.append(')').toString()), args.toArray());
    }
}
