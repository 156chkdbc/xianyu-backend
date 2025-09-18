package com.tcosfish.xianyu.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;

import java.io.IOException;


/**
 * @author tcosfish
 */
public class TraceIdFilter implements Filter {
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            // 清理 MDC 中的 traceId
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
