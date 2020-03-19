package com.lc.community.service;

import com.lc.community.dao.LoginTicketMapper;
import com.lc.community.dao.UserMapper;
import com.lc.community.entity.LoginTicket;
import com.lc.community.entity.User;
import com.lc.community.util.CommunityConstant;
import com.lc.community.util.CommunityUtil;
import com.lc.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 通过userid查找
     * @param id
     * @return
     */
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    /**
     * 实现注册方法，返回封装内容,成功或失败的各种结果
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessage","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMessage","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMessage","邮箱不能为空！");
            return map;
        }

        // 判断账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已存在！");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已被注册！");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // 希望服务器怎么处理请求 http://localhost:8080/community/activation/{userid}/{activeCode}
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        // 返回map为空，表示没有问题
        return map;
    }

    /**
     * 激活账号
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEATE;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String,Object> login(String username,String password,int expiredSeconds) {
        Map<String,Object> map = new HashMap<>();

        // 空值的判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMessage","密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该账号不存在！");
            return map;
        }
        // 验证状态
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        // 登录成功,生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 退出
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    /**
     * 查询凭证
     */
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 更新用户头像的行数
     * @param userId
     * @return
     */
    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    /**
     * 更新密码
     * @param userId
     * @param password
     * @return
     */
    public int updatePassword(int userId,String password){
        User user = userMapper.selectById(userId);
        password = CommunityUtil.md5(password+user.getSalt());
        return userMapper.updatePassword(userId,password);
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }


}
