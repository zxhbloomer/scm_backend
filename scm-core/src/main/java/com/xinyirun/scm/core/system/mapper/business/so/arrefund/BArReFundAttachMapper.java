package com.xinyirun.scm.core.system.mapper.business.so.arrefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应收退款附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Repository
public interface BArReFundAttachMapper extends BaseMapper<BArReFundAttachEntity> {

    /**
     * 根据应收退款ID查询附件信息
     */
    @Select("SELECT * FROM b_ar_refund_attach WHERE ar_refund_id = #{arId}")
    BArReFundAttachVo selectByArId(@Param("arId") Integer arId);

    /**
     * 根据应收退款ID查询附件列表
     */
    @Select("SELECT * FROM b_ar_refund_attach WHERE ar_refund_id = #{arId}")
    List<BArReFundAttachVo> selectListByArId(@Param("arId") Integer arId);

    /**
     * 根据文件ID查询附件信息
     */
    @Select("SELECT * FROM b_ar_refund_attach WHERE one_file = #{fileId}")
    BArReFundAttachVo selectByFileId(@Param("fileId") Integer fileId);

}