package com.xinyirun.scm.bean.bpm.vo;

import com.xinyirun.scm.bean.bpm.enums.NodeTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2022/7/6
 */
@Data
public class ProcessNode<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2798960154179398131L;
    private String id;

    private String parentId;

    private NodeTypeEnum type;

    private String name;

    private T props;

    private NodeTypeEnum parentType;

    private ProcessNode<?> children;

    private List<ProcessNode<?>> branchs;
}
