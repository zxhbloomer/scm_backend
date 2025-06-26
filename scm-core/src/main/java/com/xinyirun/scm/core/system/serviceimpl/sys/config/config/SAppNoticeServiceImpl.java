package com.xinyirun.scm.core.system.serviceimpl.sys.config.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppNoticeEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppNoticeVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.app.SAppNoticeMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppNoticeService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.SAppNoticeAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
@Service
public class SAppNoticeServiceImpl extends ServiceImpl<SAppNoticeMapper, SAppNoticeEntity> implements ISAppNoticeService {

    @Autowired
    private SAppNoticeMapper mapper;

    @Autowired
    private SAppNoticeAutoCodeServiceImpl autoCodeService;

    @Autowired
    private ISConfigService isConfigService;

    @Override
    public IPage<SAppNoticeVo> selectPage(SAppNoticeVo searchCondition) {

        Page<SAppNoticeEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectList(pageCondition, searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(SAppNoticeVo vo) {

        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        SConfigEntity config = isConfigService.selectByKey(SystemConstants.APK_URL);

        SAppNoticeEntity entity = new SAppNoticeEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setCode(autoCodeService.autoCode().getCode());
        entity.setUrl(config.getValue());

        int rtn = mapper.insert(entity);

        return InsertResultUtil.OK(rtn);
    }

    @Override
    public UpdateResultAo<Integer> update(SAppNoticeVo vo) {
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        SConfigEntity config = isConfigService.selectByKey(SystemConstants.APK_URL);
        SAppNoticeEntity entity = new SAppNoticeEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setUrl(config.getValue());

        int rtn = mapper.updateById(entity);

        return UpdateResultUtil.OK(rtn);
    }

    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(SAppNoticeVo vo, String moduleType) {

        List<SAppNoticeVo> list = mapper.selectByVersionCode(vo.getVersion_code());

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (list.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：版本号【"+ vo.getVersion_code() +"】出现重复!", vo.getCode());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                // 新增场合，不能重复
                if (list.size() > 1) {
                    return CheckResultUtil.NG("编辑保存出错：版本号【"+ vo.getVersion_code() +"】出现重复!", vo.getCode());
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
