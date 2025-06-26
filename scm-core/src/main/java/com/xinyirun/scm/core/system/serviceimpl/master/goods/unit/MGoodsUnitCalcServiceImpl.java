package com.xinyirun.scm.core.system.serviceimpl.master.goods.unit;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitCalcEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.sys.unit.SUnitVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.unit.MGoodsUnitCalcMapper;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMUnitService;
import com.xinyirun.scm.core.system.service.sys.unit.ISUnitService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-16
 */
@Service
public class MGoodsUnitCalcServiceImpl extends BaseServiceImpl<MGoodsUnitCalcMapper, MGoodsUnitCalcEntity> implements IMGoodsUnitCalcService {

    @Autowired
    private MGoodsUnitCalcMapper mapper;

    @Autowired
    private IMUnitService imUnitService;
    @Autowired
    private ISUnitService isUnitService;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MGoodsUnitCalcVo> selectPage(MGoodsUnitCalcVo searchCondition) {
        // 分页条件
        Page<MGoodsUnitCalcVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 无分页查询
     * @param searchCondition
     * @return
     */
    @Override
    public List<MGoodsUnitCalcVo> selectList(MGoodsUnitCalcVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 获取数据条数
     */
    @Override
    public Integer getCount(MGoodsUnitCalcVo searchCondition) {
        return mapper.getCount(searchCondition);
    }

    /**
     * 查询一条数据
     * @param searchCondition
     * @return
     */
    @Override
    public MGoodsUnitCalcVo selectOne(MGoodsUnitCalcVo searchCondition) {
        return mapper.selectOne(searchCondition);
    }

    /**
     * 无分页查询
     * @param searchCondition
     * @return
     */
    @Override
    public List<MUnitVo> selectUnusedUnitsList(MGoodsUnitCalcVo searchCondition) {
        if("update".equals(searchCondition.getStatus())){
            // 更新模式
            return mapper.selectAllUnitsList(searchCondition);
        } else {
            // 其他模式
            return mapper.selectUnusedUnitsList(searchCondition);
        }
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public MGoodsUnitCalcVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 插入集合list记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<MGoodsUnitCalcVo> insert(MGoodsUnitCalcVo vo) {

        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

//        MUnitVo mUnitVo = imUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//        vo.setTgt_unit_id(mUnitVo.getId());
        SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
        vo.setTgt_unit_id(sUnitVo.getId());
        vo.setTgt_unit_code(SystemConstants.DEFAULT_UNIT.CODE);
        vo.setTgt_unit(SystemConstants.DEFAULT_UNIT.NAME);
        vo.setEnable(true);

        // 插入逻辑保存
        MGoodsUnitCalcEntity entity = (MGoodsUnitCalcEntity) BeanUtilsSupport.copyProperties(vo, MGoodsUnitCalcEntity.class);
        mapper.insert(entity);

        // 查询数据
        MGoodsUnitCalcVo rtnVo = this.selectById(entity.getId());

        // 设置返回值
        return InsertResultUtil.OK(rtnVo);
    }

    /**
     * 执行更新
     * @param vo
     * @return
     */
    @Override
    public UpdateResultAo<MGoodsUnitCalcVo> update(MGoodsUnitCalcVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        MGoodsUnitCalcEntity entity = (MGoodsUnitCalcEntity) BeanUtilsSupport.copyProperties(vo, MGoodsUnitCalcEntity.class);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        // 查询数据
        MGoodsUnitCalcVo rtnVo = this.selectById(entity.getId());

        // 设置返回值
        return UpdateResultUtil.OK(rtnVo);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(MGoodsUnitCalcVo vo, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if(vo.getSrc_unit_id() == null){
                    throw new BusinessException("请输入入库时计量单位！");
                }
                // 新增场合，不能重复
                MGoodsUnitCalcVo searchCondition = new MGoodsUnitCalcVo();
                searchCondition.setSku_id(vo.getSku_id());
                searchCondition.setSrc_unit_id(vo.getSrc_unit_id());
                MGoodsUnitCalcVo oneUnit = mapper.selectOne(searchCondition);
                if (oneUnit!=null) {
                    throw new BusinessException("该物料已存在"+vo.getSrc_unit()+"->"+vo.getCalc()+"关系,无法重复添加");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，无
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 删除单位换算数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(MGoodsUnitCalcVo vo){
        int delCount = mapper.deleteById(vo.getId());
        if(delCount == 0){
            throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
        }
        return DeleteResultUtil.OK(delCount);
    }

    /**
     * 根据id获取详情单条数据
     */
    @Override
    public MGoodsUnitCalcVo detail(MGoodsUnitCalcVo searchCondition) {
        return selectById(searchCondition.getId());
    }

}
