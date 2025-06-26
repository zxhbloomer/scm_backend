package com.xinyirun.scm.core.bpm.serviceimpl.business;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmCommentEntity;
import com.xinyirun.scm.core.bpm.mapper.business.BpmCommentMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmCommentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批流评论 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-11-07
 */
@Service
public class BpmCommentServiceImpl extends ServiceImpl<BpmCommentMapper, BpmCommentEntity> implements IBpmCommentService {

}
