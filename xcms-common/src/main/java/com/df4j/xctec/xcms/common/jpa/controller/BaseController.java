package com.df4j.xctec.xcms.common.jpa.controller;

import com.df4j.xctec.xcms.common.jpa.dto.BaseDto;
import com.df4j.xctec.xcms.common.jpa.form.BaseForm;
import com.df4j.xctec.xcms.common.jpa.service.BaseService;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.PageQuery;
import com.df4j.xctec.xcms.core.vo.PageVo;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Getter
public class BaseController<
        D extends BaseDto,
        F extends BaseForm,
        QP extends PageQuery,
        S extends BaseService<?, ?, D, F, QP, ?, ?>> {

    @Setter(onMethod_ = {@Autowired})
    private S service;

    @PostMapping("/page")
    public ResultVo<PageVo<D>> page(@RequestBody QP params) {
        PageVo<D> res = this.getService().page(params);
        return ResultUtils.success(res);
    }

    @PostMapping("/list")
    public ResultVo<List<D>> list(@RequestBody QP params) {
        List<D> res = this.getService().list(params);
        return ResultUtils.success(res);
    }

    @PostMapping("/create")
    public ResultVo<F> create(@RequestBody F form) {
        F res = this.getService().create(form);
        return ResultUtils.success(res);
    }

    @PostMapping("/edit")
    public ResultVo<F> edit(@RequestBody F form) {
        F res = this.getService().edit(form);
        return ResultUtils.success(res);
    }

    @PostMapping("/del")
    public ResultVo<Long> del(Long id) {
        long count = this.getService().del(id);
        return ResultUtils.success(count);
    }

    @PostMapping("/delAll")
    public ResultVo<Long> del(List<Long> ids) {
        long count = this.getService().delAll(ids);
        return ResultUtils.success(count);
    }
}
