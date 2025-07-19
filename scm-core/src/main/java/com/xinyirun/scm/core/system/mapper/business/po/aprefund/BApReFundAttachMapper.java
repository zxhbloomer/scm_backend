package com.xinyirun.scm.core.system.mapper.business.po.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.aprefund.BApReFundAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应付退款附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Repository
public interface BApReFundAttachMapper extends BaseMapper<BApReFundAttachEntity> {

    /**
     * 根据应付退款ID查询附件信息
     */
    @Select("SELECT * FROM b_ap_refund_attach WHERE ap_refund_id = #{apId}")
    BApReFundAttachVo selectByApId(@Param("apId") Integer apId);

    /**
     * 根据应付退款ID查询附件列表
     */
    @Select("SELECT * FROM b_ap_refund_attach WHERE ap_refund_id = #{apId}")
    List<BApReFundAttachVo> selectListByApId(@Param("apId") Integer apId);

    /**
     * 根据文件ID查询附件信息
     */
    @Select("SELECT * FROM b_ap_refund_attach WHERE one_file = #{fileId}")
    BApReFundAttachVo selectByFileId(@Param("fileId") Integer fileId);

}