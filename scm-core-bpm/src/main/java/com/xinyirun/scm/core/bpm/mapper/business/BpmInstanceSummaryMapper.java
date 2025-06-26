package com.xinyirun.scm.core.bpm.mapper.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.bpm.vo.BpmCcVo;
import com.xinyirun.scm.bean.bpm.vo.BpmInstanceSummaryVo;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 审批流实例-摘要 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-20
 */
@Repository
public interface BpmInstanceSummaryMapper extends BaseMapper<BpmInstanceSummaryEntity> {


    /**
     * 根据编号获取数据
     * @param instanceCode
     * @return
     */
    @Select( "  select * from bpm_instance_summary  where true               "
            +"     and process_code = #{p1}              "
            +"     ")
    BpmInstanceSummaryVo getDataByInstanceCode(String instanceCode);

}
