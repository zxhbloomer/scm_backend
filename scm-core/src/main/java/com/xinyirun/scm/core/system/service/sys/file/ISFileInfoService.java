package com.xinyirun.scm.core.system.service.sys.file;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.file.BackupFileVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import java.util.List;

/**
 * <p>
 * 附件详情 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ISFileInfoService extends IService<SFileInfoEntity> {

    /**
     * 获取列表，页面查询
     */
    List<SFileInfoVo> selectList(SFileInfoVo searchCondition) ;

    /**
     * 插入记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(List<SFileInfoVo> vo);

    /**
     * 修改记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    UpdateResultAo<Integer> save(List<SFileInfoVo> vo);

    /**
     * 修改记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    UpdateResultAo<Integer> save(SFileInfoVo vo);

    /**
     * 文件备份
     */
    void backup(Integer backup_now_count);

    /**
     * 批量物理删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<Integer> realDeleteByIdsIn(List<SFileInfoVo> searchCondition);

    SFileInfoVo selectById(int id);
}
