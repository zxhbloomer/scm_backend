package com.xinyirun.scm.core.system.service.business.po.aprefund;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.aprefund.BApReFundAttachEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundAttachVo;

import java.util.List;

/**
 * <p>
 * 应付退款附件表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
public interface IBApReFundAttachService extends IService<BApReFundAttachEntity> {

    /**
     * 新增附件
     */
    InsertResultAo<BApReFundAttachVo> insert(BApReFundAttachVo vo);

    /**
     * 更新附件
     */
    UpdateResultAo<BApReFundAttachVo> update(BApReFundAttachVo vo);

    /**
     * 删除附件
     */
    DeleteResultAo<Integer> delete(BApReFundAttachVo vo);

    /**
     * 根据应付退款ID查询附件信息
     */
    BApReFundAttachVo selectByApId(Integer apId);

    /**
     * 根据应付退款ID查询附件列表
     */
    List<BApReFundAttachVo> selectListByApId(Integer apId);

    /**
     * 根据文件ID查询附件信息
     */
    BApReFundAttachVo selectByFileId(Integer fileId);

}