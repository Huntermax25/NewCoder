package com.lc.community.util;

import com.lc.community.entity.User;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象。线程隔离的
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }


}
