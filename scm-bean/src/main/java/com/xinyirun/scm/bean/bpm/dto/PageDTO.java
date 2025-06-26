package com.xinyirun.scm.bean.bpm.dto;

import lombok.Data;

/**
 * @author LoveMyOrange
 * @create 2022-10-15 10:33
 */
@Data
//@ApiModel("分页DTO")
public class PageDTO {
//    @ApiModelProperty(value = "第几页",required = true)
    private Integer pageNo;
//    @ApiModelProperty(value = "多少条",required = true)
    private Integer pageSize;
}
