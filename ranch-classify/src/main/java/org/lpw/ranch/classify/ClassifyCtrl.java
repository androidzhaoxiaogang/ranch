package org.lpw.ranch.classify;

import org.lpw.ranch.recycle.RecycleCtrlSupport;
import org.lpw.ranch.recycle.RecycleService;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(ClassifyModel.NAME + ".ctrl")
@Execute(name = "/classify/", key = ClassifyModel.NAME, code = "12")
public class ClassifyCtrl extends RecycleCtrlSupport {
    @Inject
    private Templates templates;
    @Inject
    private ClassifyService classifyService;

    /**
     * 检索分类信息集。
     * code 编码前缀，会自动匹配【code+%】。
     * pageSize 每页显示最大记录数。
     * pageNum 当前显示页码。
     *
     * @return {PageList}。
     */
    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return classifyService.query(request.get("code"));
    }

    /**
     * 检索分类信息树。
     * code 编码前缀，会自动匹配【code+%】。
     *
     * @return [ClassifyModel]。
     */
    @Execute(name = "tree", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 2)
    })
    public Object tree() {
        return classifyService.tree(request.get("code"));
    }

    /**
     * 获取分类信息。
     * ids 分类信息ID集。
     *
     * @return {id:ClassifyModel}。
     */
    @Execute(name = "get")
    public Object get() {
        return classifyService.get(request.getAsArray("ids"));
    }

    /**
     * 创建新分类。
     * code 编码。
     * pinyin 拼音码。
     * name 名称。
     * label 标签。
     *
     * @return {ClassifyModel}。
     */
    @Execute(name = "create", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "code", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "pinyin", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 6),
            @Validate(validator = Validators.SIGN)
    })
    public Object create() {
        return templates.get().success(classifyService.create(request.getMap()), null);
    }

    /**
     * 修改分类信息。
     * id ID值。
     * code 编码；为空表示不修改。
     * pinyin 拼音码；为空表示不修改。
     * name 名称；为空表示不修改。
     * label 标签；为空表示不修改。
     *
     * @return {ClassifyModel}。
     */
    @Execute(name = "modify", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "code", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "pinyin", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 6),
            @Validate(validator = Validators.SIGN)
    })
    public Object modify() {
        return templates.get().success(classifyService.modify(request.get("id"), request.get("code"), request.get("pinyin"), request.get("name"), request.getMap()), null);
    }

    /**
     * 刷新缓存信息。
     *
     * @return ""。
     */
    @Execute(name = "refresh", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object refresh() {
        classifyService.refresh();

        return "";
    }

    @Override
    protected RecycleService getRecycleService() {
        return classifyService;
    }
}
