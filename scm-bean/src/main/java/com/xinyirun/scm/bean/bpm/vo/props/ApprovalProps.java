package com.xinyirun.scm.bean.bpm.vo.props;

import com.xinyirun.scm.bean.bpm.enums.ApprovalModeEnum;
import com.xinyirun.scm.bean.bpm.enums.ApprovalTypeEnum;
import com.xinyirun.scm.bean.bpm.vo.form.FormPerm;
import com.xinyirun.scm.bean.system.vo.business.bpm.BpmOperationPermVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : willian fu
 * @date : 2022/7/6
 */
@Data
public class ApprovalProps implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;

    private ApprovalTypeEnum assignedType;

    private ApprovalModeEnum mode;

    private boolean sign;

    private Nobody nobody;

    private TimeLimit timeLimit;

    private List<OrgUserVo> assignedUser;

    private List<OrgUserVo> assignedDept;

    private SelfSelect selfSelect;

    private LeaderTop leaderTop;

    private Leader leader;

    private List<OrgUserVo> role;

    private String formUser;

    private String formDept;

    private Refuse refuse;

    private List<FormPerm> formPerms;

    private BpmOperationPermVo operationPerm;

    @Data
    public static class Nobody implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private NobodyHandlerTypeEnum handler;
        private List<OrgUserVo> assignedUser;
    }

    public enum NobodyHandlerTypeEnum {
        TO_PASS, TO_REFUSE, TO_ADMIN, TO_USER
    }

    @Data
    public static class TimeLimit implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private TimeOut timeout;
        private Handler handler;

        @Data
        public static class TimeOut implements Serializable {
            private static final long serialVersionUID = -45475579271153023L;
            private String unit;
            private Integer value;
        }

        @Data
        public static class Handler implements Serializable {
            private static final long serialVersionUID = -45475579271153023L;
            private HandlerType type;
            private Notify notify;

            public enum HandlerType{
                PASS, REFUSE, NOTIFY
            }

            @Data
            public static class Notify implements Serializable {
                private static final long serialVersionUID = -45475579271153023L;
                private boolean once;
                private Integer hour;
            }
        }
    }

    @Data
    public static class SelfSelect implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private boolean multiple;
    }

    @Data
    public static class LeaderTop implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private String endCondition;
        private Integer endLevel;
        private Boolean skipEmpty = false;
    }

    @Data
    public static class Leader implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private Integer level;
        private Boolean skipEmpty = true;
    }

    @Data
    public static class Role implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private String id;
        private String name;
    }

    @Data
    public static class Refuse implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        private String type;
        private String target;
    }
}
