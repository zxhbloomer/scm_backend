package com.xinyirun.scm.core.app.mapper.master.enterpise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.master.enterprise.AppMEnterpriseVo;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseHisEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseHisVo;
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
public interface AppMEnterpriseHisMapper extends BaseMapper<MEnterpriseHisEntity> {


    /**
     * 根据客户id查询调整信息
     * @param id
     * @return
     */
    @Select("select * from m_enterprise_his where enterprise_id = #{p1} order by c_time desc")
    List<MEnterpriseHisVo> selectEnterpriseId(@Param("p1") Integer id);


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("select id,JSON_UNQUOTE(JSON_EXTRACT(adjust_info_json, '$.name')) as name                                                "
            +"        ,JSON_UNQUOTE(JSON_EXTRACT(adjust_info_json, '$.credit_no')) as credit_no                                      "
            +"        , c_time                                                                                                       "
            +"    from m_enterprise_his where enterprise_id = #{p1.id} order by c_time desc                                      ")
    IPage<AppMEnterpriseVo> selectPage(Page page, @Param("p1") AppMEnterpriseVo searchCondition);
}
