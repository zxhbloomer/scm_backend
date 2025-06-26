package com.xinyirun.scm.bean.system.vo.master.goods;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MBusinessTypeVo implements Serializable {


        private static final long serialVersionUID = 1021499829475973072L;

        /**
         * 主键
         */
        private Integer id;

        /**
         * 板块名
         */
        private String name;

        /**
         * 编号
         */
        private String code;

        /**
         * 是否启用
         */
        private Boolean enable;

        /**
         * 创建时间
         */
        private LocalDateTime c_time;

        /**
         * 修改时间
         */
        private LocalDateTime u_time;

        /**
         * 创建人ID
         */
        private Integer c_id;

        /**
         * 修改人ID
         */
        private Integer u_id;

        /**
         * 创建人姓名
         */
        private String c_name;

        /**
         * 修改人姓名
         */
        private String u_name;

        /**
         * 数据版本，乐观锁使用
         */
        private Integer dbversion;


        /**
         * 换页条件
         */
        private PageCondition pageCondition;

        /**
         * 子行业集合
         */
        private List<MIndustryVo> industryVo;

        private Integer[] ids;
}
