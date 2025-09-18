package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.enums.NegotiationStatus;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * @author tcosfish
 */
@Component
public class NegotiateConvertHelper {
  @Named("enumToInteger")
  public Integer enumToInteger(NegotiationStatus status) {
    return status == null ? null : status.getCode();
  }

  // 添加这个方法解决映射问题
  public NegotiationStatus map(NegotiationStatus value) {
    return value;
  }
}
