package com.df4j.xctec.xcms.common.jpa.controller;

import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import com.df4j.xctec.xcms.common.jpa.form.BaseTreeForm;
import com.df4j.xctec.xcms.common.jpa.service.BaseTreeService;
import com.df4j.xctec.xcms.core.vo.PageQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
public abstract class BaseTreeController<
        D extends BaseTreeDto<D>,
        F extends BaseTreeForm,
        QP extends PageQuery,
        S extends BaseTreeService<?, ?, D, F, QP, ?, ?>>
        extends BaseController<D, F, QP, S> {

    @PostMapping("/tree")
    public List<D> tree(QP params) {
        return this.getService().tree(params);
    }
}
