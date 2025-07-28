package com.xinyirun.scm.core.system.service.business.message;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.message.BMessageEntity;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.bean.system.vo.business.message.BMessageVo;

import java.util.List;

/**
 * <p>
 * websocket 消息通知表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-22
 */
public interface IBMessageService extends IService<BMessageEntity> {

    /**
     * 新增
     * @param list 新增的serial_id 和 serial_code信息
     */
    void insert(List<BMessageBo> list, String type, String serial_type);

    /**
     * 分页查询
     * @param param
     * @return
     */
    IPage<BMessageVo> selectHeaderPageList(BMessageVo param);

    /**
     * 查询数量
     * @param param
     * @return
     */
    BMessageVo getHeaderCount(BMessageVo param);

    /**
     * 删除 notice 信息
     * @param serialId
     * @param serialCode
     * @param serialType
     */
    void deleteNotice(Integer serialId, String serialCode, String serialType);

    /**
     * 删除 notice 信息
     * @param deleteList
     */
    void deleteNoticeList(List<BMessageBo> deleteList);
}
