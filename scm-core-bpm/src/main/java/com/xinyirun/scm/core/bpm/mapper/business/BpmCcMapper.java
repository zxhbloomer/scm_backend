package com.xinyirun.scm.core.bpm.mapper.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.bpm.vo.BpmCcVo;
import com.xinyirun.scm.bean.entity.bpm.BpmCcEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCcVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 抄送 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Repository
public interface BpmCcMapper extends BaseMapper<BpmCcEntity> {


    @Select( "  select * from bpm_cc  where true               "
            +"     and user_code = #{p1.user_code}              "
            +"     and (tenant_code = #{p1.tenant_code} or #{p1.tenant_code} is null  or  #{p1.tenant_code} ='' )")
    Page<BpmCcVo> selectPagesOne(Page param,@Param("p1") BBpmProcessVo bpmProcess);


    @Select("   SELECT                                                                                            "
            +"	t1.*,                                                                                          "
            +"	t2.process_code,                                                                               "
            +"	t2.process_definition_name as process_name,                                                    "
            +"	t4.user_name as owner_name,                                                                    "
            +"	t2.serial_id,                                                                                  "
            +"	t2.serial_type,                                                                                "
            +"	t3.label as status_name                                                                        "
            +" FROM                                                                                             "
            +"	bpm_cc t1                                                                                      "
            +"	LEFT JOIN bpm_instance t2 ON t1.process_instance_id = t2.process_instance_id                   "
            +"	LEFT JOIN s_dict_data t3 ON t3.CODE ='"+ DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS +"'    "
            +"	AND t2.`status` = t3.dict_value                                                                "
            +"     and t1.user_code = #{p1.user_code}                                                          "
            +"  LEFT JOIN bpm_users t4  ON t2.owner_code = t4.user_code                                                   "
//            +"     and (t1.tenant_code = #{p1.tenant_code} or #{p1.tenant_code} is null  or  #{p1.tenant_code} ='' )"
    )
    IPage<BBpmCcVo> selectPages(Page<BBpmCcVo> pageCondition, @Param("p1") BBpmCcVo bpmProcess);
}
