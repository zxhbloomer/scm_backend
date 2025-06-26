package com.xinyirun.scm.bean.system.vo.business.wo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *  生产管理 表
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BWoExportUtilVo implements Serializable {

    private static final long serialVersionUID = 2110953230703545621L;

    private String code;

    private String key;


}
