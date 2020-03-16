package com.lc.community.dao;

import com.lc.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    /**
     * 插入,返回插入的行数
     * 以id自动增加主键
     */
    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 以ticket为条件查询
     * @param ticket
     * @return
     */
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 修改状态，相当于删除
     * 如果要写动态sql，前后要套上<script></script>标签
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\">",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
