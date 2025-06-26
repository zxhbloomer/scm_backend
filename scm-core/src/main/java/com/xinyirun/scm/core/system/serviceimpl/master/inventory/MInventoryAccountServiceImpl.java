package com.xinyirun.scm.core.system.serviceimpl.master.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryAccountEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryAccountExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryAccountVo;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.inventory.MInventoryAccountMapper;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryAccountService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MInventoryAccountServiceImpl extends BaseServiceImpl<MInventoryAccountMapper, MInventoryAccountEntity> implements IMInventoryAccountService {

    @Autowired
    private MInventoryAccountMapper mapper;

    @Autowired
    private ISConfigService configService;

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t1.warehouse_id")
    public IPage<MInventoryAccountVo> selectPage(MInventoryAccountVo searchCondition) {
        // 分页条件
        Page<MInventoryEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        // 替换分页插件自动count sql 因为该sql执行速度非常慢
        pageCondition.setCountId("selectPageMyCount");

        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 导出查询
     *
     * @param searchCondition 查询条件
     * @return List<MInventoryAccountExportVo>
     */
    @Override
    public List<MInventoryAccountExportVo> selectExportList(MInventoryAccountVo searchCondition) {
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (null == searchCondition.getIds() && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue())
                && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectPageMyCount(searchCondition);
            if (count != null && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportList(searchCondition);
    }

    /**
     * 查询合计
     *
     * @param searchCondition
     * @return
     */
    @Override
    public MInventoryAccountVo selectListSum(MInventoryAccountVo searchCondition) {
        return mapper.selectListSum(searchCondition);
    }
}
