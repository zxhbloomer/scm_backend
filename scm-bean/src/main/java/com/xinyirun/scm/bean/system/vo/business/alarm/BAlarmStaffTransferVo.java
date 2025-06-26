package com.xinyirun.scm.bean.system.vo.business.alarm;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MStaffVo
 * @Description: 员工bean，为穿梭框服务
 * @Author: zxh
 * @date: 2020/1/10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BAlarmStaffTransferVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = 4772234122217278052L;

    /** 穿梭框 key */
    private Long key;
    /** 穿梭框 label */
    private String label;

    /**
     * alarm_id
     */
    private Integer alarm_id;

    /** 岗位ID */
    private Integer group_id;

    /** 穿梭框已经选择的员工id */
    Integer[] staff_alarm;

}
