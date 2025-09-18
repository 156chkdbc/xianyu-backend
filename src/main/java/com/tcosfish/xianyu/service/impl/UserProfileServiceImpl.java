package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.model.entity.UserProfile;
import com.tcosfish.xianyu.service.UserProfileService;
import com.tcosfish.xianyu.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_profile(用户-冷数据)】的数据库操作Service实现
* @createDate 2025-09-08 15:04:08
*/
@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile>
    implements UserProfileService{

}




