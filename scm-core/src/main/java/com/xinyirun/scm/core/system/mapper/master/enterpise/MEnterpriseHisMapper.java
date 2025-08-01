package com.xinyirun.scm.core.system.mapper.master.enterpise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseHisEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseHisVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
@Repository
public interface MEnterpriseHisMapper extends BaseMapper<MEnterpriseHisEntity> {


    /**
     * 根据客户id查询调整信息
     * @param id
     * @return
     */
    @Select("""
            select * from m_enterprise_his where enterprise_id = #{p1} order by c_time desc
            """)
    List<MEnterpriseHisVo> selectEnterpriseId(@Param("p1") Integer id);


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
            SELECT
            	t1.id,
            	t1.uscc,
            	t1.enterprise_id,
            	t1.version,
            	t1.modify_reason,
            	t1.enterprise_name,
            	JSON_UNQUOTE(JSON_EXTRACT(t1.adjust_info_json, '$.bpm_instance_code')) AS bpm_instance_code,
            	t1.c_time
            FROM
            	m_enterprise_his t1
            JOIN
                m_enterprise t2 ON t1.enterprise_id = t2.id
            WHERE
                t1.enterprise_id = #{p1.id}
            AND t1.version < t2.version
            ORDER BY
            	t1.c_time DESC
            """)
    List<MEnterpriseHisVo> getAdjustList( @Param("p1") MEnterpriseHisVo searchCondition);
}
