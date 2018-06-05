package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Author Jason
 * Create in 2018-06-04 17:02
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Resource
    private IUserService iUserService;

    @ResponseBody
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);

        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if (user.getRole().equals(Const.Role.ROLE_ADMIN)) {
                // 管理员登陆
                session.setAttribute(Const.CURRENT_USER, user);
                return serverResponse;
            } else {
                return ServerResponse.createByErrorMsg("管理员才能进行登陆");
            }
        }
        return serverResponse;
    }
}
