package com.xinyirun.scm.bean.bpm.vo.props;


import com.xinyirun.scm.bean.bpm.vo.form.FormPerm;
import com.xinyirun.scm.bean.system.vo.business.bpm.BpmOperationPermVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2022/7/7
 */
@Data
public class RootProps implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;

    private List<OrgUserVo> assignedUser;
    private List<FormPerm> formPerms;
    private BpmOperationPermVo operationPerm;
}
