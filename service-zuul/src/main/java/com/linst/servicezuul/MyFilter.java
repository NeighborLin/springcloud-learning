package com.linst.servicezuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * zuul除了路由功能，还能过滤，做一些安全验证
 */
@Component
public class MyFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(MyFilter.class);
    @Override
    //过滤器类型：4种不同生命周期过滤器类型
    public String filterType() {
        //pre路由之前,routing路由时,post,路由之后,error发送错误调用
        return "pre";
    }

    @Override
    //过滤器顺序
    public int filterOrder() {
        return 0;
    }

    @Override
    //判断逻辑，是否过滤，这里全部过滤
    public boolean shouldFilter() {
        return true;
    }

    @Override
    //过滤器具体逻辑
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(String.format("%s >>> %s",request.getMethod(),request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if (accessToken == null) {
            log.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("ok");
        return null;
    }
}
