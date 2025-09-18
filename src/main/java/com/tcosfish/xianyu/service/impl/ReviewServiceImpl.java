package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.model.entity.Review;
import com.tcosfish.xianyu.service.ReviewService;
import com.tcosfish.xianyu.mapper.ReviewMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【review(评价)】的数据库操作Service实现
* @createDate 2025-09-16 20:23:08
*/
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review>
    implements ReviewService{

}




