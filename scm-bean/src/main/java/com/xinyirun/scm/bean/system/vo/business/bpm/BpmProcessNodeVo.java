package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.bpm.enums.NodeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2022/7/6
 */
@Data
public class BpmProcessNodeVo<T> implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;

    private String id;

    private String parentId;

    private NodeTypeEnum type;

    private String name;

    private T props;

    private NodeTypeEnum parentType;

    private BpmProcessNodeVo<?> children;

    private List<BpmProcessNodeVo<?>> branchs;
}
