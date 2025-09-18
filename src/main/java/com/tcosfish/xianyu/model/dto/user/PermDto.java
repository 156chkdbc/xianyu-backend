package com.tcosfish.xianyu.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author tcosfish
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermDto implements Serializable {
  private String code;
  private String description;
}
