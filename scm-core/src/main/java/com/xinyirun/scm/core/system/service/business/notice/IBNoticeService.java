package com.xinyirun.scm.core.system.service.business.notice;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.notice.BNoticeEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;

import java.util.List;

/**
 * <p>
 * 通知表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
public interface IBNoticeService extends IService<BNoticeEntity> {

    /**
     * 列表查询
     * @param param
     * @return
     */
    IPage<BNoticeVo> selectPageList(BNoticeVo param);

    /**
     * 列表查询
     * @param param
     * @return
     */
    List<BNoticeVo> getNoticeUnreadTen(BNoticeVo param);

    /**
     * 新增
     * @param param
     * @return
     */
    InsertResultAo<BNoticeVo> insert(BNoticeVo param);

    /**
     * 查询详情
     * @param id
     * @return
     */
    BNoticeVo selectById(Integer id);

    /**
     * 更新
     * @param id
     * @return
     */
    UpdateResultAo<BNoticeVo> updateParamById(BNoticeVo param);

    /**
     * 查询详情
     * @param param
     * @return
     */
    BNoticeVo getPCDetail(BNoticeVo param);
}
