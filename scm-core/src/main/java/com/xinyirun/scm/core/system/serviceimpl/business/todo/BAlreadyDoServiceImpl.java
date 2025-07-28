package com.xinyirun.scm.core.system.serviceimpl.business.todo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.entity.business.todo.BAlreadyDoEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.todo.BAlreadyDoVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.todo.BAlreadyDoMapper;
import com.xinyirun.scm.core.system.service.business.todo.IBAlreadyDoService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 待办事项 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
@Service
public class BAlreadyDoServiceImpl extends BaseServiceImpl<BAlreadyDoMapper, BAlreadyDoEntity> implements IBAlreadyDoService {

    @Autowired
    private BAlreadyDoMapper mapper;

    @Override
    public IPage<BAlreadyDoVo> selectPage(BAlreadyDoVo searchCondition) {
        // 分页条件
        Page<BAlreadyDoEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition, SecurityUtil.getStaff_id());
    }

    @Override
    public List<Integer> selectAlreadyDoIdList(String serial_type, Long staff_id) {
        return mapper.selectAlreadyDoIdList(serial_type, staff_id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BAlreadyDoVo vo) {
        // 插入逻辑保存
        BAlreadyDoEntity entity = (BAlreadyDoEntity) BeanUtilsSupport.copyProperties(vo, BAlreadyDoEntity.class);

        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> save(BAlreadyDoVo vo) {
        // 修改逻辑保存
        BAlreadyDoEntity entity = (BAlreadyDoEntity) BeanUtilsSupport.copyProperties(vo, BAlreadyDoEntity.class);

        int rtn = mapper.updateById(entity);
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 生成已办事项
     * @param vo
     */
    @Override
    public void insertAlreadyDo(BAlreadyDoVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id());
        this.insert(vo);
    }

}
