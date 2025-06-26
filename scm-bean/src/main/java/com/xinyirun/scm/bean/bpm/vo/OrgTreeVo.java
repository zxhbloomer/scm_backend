package com.xinyirun.scm.bean.bpm.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : willian fu
 * @version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgTreeVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 4864195865088420828L;
    private Long id;

    private String name;

    private String type;

    private String avatar;

    private Boolean sex;

    private Boolean selected;
}
