package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseEnum;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Author Jason
 * Create in 2018-06-04 6:07
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService iUserService;

    /**
     * 用户登陆
     *
     * @param username 用户名
     * @param password 密码
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);

        if (serverResponse.isSuccess())
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());

        return serverResponse;
    }

    /**
     * 退出登录
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "loginout.do", method = RequestMethod.POST)
    public ServerResponse<String> loginout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册用户
     *
     * @param user 用户对象
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 校验信息是否存在
     *
     * @param str  需要校验的字符串
     * @param type 需要校验的类型
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取当前用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null)
            return ServerResponse.createBySuccess(user);
        else
            return ServerResponse.createByErrorMsg("用户未登录，无法获取信息");
    }

    /**
     * 获取用户重置密码的提示问题
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 检查答案是否正确
     *
     * @param username 用户名
     * @param question 问题
     * @param answer   答案
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.forgetCheckAnswer(username, question, answer);
    }

    /**
     * 忘记密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param token       令牌
     * @return
     */
    @RequestMapping(value = "forget_resst_password.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetResstPassword(String username, String passwordNew, String token) {
        return iUserService.forgetResetPassword(username, passwordNew, token);
    }

    /**
     * 登录状态下重置密码
     *
     * @param session
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorMsg("用户未登陆");
        return iUserService.resstPassword(user, passwordOld, passwordNew);
    }

    /**
     * 更改用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null)
            return ServerResponse.createByErrorMsg("用户未登陆");

        user.setId(currentUser.getId());
        ServerResponse<User> serverResponse = iUserService.updateUserInformation(user);
        if (serverResponse.isSuccess())
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        return serverResponse;
    }

    /**
     * 获取用户信息，并检查是否需要强制登录
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    public ServerResponse<User> getInformation(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMsg(ResponseEnum.NEED_LOGIN.getKey(), "用户未登陆");
        return iUserService.getInformation(user.getId());
    }
}
