package com.df4j.xctec.xcms.core.context.tenant;

import com.df4j.xctec.xcms.core.context.ContextBinder;

public class TenantContextBinder extends ContextBinder<TenantContextAccessor> {

    public TenantContextBinder(TenantContextAccessor contextAccess) {
        super(contextAccess);
    }

    @Override
    public void afterSingletonsInstantiated() {
        TenantContextUtils.bind(this.getContextAccess());
    }
}
