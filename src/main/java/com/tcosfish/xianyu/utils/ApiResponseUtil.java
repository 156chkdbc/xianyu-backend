package com.tcosfish.xianyu.utils;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.Pagination;
import com.tcosfish.xianyu.model.base.PaginationApiResponse;
import com.tcosfish.xianyu.model.base.TokenApiResponse;
import com.tcosfish.xianyu.model.vo.user.TokenVO;
import org.springframework.http.HttpStatus;

/**
 * @author tcosfish
 * @apiNote 响应工具类, 减少多余状态码重复
 */
public class ApiResponseUtil {
  /**
   * 构建成功的响应
   *
   * @param message 响应消息
   * @return ApiResponse
   */
  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.success(message);
  }

  // success的 message不被改变? 只是为了后端端口格式统一!?
  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.success(message, data);
  }

  // 只有错误时, 才需要改变相应的 message信息
  /**
   * 构建参数错误的响应
   */
  public static <T> ApiResponse<T> error(String msg) {
    return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), msg);
  }

  /**
   * 构建 TokenApiResponse, token 相关
   */
  public static <T> TokenApiResponse<T> success(String msg, T data, String token) {
    return new TokenApiResponse<>(HttpStatus.OK.value(), msg, data, token);
  }

  /**
   * 构建 TokenApiResponse, token拓展, 存储 tokenVO
   */
  public static <T> TokenApiResponse<T> success(String msg, T data, TokenVO tokenVO) {
    return new TokenApiResponse<>(HttpStatus.OK.value(), msg, data, tokenVO);
  }

  /**
   * 构建 PaginationApiResponse, 分页相关
   */
  public static <T> PaginationApiResponse<T> success(String msg, T data, Pagination pagination) {
    return new PaginationApiResponse<>(HttpStatus.OK.value(), msg, data, pagination);
  }
}
