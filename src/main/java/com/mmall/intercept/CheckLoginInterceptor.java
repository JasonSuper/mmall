package com.mmall.intercept;

import com.alibaba.fastjson.JSONObject;
import com.mmall.common.Const;
import com.mmall.common.ResponseEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author Jason
 * Create in 2018-06-05 11:24
 */
public class CheckLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private HttpSession session;

    @Resource
    private IUserService iUserService;

    private final Logger logger = LoggerFactory.getLogger(CheckLoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        ServerResponse serverResponse;
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 判断是否登陆
        if (user == null) {
            serverResponse = ServerResponse.createByErrorCodeMsg(ResponseEnum.NEED_LOGIN.getKey(), "用户未登录");
            setReeturnResponse(response, serverResponse);
        } else {
            // 判断是否管理员
            serverResponse = iUserService.checkAdminRole(user);
            if (!serverResponse.isSuccess()) {
                serverResponse = ServerResponse.createByErrorMsg("无权限操作");
                setReeturnResponse(response, serverResponse);
            }
        }
        return serverResponse.isSuccess();
    }

    private void setReeturnResponse(HttpServletResponse response, Object obj) throws IOException {
        String jsonObj = JSONObject.toJSON(obj).toString();
        PrintWriter writer = response.getWriter();
        writer.write(jsonObj);
        writer.close();
        response.flushBuffer();
    }
}
