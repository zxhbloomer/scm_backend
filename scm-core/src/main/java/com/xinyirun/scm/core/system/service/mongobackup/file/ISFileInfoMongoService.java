package com.xinyirun.scm.core.system.service.mongobackup.file;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.file.SFileMonitorInfoMongoVo;

/**
 * <p>
 * 附件详情 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ISFileInfoMongoService extends IService<SFileInfoEntity> {

    /**
     * 根据文件 id 查询
     * @param id
     * @return
     */
    SFileMonitorInfoMongoVo selectFId(Integer id);
}
