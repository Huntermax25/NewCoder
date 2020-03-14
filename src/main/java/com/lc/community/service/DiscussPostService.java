package com.lc.community.service;

import com.lc.community.dao.DiscussPosMapper;
import com.lc.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPosMapper discussPosMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset,int limit){
        return discussPosMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPosMapper.selectDiscussPostRows(userId);
    }

}
