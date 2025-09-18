package com.tcosfish.xianyu.utils;

import com.tcosfish.xianyu.model.dto.product.ProductDto;
import com.tcosfish.xianyu.model.vo.product.ItemStatusEnum;
import com.tcosfish.xianyu.model.vo.product.ProductVO;
import com.tcosfish.xianyu.model.vo.product.UnitEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author tcosfish
 * @apiNote 组装productVO数据
 */
public class ProductConvertUtil {

  private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * 如果 str 为 null 或 "" 则返回 defaultVal，否则返回 str
   */
  private static String blankToDefault(String str) {
    return str == null || str.trim().isEmpty() ? "piece" : str;
  }

  public static ProductVO toCard(ProductDto item) {
    ProductVO vo = new ProductVO();
    vo.setId(item.getId());
    vo.setTitle(item.getTitle());
    vo.setDescription(item.getDescription());

    // 1. 价格标签
    String unit = blankToDefault(item.getUnit());
    String unitName = UnitEnum.of(unit).getDesc(); // 自己维护一个单位字典
    vo.setPriceLabel(item.getPrice().stripTrailingZeros().toPlainString() + "元/" + unitName);

    // 2. 库存
    vo.setStock(item.getStock());

    // 3. 可议价
    vo.setNegotiableLabel(item.getNegotiable() == 1 ? "可议价" : "不可议价");

    // 4. 类型
    vo.setItemType(java.lang.String.valueOf(item.getItemType()));

    // 5. 状态
    vo.setStatusLabel(ItemStatusEnum.of(item.getStatus()).getDesc());

    // 6. 封面 & 浏览量
    vo.setViewCount(item.getViewCount());

    // 7. 时间
    vo.setCreateTime(LocalDateTime.ofInstant(item.getCreatetime().toInstant(), ZoneId.systemDefault())
      .format(DF));

    // 8. 卖家冗余
    vo.setSellerNick(item.getSellerNick());
    vo.setSellerAvatar(item.getSellerAvatar());

    return vo;
  }
}
