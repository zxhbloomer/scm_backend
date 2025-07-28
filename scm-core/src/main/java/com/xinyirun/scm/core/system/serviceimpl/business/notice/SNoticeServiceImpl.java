package com.xinyirun.scm.core.system.serviceimpl.business.notice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.entity.business.notice.BNoticeEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.notice.BNoticeMapper;
import com.xinyirun.scm.core.system.service.business.notice.IBNoticeService;
import com.xinyirun.scm.core.system.service.business.notice.IBNoticeStaffService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Service
public class SNoticeServiceImpl extends ServiceImpl<BNoticeMapper, BNoticeEntity> implements IBNoticeService {

    @Autowired
    private BNoticeMapper mapper;

    @Autowired
    private IBNoticeStaffService noticeStaffService;

    /**
     * 列表查询
     *
     * @param param
     * @return
     */
    @Override
    public IPage<BNoticeVo> selectPageList(BNoticeVo param) {
        // 分页条件
        Page<BInPlanEntity> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.selectPageList(pageCondition, param);
    }

    /**
     * 列表查询
     * @param param
     * @return
     */
    @Override
    public List<BNoticeVo> getNoticeUnreadTen(BNoticeVo param){
        return mapper.getNoticeUnreadTen(param);
    }

    /**
     * 新增
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BNoticeVo> insert(BNoticeVo param) {
        BNoticeEntity entity = new BNoticeEntity();
        BeanUtilsSupport.copyProperties(param, entity);
        mapper.insert(entity);

        // 新增关联关系
        noticeStaffService.insertNoticeStaff(entity.getId(), param.getStaff_list());

        BNoticeVo bNoticeVo = selectById(entity.getId());
        return InsertResultUtil.OK(bNoticeVo);
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @Override
    public BNoticeVo selectById(Integer id) {
        BNoticeVo result = new BNoticeVo();
        BNoticeEntity bNoticeEntity = mapper.selectById(id);

        // 查询员工列表
        List<MStaffVo> mStaffVos = noticeStaffService.selectStaffList(id);

        BeanUtilsSupport.copyProperties(bNoticeEntity, result);
        result.setStaff_list(mStaffVos);
        return result;
    }

    /**
     * 更新
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BNoticeVo> updateParamById(BNoticeVo param) {
        BNoticeEntity entity = new BNoticeEntity();
        BeanUtilsSupport.copyProperties(param, entity);
        mapper.updateById(entity);

        // 更新关联关系
        noticeStaffService.updateNoticeStaff(entity.getId(), param.getStaff_list());

        BNoticeVo bNoticeVo = selectById(param.getId());
        return UpdateResultUtil.OK(bNoticeVo);
    }

    /**
     * 查询详情
     *
     * @param param
     * @return
     */
    @Override
    public BNoticeVo getPCDetail(BNoticeVo param) {
        BNoticeVo result = mapper.selectPCDetail(param);

        if (result != null && !"1".equals(result.getIs_read())) {
            // 更新已读状态
            noticeStaffService.updateIsRead(param.getId(), param.getStaff_id());
        }

        return result;
    }
}
