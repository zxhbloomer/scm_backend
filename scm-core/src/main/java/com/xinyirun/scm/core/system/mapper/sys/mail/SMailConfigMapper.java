package com.xinyirun.scm.core.system.mapper.sys.mail;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.mail.SMailConfigEntity;
import com.xinyirun.scm.bean.system.vo.mail.SMailConfigVo;
import org.springframework.stereotype.Repository;

/**
 * @Author: Wqf
 * @Description: mapper 层
 * @CreateTime : 2023/12/12 16:47
 */

@Repository
public interface SMailConfigMapper extends BaseMapper<SMailConfigEntity> {

    /**
     * 根据id查询vo
     * @param id
     * @return
     */
    SMailConfigVo selectVoById(Integer id);
}
