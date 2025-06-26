package com.xinyirun.scm.bean.api.vo.business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBAllAsyncVo<E> implements Serializable {

    private static final long serialVersionUID = 2532468659330765679L;

    private List<E> beans;

    String app_config_type;
}
