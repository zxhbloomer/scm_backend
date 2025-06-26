package com.xinyirun.scm.bean.bpm.vo;

//import com.xinyirun.scm.bean.entity.ProcessTemplates;
//import io.swagger.annotations.ApiModel;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author LoveMyOrange
 * @create 2022-10-15 16:09
 */
@Data
//@ApiModel("查看详情 需要返回给前端的VO")
public class ProcessInstanceDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1516811436897986509L;
    private BpmProcessTemplatesEntity processTemplates;
    private String formData;
    private String processInstanceId;
    private List<String> completeIdList;
    private List<String> finishIdList;
    private List<String> noneList;
    private Boolean signFlag;
}
