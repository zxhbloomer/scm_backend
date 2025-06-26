package com.xinyirun.scm.bean.system.vo.sys.log.datachange;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SDataChangeLogFindOrderCodeVo implements Serializable {
    
    private static final long serialVersionUID = -7079865013623222628L;


    /**
     * mongodb：s_log_data_change_detail.id
     */
    private String s_log_data_change_detail_id;
}
