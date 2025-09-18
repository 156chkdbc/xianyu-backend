package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.dto.product.CreateNegotiateParam;
import com.tcosfish.xianyu.model.entity.Negotiation;
import com.tcosfish.xianyu.model.vo.product.CreateNegotiateVO;
import com.tcosfish.xianyu.model.vo.product.ProductNegotiateVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author tcosfish
 */
@Mapper(componentModel = "spring", uses = NegotiateConvertHelper.class)
public interface NegotiateConverter {

  /* ===== Param -> Entity ===== */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "PENDING") // 数据库默认值
  @Mapping(target = "round", constant = "1")
  @Mapping(target = "deleted", constant = "0")
  @Mapping(target = "itemId", ignore = true)
  @Mapping(target = "createtime", ignore = true)
  @Mapping(target = "updatetime", ignore = true)
  @Mapping(target = "expireAt", ignore = true)
  @Mapping(target = "buyerId", ignore = true)
  @Mapping(target = "sellerId", ignore = true)
  Negotiation toEntity(CreateNegotiateParam param);

  /* ===== Entity -> VO ===== */
  @Mapping(target = "negotiationId", source = "id")
  @Mapping(target = "status", source = "status", qualifiedByName = "enumToInteger")
  CreateNegotiateVO toVo(Negotiation entity);

  /* ===== Entity -> VO ===== */
  @Mapping(target = "negotiationId", source = "id")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "buyerNick", ignore = true)
  @Mapping(target = "createTime", source = "createtime")
  ProductNegotiateVO toProductNegotiateVO(Negotiation entity);
}
