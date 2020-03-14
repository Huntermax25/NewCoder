package com.lc.community.dao;

import com.lc.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPosMapper {
    /**
     * 实现分页查询的功能
     * @param userId 在个人主页里我的帖子需要userId
     * @param offset 其实行号
     * @param limit 一页的个数
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询帖子总页数
     * @Param 用于给参数取别名，如果只有一个参数，并且在<if>里使用，则必须加别名.
     * 那就是动态 SQL ，如果在动态 SQL 中使用了参数作为变量，那么也需要 @Param 注解，即使你只有一个参数。
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);




}
