package com.xinyirun.scm.bean.system.vo.business.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/10/20 17:31
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppScheduleSendMqData implements Serializable {

    private static final long serialVersionUID = 8196175370124129225L;


    /**
     * 物流订单id
     */
    private Integer id;

    /**
     * 类型, out-save 出库, in-save 入库
     */
    private String type;

}
