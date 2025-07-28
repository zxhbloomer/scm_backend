package com.xinyirun.scm.core.system.service.business.so.arrefund;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundAttachEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundAttachVo;

import java.util.List;

/**
 * <p>
 * 应收退款附件表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
public interface IBArReFundAttachService extends IService<BArReFundAttachEntity> {

    /**
     * 新增附件
     */
    InsertResultAo<BArReFundAttachVo> insert(BArReFundAttachVo vo);

    /**
     * 更新附件
     */
    UpdateResultAo<BArReFundAttachVo> update(BArReFundAttachVo vo);

    /**
     * 删除附件
     */
    DeleteResultAo<Integer> delete(BArReFundAttachVo vo);

    /**
     * 根据应收退款ID查询附件信息
     */
    BArReFundAttachVo selectByArId(Integer arId);

    /**
     * 根据应收退款ID查询附件列表
     */
    List<BArReFundAttachVo> selectListByArId(Integer arId);

    /**
     * 根据文件ID查询附件信息
     */
    BArReFundAttachVo selectByFileId(Integer fileId);

}