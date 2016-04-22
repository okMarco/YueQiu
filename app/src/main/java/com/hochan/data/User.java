package com.hochan.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/4/11.
 */
public class User {
    /** 用户UID（int64） */
    public String id;
    /** 字符串型的用户 UID */
    public String idstr;
    /** 用户昵称 */
    public String screen_name;
    /** 用户的最近一条微博信息字段 */
    public Status status;
    /** 是否允许所有人对我的微博进行评论，true：是，false：否 */
    public boolean allow_all_comment;
    /** 用户大头像地址 */
    public String avatar_large;
    /** 用户高清大头像地址 */
    public String avatar_hd;
    /** 认证原因 */
    public String verified_reason;
    /** 该用户是否关注当前登录用户，true：是，false：否 */
    public boolean follow_me;

    public static User parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return User.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }

        User user = new User();
        user.id                 = jsonObject.optString("id", "");
        user.idstr              = jsonObject.optString("idstr", "");
        user.screen_name        = jsonObject.optString("screen_name", "");
        user.allow_all_comment  = jsonObject.optBoolean("allow_all_comment", true);
        user.avatar_large       = jsonObject.optString("avatar_large", "");
        user.avatar_hd          = jsonObject.optString("avatar_hd", "");
        user.verified_reason    = jsonObject.optString("verified_reason", "");
        user.follow_me          = jsonObject.optBoolean("follow_me", false);
        return user;
    }
}
