package com.xinyirun.scm.bean.bpm.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2020/9/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateGroupVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -215956160841338966L;
    private Integer id;

    private String name;

    private List<Template> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Template{

        private String formId;

        private Integer tgId;

        private String formName;

        private String icon;

        private Boolean isStop;

        private String remark;
        private JSONObject logo;

        private String background;

        private String updated;
        private String templateId;
    }


}
