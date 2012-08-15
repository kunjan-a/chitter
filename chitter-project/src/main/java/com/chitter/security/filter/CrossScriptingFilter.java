package com.chitter.security.filter;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 15/8/12
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
public class CrossScriptingFilter implements Filter {
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    public void destroy() {
        this.filterConfig = null;
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
    }
}
