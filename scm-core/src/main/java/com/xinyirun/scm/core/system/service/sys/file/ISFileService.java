package com.xinyirun.scm.core.system.service.sys.file;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileVo;

import java.util.List;

/**
 * <p>
 * 附件信息 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ISFileService extends IService<SFileEntity> {

    /**
     * 获取列表，页面查询
     */
    List<SFileVo> selectList(SFileVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(SFileVo vo);

    /**
     * 批量物理删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<Integer> realDeleteByIdsIn(List<SFileVo> searchCondition);

    SFileVo selectById(int id);

    /**
     * 获取附件信息
     */
    List<SFileInfoVo> selectFileInfo(Integer id);

    /**
     * 根据业务类型和业务ID查询文件信息
     *
     * @param serialType 业务类型（如 "ai_knowledge_base_item"）
     * @param serialId 业务ID（如 知识项ID）
     * @return 文件信息列表
     */
    List<SFileInfoVo> selectFileInfoBySerialTypeAndId(String serialType, Integer serialId);
}
