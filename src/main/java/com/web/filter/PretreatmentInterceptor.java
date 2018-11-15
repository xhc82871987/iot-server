package com.web.filter;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 用来清除客户端的缓存，以及设置编码格式
 */
public class PretreatmentInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        httpServletRequest.setCharacterEncoding("utf-8");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        httpServletResponse.setHeader("Pragma","No-cache");
        httpServletResponse.setHeader("Cache-Control","no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("text/html;charset=utf-8");
    }
}
