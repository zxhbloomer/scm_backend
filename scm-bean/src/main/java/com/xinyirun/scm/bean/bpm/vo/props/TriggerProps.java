package com.xinyirun.scm.bean.bpm.vo.props;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2022/7/7
 */
@Data
public class TriggerProps implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;
    private TriggerTypeEnum type;
    private Http http;
    private Email email;

    public enum TriggerTypeEnum {
        WEBHOOK, EMAIL;
    }

    @Data
    public static class Http implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private String method;
        private String url;
        private String contentType;
        private List<Variable> headers;
        private List<Variable> params;
        private Boolean handlerByScript;
        private String success;
        private String fail;

        @Data
        public static class Variable implements Serializable {
            private static final long serialVersionUID = -45475579271153023L;
            private String name;
            private Boolean isField;
            private Object value;
        }
    }

    @Data
    public static class Email implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private String subject;
        private List<String> to;
        private String content;
    }

}
