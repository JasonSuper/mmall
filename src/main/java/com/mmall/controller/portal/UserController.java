package com.mmall.controller.portal;

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
 * Create in 2018-06-04 6:07
 */
@Controller
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
    @ResponseBody
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);

        if (serverResponse.isSuccess())
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());

        return iUserService.login(username, password);
    }

    /**
     * 退出登录
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "loginout.do", method = RequestMethod.GET)
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
    @ResponseBody
    @RequestMapping(value = "register.do", method = RequestMethod.GET)
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
    @ResponseBody
    @RequestMapping(value = "checkValid.do", method = RequestMethod.GET)
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取当前用户信息
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
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
    @ResponseBody
    @RequestMapping(value = "forgetGetQuestion.do", method = RequestMethod.GET)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    @ResponseBody
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.GET)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.forgetCheckAnswer(username, question, answer);
    }
}
