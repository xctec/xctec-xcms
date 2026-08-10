package com.df4j.xctec.xcms.core.context.token;

import com.df4j.xctec.xcms.core.context.ContextBinder;

public class TokenContextBinder extends ContextBinder<TokenContextAccessor> {

    public TokenContextBinder(TokenContextAccessor contextAccess) {
        super(contextAccess);
    }

    @Override
    public void afterSingletonsInstantiated() {
        TokenContextUtils.bind(this.getContextAccess());
    }
}
