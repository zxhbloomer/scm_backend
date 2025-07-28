package com.xinyirun.scm.core.system.serviceimpl.business.price;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.vo.sync.ApiBMaterialPriceVo;
import com.xinyirun.scm.bean.entity.business.price.BMaterialPriceEntity;
import com.xinyirun.scm.bean.system.vo.business.price.BMaterialPriceVo;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.price.BMaterialPriceMapper;
import com.xinyirun.scm.core.system.service.business.price.IBMaterialPriceService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-15
 */
@Service
@Slf4j
public class BMaterialPriceServiceImpl extends ServiceImpl<BMaterialPriceMapper, BMaterialPriceEntity> implements IBMaterialPriceService {

    /**
     * 查询页面
     *
     * @param param 参数
     * @return IPage<BMaterialPriceVo>
     */
    @Override
    public IPage<BMaterialPriceVo> selectPage(BMaterialPriceVo param) {
        Page<BMaterialPriceVo> page = new Page<>(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        PageUtil.setSort(page, param.getPageCondition().getSort());
        return baseMapper.selectPageList(page, param);
    }

    /**
     * 全部同步
     *
     * @param param
     */
    @Override
    public void sync(List<BMaterialPriceVo> param) {

    }

    /**
     * 部分同步, 选择ID
     *
     * @param param
     */
    @Override
    public void syncAll(BMaterialPriceVo param) {
        // 查询所有数据, size 为 -1 时不适用分页
        Page<BMaterialPriceVo> page = new Page<>(param.getPageCondition().getCurrent(), -1);
        IPage<BMaterialPriceVo> bMaterialPriceVoIPage = baseMapper.selectPageList(page, param);
        // 全部同步数据
        List<BMaterialPriceVo> records = new ArrayList<>();
        if (StringUtils.isNotNull(bMaterialPriceVoIPage)) {
            records = bMaterialPriceVoIPage.getRecords();
        }
        log.info("测试: {}" ,records);
    }

    @Override
    public List<ApiBMaterialPriceVo> getMaterialPriceList() {
        return baseMapper.getMaterialPriceList();
    }
}
