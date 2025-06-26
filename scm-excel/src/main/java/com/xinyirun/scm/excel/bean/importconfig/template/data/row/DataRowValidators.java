package com.xinyirun.scm.excel.bean.importconfig.template.data.row;

import com.alibaba.fastjson2.annotation.JSONField;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.ValidatorBean;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 行级check，一般在读取行 结束后，调用次check
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataRowValidators implements Serializable {

    private static final long serialVersionUID = 3781902558120203271L;

    /**
     * 行级check
     */
    @JSONField
    private List<ValidatorBean> listValidators;
}
