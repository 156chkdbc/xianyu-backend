package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.model.entity.ItemImages;
import com.tcosfish.xianyu.service.ItemImagesService;
import com.tcosfish.xianyu.mapper.ItemImagesMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【item_images(商品图片表：一行一图，含软删除)】的数据库操作Service实现
* @createDate 2025-09-13 11:23:19
*/
@Service
public class ItemImagesServiceImpl extends ServiceImpl<ItemImagesMapper, ItemImages>
    implements ItemImagesService{

}




