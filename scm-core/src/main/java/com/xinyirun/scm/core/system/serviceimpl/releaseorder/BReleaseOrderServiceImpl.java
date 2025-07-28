package com.xinyirun.scm.core.system.serviceimpl.releaseorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.entity.business.releaseorder.BReleaseOrderEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseFilesVo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.releaseorder.BReleaseOrderMapper;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseFilesService;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderDetailService;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Service
public class BReleaseOrderServiceImpl extends ServiceImpl<BReleaseOrderMapper, BReleaseOrderEntity> implements IBReleaseOrderService {

    @Autowired
    private BReleaseOrderMapper mapper;

    @Autowired
    private IBReleaseOrderDetailService detailService;

    @Autowired
    private IMStaffService staffService;

    @Autowired
    private IBReleaseFilesService ibReleaseFilesService;

    @Autowired
    private ISConfigService configService;
    /**
     * 查询放货指令列表
     *
     * @param param 入参
     * @return IPage<BOwnerChangeVo>
     */
    @Override
    public IPage<BReleaseOrderVo> selectPage(BReleaseOrderVo param) {
        Page<BReleaseOrderVo> page = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        PageUtil.setSort(page, param.getPageCondition().getSort());
        return baseMapper.selectPages(page, param);
    }

    /**
     * 根据 ID 查询详情
     *
     * @param param
     * @return
     */
    @Override
    public BReleaseOrderVo get(BReleaseOrderVo param) {
        BReleaseOrderVo result = baseMapper.get(param.getId());
        // 查询详情
        List<BReleaseOrderDetailVo> detailList = detailService.selectByReleaseId(param.getId());
        result.setDetailList(detailList);

        // 查询附件
        List<BReleaseFilesVo> bReleaseFilesVos = ibReleaseFilesService.selectByReleaseOrderId(param.getId());

        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.IMG_URL);

        bReleaseFilesVos.stream().forEach(k->{
           k.setUrl(sConfigEntity.getValue()+k.getUrl().substring(k.getUrl().lastIndexOf("/")+1));
        });


        result.setFiles(bReleaseFilesVos);
        return result;
    }

    @Override
    public BReleaseOrderVo getDetail(BReleaseOrderVo param) {
        return baseMapper.selectByDetailId(param.getDetail_id());
    }

    /**
     * 查询列表, 带商品
     *
     * @param param
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t7.id")
    public IPage<BReleaseOrderVo> selectCommPage(BReleaseOrderVo param) {
        Page<BReleaseOrderVo> page = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 关闭 sql 优化
        page.setOptimizeCountSql(false);
        PageUtil.setSort(page, param.getPageCondition().getSort());
        // 查询入库计划page
        return mapper.selectCommPages(page, param);
    }

    /**
     * 新增 api
     *
     * @param param 参数
     * @return InsertResultAo<BReleaseOrderVo>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<String> insert(BReleaseOrderVo param) {
        // 放货指令不能重复
        checkLogic(CheckResultAo.INSERT_CHECK_TYPE, param);

        // 查询新增人
        MStaffVo mStaffVo = staffService.selectByid(SecurityUtil.getStaff_id());

        // 新增
        BReleaseOrderEntity entity = (BReleaseOrderEntity) BeanUtilsSupport.copyProperties(param, BReleaseOrderEntity.class);
        // 默认 执行中
        LocalDateTime now = LocalDateTime.now();
        entity.setStatus_name("执行中");
        entity.setC_time(now);
        entity.setC_name(mStaffVo.getLogin_name());
        entity.setU_name(mStaffVo.getLogin_name());
        entity.setU_time(now);
        // 来源
        entity.setSource_type(SystemConstants.DATA_SOURCE_TYPE.WMS);
        mapper.insert(entity);
        // 新增物料
        List<BReleaseOrderDetailEntity> collect = param.getDetailList().stream().map(item -> {
            BReleaseOrderDetailEntity detailEntity = (BReleaseOrderDetailEntity) BeanUtilsSupport.copyProperties(item, BReleaseOrderDetailEntity.class);
            detailEntity.setRelease_order_code(entity.getCode());
            detailEntity.setRelease_order_id(entity.getId());
            detailEntity.setC_time(now);
            detailEntity.setC_name(mStaffVo.getLogin_name());
            detailEntity.setU_name(mStaffVo.getLogin_name());
            detailEntity.setU_time(now);
            return detailEntity;
        }).collect(Collectors.toList());
        detailService.saveBatch(collect);
        return InsertResultUtil.OK("OK");
    }

    /**
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<String> updateByParam(BReleaseOrderVo param) {
        // 放货指令不能重复
        checkLogic(CheckResultAo.UPDATE_CHECK_TYPE, param);

        // 查询更新人
        MStaffVo mStaffVo = staffService.selectByid(SecurityUtil.getStaff_id());
        LocalDateTime now = LocalDateTime.now();

        // 更新业务
        BReleaseOrderEntity entity = (BReleaseOrderEntity) BeanUtilsSupport.copyProperties(param, BReleaseOrderEntity.class);
        entity.setU_name(mStaffVo.getLogin_name());
        entity.setU_time(now);
        int upd = mapper.updateById(entity);
        if (upd == 0) {
            throw new UpdateErrorException("更新失败");
        }

        // 更新详情
        List<BReleaseOrderDetailVo> detailList = param.getDetailList();
        List<Integer> collect = detailList.stream().map(BReleaseOrderDetailVo::getId).collect(Collectors.toList());
        // 先删除已删除的
        List<BReleaseOrderDetailVo> bReleaseOrderDetailVos = detailService.selectByReleaseId(entity.getId());
        // 如果 数据库中有, 前端没传过来, 说明删除了
        List<Integer> collect2 = bReleaseOrderDetailVos.stream().filter(item -> !collect.contains(item.getId()))
                .map(BReleaseOrderDetailVo::getId).collect(Collectors.toList());
        detailService.removeBatchByIds(collect2);


        for (BReleaseOrderDetailVo bReleaseOrderDetailVo : detailList) {
                // 新增
                BReleaseOrderDetailEntity detailEntity = (BReleaseOrderDetailEntity) BeanUtilsSupport.copyProperties(bReleaseOrderDetailVo, BReleaseOrderDetailEntity.class);
                detailEntity.setRelease_order_code(entity.getCode());
                detailEntity.setRelease_order_id(entity.getId());
                detailEntity.setC_time(now);
                detailEntity.setC_name(mStaffVo.getLogin_name());
                detailEntity.setU_name(mStaffVo.getLogin_name());
                detailEntity.setU_time(now);
                detailService.saveOrUpdate(detailEntity);

        }

        return UpdateResultUtil.OK("OK");
    }

    /**
     * 删除
     *
     * @param param
     */
    @Override
    public void delete(BReleaseOrderVo param) {
        checkLogic(CheckResultAo.DELETE_CHECK_TYPE, param);

    }

    /**
     * 数据校验
     * @param checkType
     * @param param
     */
    private void checkLogic(String checkType, BReleaseOrderVo param) {
        // 放货指令编号不能重复校验
        BReleaseOrderVo bReleaseOrderVo = mapper.selectCodeOrId(param.getId(), param.getCode());

        switch (checkType) {
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 只能更新 wms 的
                if (!SystemConstants.DATA_SOURCE_TYPE.WMS.equals(param.getSource_type())) {
                    throw new UpdateErrorException("无法删除, 不是 WMS 新增的!");
                }

                // 编号重复校验
                if (null != bReleaseOrderVo)
                    throw new UpdateErrorException("放货指令编号重复!");
                break;

            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增编号重复校验
                if (null != bReleaseOrderVo)
                    throw new InsertErrorException("放货指令编号重复!");
                break;

            case CheckResultAo.DELETE_CHECK_TYPE:
                // 只能更新 wms 的
                if (!SystemConstants.DATA_SOURCE_TYPE.WMS.equals(param.getSource_type())) {
                    throw new BusinessException("无法删除, 不是 WMS 新增的!");
                }
                break;
            default:
                break;
        }


    }
}
