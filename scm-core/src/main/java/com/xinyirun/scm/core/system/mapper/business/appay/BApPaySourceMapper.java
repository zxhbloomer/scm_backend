package com.xinyirun.scm.core.system.mapper.business.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.appay.BApPaySourceEntity;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPaySourceVo;
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
    @Select("SELECT * FROM b_ap_pay_source WHERE ap_pay_id = #{apPayId}")
    List<BApPaySourceVo> selectByApPayId(@Param("apPayId") Integer apPayId);

    /**
     * 根据ap_pay_code查询付款来源表
     */
    @Select("SELECT * FROM b_ap_pay_source WHERE ap_pay_code = #{apPayCode}")
    List<BApPaySourceVo> selectByApPayCode(@Param("apPayCode") String apPayCode);

} 