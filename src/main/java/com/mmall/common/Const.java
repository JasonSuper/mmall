package com.mmall.common;

/**
 * @Author Jason
 * Create in 2018-06-04 7:07
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface Role {
        int ROLE_CUSTOMER = 0; // 普通用户
        int ROLE_ADMIN = 1; // 管理员
    }
}
