package com.xinyirun.scm.core.system.serviceimpl.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.excel.query.MInventoryDetailExportVo;
import com.xinyirun.scm.bean.system.vo.excel.query.MInventoryStagnationWarningExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQuerySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQueryVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.query.inventory.MInventoryDetailQueryMapper;
import com.xinyirun.scm.core.system.service.query.inventory.IMInventoryDetailQueryService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 库存明细查询
 * </p>
 *
 * @author xinyirun
 * @since 2021-09-23
 */
@Service
public class MInventoryDetailQueryServiceImpl extends BaseServiceImpl<MInventoryDetailQueryMapper, MInventoryEntity> implements IMInventoryDetailQueryService {

    @Autowired
    private MInventoryDetailQueryMapper mInventoryMapper;

    @Autowired
    private ISConfigService configService;

    /**
     * 库存明细查询
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public IPage<MInventoryDetailQueryVo> queryInventoryDetails(MInventoryDetailQueryVo searchCondition) {
        // 分页条件
        Page<MInventoryDetailQueryVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        return mInventoryMapper.queryInventoryDetails(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public MInventoryDetailQuerySumVo queryInventoryDetailsSum(MInventoryDetailQueryVo searchCondition) {

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mInventoryMapper.selectSumData(searchCondition);
    }

    @Override
    public List<MInventoryDetailExportVo> selectExportList(List<MInventoryDetailQueryVo> searchCondition) {
        return mInventoryMapper.selectExportList(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public List<MInventoryDetailExportVo> selectExportAllList(MInventoryDetailQueryVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mInventoryMapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mInventoryMapper.selectExportAllList(searchCondition);
    }

    /**
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public IPage<MInventoryDetailQueryVo> selectListByOrderId(MInventoryDetailQueryVo searchCondition) {
        // 分页条件
        Page<MInventoryDetailQueryVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        return mInventoryMapper.selectListByOrderId(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public IPage<MInventoryDetailQueryVo> queryInventoryByWarning(MInventoryDetailQueryVo searchCondition) {

        // 分页条件
        Page<MInventoryDetailQueryVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        return mInventoryMapper.queryInventoryByWarning(pageCondition, searchCondition);
    }
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public MInventoryDetailQuerySumVo queryInventoryByWarningSum(MInventoryDetailQueryVo searchCondition) {

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mInventoryMapper.queryInventoryByWarningSum(searchCondition);
    }


    @Override
    public List<MInventoryStagnationWarningExportVo> selectExportDataWarning(List<MInventoryDetailQueryVo> searchCondition) {
        return mInventoryMapper.selectExportDataWarning(searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public List<MInventoryStagnationWarningExportVo> selectExportAllDataWarning(MInventoryDetailQueryVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mInventoryMapper.selectExportWarningNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mInventoryMapper.selectExportAllDataWarning(searchCondition);
    }

}
