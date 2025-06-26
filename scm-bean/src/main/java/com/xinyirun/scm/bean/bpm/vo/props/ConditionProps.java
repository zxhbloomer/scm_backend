package com.xinyirun.scm.bean.bpm.vo.props;

import com.xinyirun.scm.bean.bpm.enums.ConditionModeEnum;
import com.xinyirun.scm.bean.bpm.vo.form.ValueType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2022/7/7
 */
@Data
public class ConditionProps implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;
    //条件模式
    private ConditionModeEnum mode;
    //条件组类型
    private String groupsType;
    //条件组
    private List<Group> groups;
    //条件表达式
    private String expression;

    @Data
    public static class Group implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private String groupType;
        private List<Condition> conditions;
        private List<String> cids;

        @Data
        public static class Condition implements Serializable {
            private static final long serialVersionUID = -45475579271153023L;
            private String compare;
            private String id;
            private ValueType valueType;
            private List<Object> value;
        }
    }
}
