    package com.xinyirun.scm.core.system.serviceimpl.master.cancel;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.cancel.MCancelEntity;
import com.xinyirun.scm.bean.entity.log.sys.SLogImportEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.cancel.MCancelMapper;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
@Service
public class MCancelServiceImpl extends BaseServiceImpl<MCancelMapper, MCancelEntity> implements MCancelService {

    @Autowired
    private MCancelMapper mapper;

    @Override
    public IPage<MCancelVo> selectPage(MCancelVo searchCondition) {
        // 分页条件
        Page<MCancelVo> pageCondition = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        // 查询page

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MCancelVo vo) {
        MCancelEntity entity = new MCancelEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    public MCancelVo selectBySerialIdAndType(MCancelVo vo) {
        if (vo == null || vo.getSerial_id() == null || vo.getSerial_type() == null) {
            return null;
        }
        return mapper.selectBySerialIdAndType(vo);
    }

    @Override
    @Transactional
    public void delete(MCancelVo vo) {
        mapper.deleteData(vo);
    }

}
