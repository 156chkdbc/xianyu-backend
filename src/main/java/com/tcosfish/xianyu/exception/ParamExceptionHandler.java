package com.tcosfish.xianyu.exception;

import com.tcosfish.xianyu.model.base.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tcosfish
 * @apiNote 拦截错误类, 统一封装
 */
@Slf4j
@RestControllerAdvice // 全局异常处理器
public class ParamExceptionHandler {

  // 方法参数无效异常
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );
    return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
  }

  // 违反约束异常
  @ExceptionHandler(ConstraintViolationException.class)
  public ApiResponse<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(violation ->
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
    );
    return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
  }

  // MyBatis相关错误, 面向开发
  @ExceptionHandler(MyBatisSystemException.class)
  public ApiResponse<String> handleMyBatisException(MyBatisSystemException exception) {
    // TODO 添加上对应的错误日志
    log.error("MyBatis Error", exception);
    return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "MyBatis Error", exception.getCause().getMessage());
  }

  // 手动抛出的事务异常
  @ExceptionHandler(BizException.class)
  public ApiResponse<String> handleBiz(BizException e) {
    // 统一返回 JSON，HTTP 状态仍 200，由 success 标记失败
    return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Transactional Error", e.getMessage());
  }

  // 其他错误
  @ExceptionHandler(Exception.class)
  public ApiResponse<String> handleException(Exception ex) {
    return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage());
  }
}
