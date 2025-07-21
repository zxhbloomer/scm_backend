package com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BCargoRightTransferAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferAttachVo;

import java.util.List;

/**
 * 货权转移附件表 服务类接口
 *
 * @author system
 * @since 2025-01-19
 */
public interface IBCargoRightTransferAttachService extends IService<BCargoRightTransferAttachEntity> {

    /**
     * 根据货权转移主表ID查询附件列表
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 附件列表
     */
    List<BCargoRightTransferAttachVo> selectByCargoRightTransferId(Integer cargoRightTransferId);

    /**
     * 保存附件数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @param attachList           附件列表
     * @return 保存结果
     */
    boolean saveAttachments(Integer cargoRightTransferId, List<BCargoRightTransferAttachVo> attachList);

    /**
     * 根据文件ID查询附件信息
     *
     * @param fileId 文件ID
     * @return 附件信息
     */
    BCargoRightTransferAttachVo selectByFileId(Integer fileId);

    /**
     * 删除附件
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @param fileId               文件ID
     * @return 删除结果
     */
    boolean deleteAttachment(Integer cargoRightTransferId, Integer fileId);
}