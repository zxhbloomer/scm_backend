package com.xinyirun.scm.core.system.mapper.business.so.arreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveSourceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveSourceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收款来源表 Mapper 接口
 */
@Mapper
public interface BArReceiveSourceMapper extends BaseMapper<BArReceiveSourceEntity> {

    /**
     * 根据ar_receive_id查询收款来源表
     */
    @Select("SELECT * FROM b_ar_receive_source WHERE ar_receive_id = #{arReceiveId}")
    List<BArReceiveSourceVo> selectByArReceiveId(@Param("arReceiveId") Integer arReceiveId);

    /**
     * 根据ar_receive_code查询收款来源表
     */
    @Select("SELECT * FROM b_ar_receive_source WHERE ar_receive_code = #{arReceiveCode}")
    List<BArReceiveSourceVo> selectByArReceiveCode(@Param("arReceiveCode") String arReceiveCode);

}