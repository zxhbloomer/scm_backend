package com.xinyirun.scm.bean.system.vo.master.user;


// import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 页面按钮vo
 * </p>
 *
 * @author zxh
 * @since 2019-11-01
 */
@Data
public class MPositionInfoVo implements Serializable {

    private static final long serialVersionUID = 5537852540009672665L;

    private Long position_id;

    private Long staff_id;

    private String position_name;

    private String position_simple_name;

}
