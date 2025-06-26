package com.xinyirun.scm.bean.app.vo.master.contact_list;

import com.xinyirun.scm.bean.app.config.base.AppBaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 员工岗位
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AppMStaffPositionsVo extends AppBaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4765730484073526274L;


    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 岗位id
     */
    private Long position_id;

    /**
     * 岗位名称
     */
    private String position_name;

    /**
     * 岗位简称
     */
    private String position_simple_name;

}
