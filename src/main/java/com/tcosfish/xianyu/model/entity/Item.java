package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tcosfish.xianyu.model.enums.ProductItemType;
import com.tcosfish.xianyu.model.vo.product.ItemStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;
import lombok.Data;

/**
* 统一商品/服务实体
* @author tcosfish
* @TableName item
* 实现 Serializable 接口就是让 Java 知道这个类的对象可以被“打成字节流”，从而支持存储、传输、克隆等操作。
*/
@Data
public class Item implements Serializable {

    /**
    * 主键
    */
    @NotNull(message="[主键]不能为空")
    @Schema(description="主键")
    private Long id;
    /**
    * 发布者（卖家/服务者）
    */
    @NotNull(message="[发布者（卖家/服务者）]不能为空")
    @Schema(description="发布者（卖家/服务者）")
    private Long sellerId;
    /**
    * 类目ID
    */
    @NotNull(message="[类目ID]不能为空")
    @Schema(description="类目ID")
    private Long categoryId;
    /**
    * 标题
    */
    @NotBlank(message="[标题]不能为空")
    @Size(max= 200,message="编码长度不能超过200")
    @Schema(description="标题")
    @Length(max= 200,message="编码长度不能超过200")
    private String title;
    /**
    * 描述
    */
    @Size(max= 0,message="编码长度不能超过-1")
    @Schema(description="描述")
    @Length(max= 0,message="编码长度不能超过-1")
    private String description;
    /**
    * ["教材","考研"]
    */
    @Schema(description="['教材','考研']")
    private Object tags;
    /**
    * [10001,10002]
    */
    @Schema(description="[10001,10002]")
    private Object favorUsers;
    /**
    * 单价
    */
    @NotNull(message="[单价]不能为空")
    @Schema(description="单价")
    private BigDecimal price;
    /**
    * 单位：piece|hour|day
    */
    @Size(max= 20,message="编码长度不能超过20")
    @Schema(description="单位：piece|hour|day")
    @Length(max= 20,message="编码长度不能超过20")
    private String unit;
    /**
    * 库存/可预约次数
    */
    @Schema(description="库存/可预约次数")
    private Integer stock;
    /**
    * 1可议价 0不可
    */
    @Schema(description="1可议价 0不可")
    private Integer negotiable;
    /**
    * 业务形态, 1 商品 2 服务
    */
    @NotNull(message="[业务形态, 1 商品 2 服务]不能为空")
    @Schema(description="业务形态, 1 商品 2 服务")
    private Integer itemType;
    /**
    * 1上架 2已售/已约 3下架
    */
    @Schema(description="1上架 2已售/已约 3下架")
    private Integer status;
    /**
    * 浏览量
    */
    @Schema(description="浏览量")
    private Integer viewCount;
    /**
    * 卖家设置, 买家可选的交付方式范围, 1 线下自取 2 宿舍送达 3 快递, ["1", "宿舍送达"]
    */
    @NotNull(message="[卖家设置, 买家可选的交付方式范围, 1 线下自取 2 宿舍送达 3 快递, [1, 宿舍送达]不能为空")
    @Schema(description="卖家设置, 买家可选的交付方式范围, 1 线下自取 2 宿舍送达 3 快递, [1, '宿舍送达]")
    private Object deliveryOptions;
    /**
    * 卖家设置的最低接受价（可选）可用于自动接受议价（如买家出价 ≥ 最低价，则自动成交）
    */
    @Schema(description="卖家设置的最低接受价（可选）可用于自动接受议价（如买家出价 ≥ 最低价，则自动成交）")
    private BigDecimal minPrice;
    /**
    * 最大议价轮次，默认3轮, 控制单商品最多允许多少轮议价，防止骚扰。
    */
    @Schema(description="最大议价轮次，默认3轮, 控制单商品最多允许多少轮议价，防止骚扰。")
    private Integer maxNegotiationRound;
    /**
    * 假删除
    */
    @Schema(description="假删除")
    @TableLogic
    private Integer deleted;
    /**
    * 创建时间
    */
    @Schema(description="创建时间")
    private Date createtime;
    /**
    * 更新时间
    */
    @Schema(description="更新时间")
    private Date updatetime;
}
