package com.xinyirun.scm.core.system.mapper.business.po.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.appay.BApPaySourceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPaySourceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 付款来源表 Mapper 接口
 */
@Mapper
public interface BApPaySourceMapper extends BaseMapper<BApPaySourceEntity> {

    /**
     * 根据ap_pay_id查询付款来源表
     */
    @Select("""
            -- 根据付款单主表ID查询付款来源信息
            SELECT * FROM b_ap_pay_source 
            -- apPayId: 付款单主表ID参数
            WHERE ap_pay_id = #{apPayId}
            """)
    List<BApPaySourceVo> selectByApPayId(@Param("apPayId") Integer apPayId);

    /**
     * 根据ap_pay_code查询付款来源表
     */
    @Select("""
            -- 根据付款单编号查询付款来源信息
            SELECT * FROM b_ap_pay_source 
            -- apPayCode: 付款单编号参数
            WHERE ap_pay_code = #{apPayCode}
            """)
    List<BApPaySourceVo> selectByApPayCode(@Param("apPayCode") String apPayCode);

} 