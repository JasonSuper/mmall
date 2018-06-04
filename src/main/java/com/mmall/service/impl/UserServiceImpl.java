package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Author Jason
 * Create in 2018-06-04 6:12
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        ServerResponse serverResponse = checkValid(username, Const.USERNAME);
        if (!serverResponse.isSuccess())
            return serverResponse;

        // todo 密码登陆MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null)
            return ServerResponse.createByErrorMsg("密码错误");

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess())
            return validResponse;

        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess())
            return validResponse;

        user.setRole(Const.Role.ROLE_CUSTOMER);
        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0)
            return ServerResponse.createByErrorMsg("注册失败");
        else
            return ServerResponse.createBySuccessMsg("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0)
                    return ServerResponse.createByErrorMsg("用户名已存在");
            }

            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0)
                    return ServerResponse.createByErrorMsg("邮箱已被使用");
            }
        } else
            return ServerResponse.createByErrorMsg("参数错误");
        return ServerResponse.createBySuccessMsg("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            // 用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        String question = userMapper.selectQuestionByUserName(username);
        if (StringUtils.isNotBlank(question))
            return ServerResponse.createBySuccess(question);
        else
            return ServerResponse.createByErrorMsg("没有设置找回密码的问题");
    }

    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            String forgerToken = UUID.randomUUID().toString();
            TokenCache.setKey("token" + username, forgerToken);
            return ServerResponse.createBySuccess(forgerToken);
        }
        return ServerResponse.createByErrorMsg("问题的答案不正确");
    }
}
