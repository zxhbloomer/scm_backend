package com.xinyirun.scm.core.system.serviceimpl.business.materialconvert;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.materialconvert.BConvertRecordEntity;
import com.xinyirun.scm.bean.entity.busniess.materialconvert.BMaterialConvertEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BConvertRecordVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.materialconvert.BMaterialConvertRecordMapper;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertRecordService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @date 2022/11/23 16:19
 */
@Service
public class IBMaterialConvertRecordServiceImpl extends BaseServiceImpl<BMaterialConvertRecordMapper, BConvertRecordEntity> implements IBMaterialConvertRecordService {

    @Autowired
    private BMaterialConvertRecordMapper mapper;

    /**
     * 查询分页列表
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<BConvertRecordVo> selectPage(BConvertRecordVo searchCondition) {
        // 分页条件
        Page<BMaterialConvertEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPageList(searchCondition, pageCondition);
    }

    @Override
    public InsertResultAo<Integer> insert(BConvertRecordVo vo) {
        BConvertRecordEntity entity = new BConvertRecordEntity();
        BeanUtilsSupport.copyProperties(vo, entity);

        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }
}
