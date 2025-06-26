package com.xinyirun.scm.bean.bpm.enums;

import com.xinyirun.scm.bean.bpm.vo.props.*;

/**
 * @author : willian fu
 * @date : 2022/7/6
 */
public enum NodeTypeEnum {

    ROOT {
        @Override
        public Class<?> getTypeClass() {
            return RootProps.class;
        }
    },

    APPROVAL{
        @Override
        public Class<?> getTypeClass() {
            return ApprovalProps.class;
        }
    },

    CC{
        @Override
        public Class<?> getTypeClass() {
            return CcProps.class;
        }
    },

    CONDITIONS{
        @Override
        public Class<?> getTypeClass() {
            return Object.class;
        }
    },

    CONCURRENTS{
        @Override
        public Class<?> getTypeClass() {
            return Object.class;
        }
    },

    CONDITION{
        @Override
        public Class<?> getTypeClass() {
            return ConditionProps.class;
        }
    },

    CONCURRENT{
        @Override
        public Class<?> getTypeClass() {
            return Object.class;
        }
    },

    DELAY{
        @Override
        public Class<?> getTypeClass() {
            return DelayProps.class;
        }
    },

    TRIGGER{
        @Override
        public Class<?> getTypeClass() {
            return TriggerProps.class;
        }
    },

    EMPTY{
        @Override
        public Class<?> getTypeClass() {
            return Object.class;
        }
    },

    TASK{
        @Override
        public Class<?> getTypeClass() {
            return ApprovalProps.class;
        }
    };

    public abstract Class<?> getTypeClass();
}
