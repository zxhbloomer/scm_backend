package com.xinyirun.scm.core.system.serviceimpl.master.container;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.container.MContainerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.excel.container.MContainerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.container.MContainerVo;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.container.MContainerMapper;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
import com.xinyirun.scm.core.system.service.master.container.IMContainerService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: Wqf
 * @Description: 集装箱
 * @CreateTime : 2023/5/30 16:14
 */

@Service
public class MContainerServiceImpl extends BaseServiceImpl<MContainerMapper, MContainerEntity> implements IMContainerService {

    @Autowired
    private MContainerMapper mapper;

    @Autowired
    private IBMonitorService monitorService;

    /**
     * 查询列表下拉框
     *
     * @return
     */
    @Override
    public List<MContainerVo> selectList() {
        LambdaQueryWrapper<MContainerEntity> eq = new LambdaQueryWrapper<MContainerEntity>().eq(MContainerEntity::getIs_del, "0");
        List<MContainerEntity> list = mapper.selectList(eq);
        List<MContainerVo> result = (List<MContainerVo>) BeanUtilsSupport.copyProperties(list, MContainerVo.class);
        return result;
    }

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    @Override
    public IPage<MContainerVo> selectPageList(MContainerVo vo) {
        // 分页条件
        Page<MContainerVo> pageCondition = new Page(vo.getPageCondition().getCurrent(), vo.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, vo.getPageCondition().getSort());
        return mapper.selectPageList(vo, pageCondition);
    }

    /**
     * 新增
     *
     * @param vo
     * @return
     */
    @Override
    public InsertResultAo<MContainerVo> insert(MContainerVo vo) {
        checkLogic(vo);
        // 新增
        MContainerEntity entity = new MContainerEntity();
        entity.setIs_del(0);
        entity.setCode(vo.getCode());

        mapper.insert(entity);

        return InsertResultUtil.OK(getDetail(entity.getId()));
    }

    /**
     * 更新
     *
     * @param vo
     * @return
     */
    @Override
    public UpdateResultAo<MContainerVo> save(MContainerVo vo) {
        Assert.notNull(vo.getId(), "ID 不能为空");
        checkLogic(vo);

        MContainerEntity entity = mapper.selectById(vo.getId());
        entity.setCode(vo.getCode());

        mapper.updateById(entity);

        return UpdateResultUtil.OK(getDetail(vo.getId()));
    }

    /**
     * 删除
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MContainerVo vo) {
        for (Integer id : vo.getIds()) {
            List<Integer> list = monitorService.selectActiveMonitorByContainerId(id);
            if (!CollectionUtils.isEmpty(list)) {
                throw new AppBusinessException("删除出错：该箱号被监管任务使用中！");
            }

            MContainerEntity entity = mapper.selectById(id);
            entity.setIs_del(1);
            mapper.updateById(entity);
        }

    }

    /**
     * 导出查询
     *
     * @param vo
     * @return
     */
    @Override
    public List<MContainerExcelVo> selectExportList(MContainerVo vo) {
        return mapper.selectExportList(vo);
    }

    /**
     * 根据 id 查询详情
     * @param id 主键id
     * @return
     */
    private MContainerVo getDetail(Integer id) {
        return mapper.getDetailById(id);
    }

    /**
     * 校验
     * @param bean
     */
    private void checkLogic(MContainerVo bean) {
        if (StringUtils.isEmpty(bean.getCode())) {
            throw new BusinessException("箱号不能为空");
        }
        List<MContainerVo> list = mapper.selectByCode(bean.getId(), bean.getCode());
        if (null != list && !list.isEmpty()) {
            throw new BusinessException("箱号 重复!");
        }
    }

}
