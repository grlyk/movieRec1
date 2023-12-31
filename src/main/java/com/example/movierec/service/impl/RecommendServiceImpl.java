package com.example.movierec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movierec.dto.MovieSimple;
import com.example.movierec.entity.Recommend;
import com.example.movierec.mapper.RecommendMapper;
import com.example.movierec.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RecommendMapper recommendMapper;
    /**
     * 分页获取电影列表
     *
     * @param pageNumber 页码
     * @param pageSize   分页大小
     * @param userId     用户id
     * @return 电影列表
     */
    @Override
    public IPage<MovieSimple> getRecommend(int pageNumber, int pageSize, Integer userId) {
        QueryWrapper<Recommend> recommendQueryWrapper = new QueryWrapper<>();
        recommendQueryWrapper.eq("user",userId)
                .orderBy(true,false,"date");
        IPage<MovieSimple> simpleIPage = new Page<>(pageNumber,pageSize);
        return recommendMapper.getRecommend(simpleIPage, recommendQueryWrapper);
    }
}
