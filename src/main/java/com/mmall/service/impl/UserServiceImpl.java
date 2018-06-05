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
import javax.servlet.http.HttpSession;
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
        if (serverResponse.isSuccess())
            return ServerResponse.createByErrorMsg("用户名不存在");

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
            TokenCache.setKey(TokenCache.TOKEN_PERFIX + username, forgerToken);
            return ServerResponse.createBySuccess(forgerToken);
        }
        return ServerResponse.createByErrorMsg("问题的答案不正确");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgettoken) {
        if (!StringUtils.isNotBlank(forgettoken))
            return ServerResponse.createByErrorMsg("参数错误，需要token");

        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PERFIX + username);
        if (!StringUtils.isNotBlank(token))
            return ServerResponse.createByErrorMsg("token无效或已过期");

        if (StringUtils.equals(forgettoken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePassword(username, md5Password);

            if (resultCount > 0)
                return ServerResponse.createBySuccessMsg("修改密码成功");
        } else {
            return ServerResponse.createByErrorMsg("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createBySuccessMsg("修改密码失败");
    }

    public ServerResponse<String> resstPassword(User user, String passwordOld, String passwordNew) {
        String MD5PasswordOld = MD5Util.MD5EncodeUtf8(passwordOld);
        String MD5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew);

        // 防止横向越权，必须校验旧密码
        int resultCount = userMapper.checkPassword(MD5PasswordOld, user.getId());
        if (resultCount == 0)
            return ServerResponse.createByErrorMsg("旧密码错误");

        user.setPassword(MD5PasswordNew);
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0)
            return ServerResponse.createBySuccessMsg("密码重置成功");
        return ServerResponse.createBySuccessMsg("密码重置失败");
    }

    public ServerResponse<User> updateUserInformation(User user) {
        // username不能被更新
        // 校验新email是否存在
        int checkCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (checkCount > 0)
            return ServerResponse.createBySuccessMsg("邮箱已被使用");

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(updateUser.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0)
            return ServerResponse.createBySuccess("用户信息更新成功", userMapper.selectByPrimaryKey(updateUser.getId()));
        return ServerResponse.createBySuccessMsg("用户信息更新失败");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null)
            return ServerResponse.createBySuccessMsg("当前用户不存在");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN)
            return ServerResponse.createBySuccess();
        else
            return ServerResponse.createByError();
    }
}
