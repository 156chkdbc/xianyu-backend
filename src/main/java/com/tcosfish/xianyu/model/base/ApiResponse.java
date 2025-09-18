package com.tcosfish.xianyu.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tcosfish
 * @apiNote 统一的数据响应类
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {
  private int code;
  private String message;
  private T data;

  public ApiResponse(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  // 成功响应, 无数据
  public static ApiResponse<EmptyVO> success() {
    return success(new EmptyVO());
  }

  // 成功响应, 带消息
  public static <T> ApiResponse<T> success(String message) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(200);
    response.setMessage(message);
    response.setData(null);
    return response;
  }

  // 成功响应, 带数据
  public static <T> ApiResponse<T> success(T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(200);
    response.setMessage("success");
    response.setData(data);
    return response;
  }

  // 成功响应, 带消息, 数据
  public static <T> ApiResponse<T> success(String message, T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(200);
    response.setMessage(message);
    response.setData(data);
    return response;
  }

  // 错误响应, 无数据
  public static <T> ApiResponse<T> error(int code, String message) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(code);
    response.setMessage(message);
    return response;
  }

  // 错误响应, 带数据
  public static <T> ApiResponse<T> error(int code, String message, T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setCode(code);
    response.setMessage(message);
    response.setData(data);
    return response;
  }
}
