package com.tcosfish.xianyu.model.base;

import com.tcosfish.xianyu.model.vo.user.TokenVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tcosfish
 * @apiNote TokenApiResponse类是ApiResponse的一个子类，用于处理包含Token的API响应
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenApiResponse<T> extends ApiResponse<T> {
  private String token;
  private TokenVO tokenVO;

  public TokenApiResponse(Integer code, String msg, T data, String token) {
    super(code, msg, data);
    this.token = token;
  }
  public TokenApiResponse(Integer code, String msg, T data, TokenVO tokenVO) {
    super(code, msg, data);
    this.tokenVO = tokenVO;
  }

}
