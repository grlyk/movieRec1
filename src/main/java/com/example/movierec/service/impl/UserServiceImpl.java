package com.example.movierec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.movierec.entity.User;
import com.example.movierec.mapper.UserMapper;
import com.example.movierec.service.UserService;
import com.example.movierec.util.JwtUtil;
import com.example.movierec.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;


    /**
     * 登录功能
     * @param account 账号
     * @param password 密码
     * @return jwt
     */
    @Override
    public String login(String account, String password) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user.getPassword().equals(password)) {
            String key = "user:"+user.getId();
            redisCache.setCacheObject(key,user,7, TimeUnit.DAYS);
            return JwtUtil.createToken(user);
        }
        return null;
    }

    /**
     * 用户退出登录功能
     * @param userId 用户id
     * @return 退出结果
     */
    @Override
    public Boolean logout(Integer userId) {
        String key = "user:"+userId;
        return redisCache.deleteObject(key);
    }

    @Override
    public boolean userExists(String username) {
        return userMapper.checkUsernameExists(username);
    }
    @Override
    public void saveUser(User user) {
        if (!userExists(user.getName())) {
            userMapper.insertUserFull(user);
        } else {
            throw new RuntimeException("用户名已存在");
        }
    }

    @Override
    public User findById(Integer id) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .select("id","name", "account", "sex")
                .eq("id", id);
        User user = userMapper.selectOne(userQueryWrapper);
        return user;
    }

    @Override
    public void changeInfoById(Integer id, String name, String account, Boolean sex) {
        UpdateWrapper<User> userUpdateWrapper=new UpdateWrapper<>();
        userUpdateWrapper
                .set("name",name)
                .set("account",account)
                .set("sex",sex)
                .eq("id",id);
        userMapper.update(null, userUpdateWrapper);
    }
//    @Override
//    public void updatePassword(User user) {
//        // 检查用户是否存在
//        if (!userExists(user.getUsername())) {
//            throw new RuntimeException("用户不存在");
//        }
//        // 检查新密码是否为空
//        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
//            throw new IllegalArgumentException("新密码不能为空");
//        }
//        // 调用UserMapper的方法更新密码
//        userMapper.updateUserPassword(user);
//    }
}
