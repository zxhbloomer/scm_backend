package com.xinyirun.scm.core.system.serviceimpl.master.goods;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.*;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.*;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.*;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MGoodsSpecServiceImpl extends BaseServiceImpl<MGoodsSpecMapper, MGoodsSpecEntity> implements IMGoodsSpecService {

    @Autowired
    private MGoodsSpecMapper mapper;


    @Autowired
    private MCategoryMapper categorymapper;

    @Autowired
    private MGoodsMapper goodsmapper;

    @Autowired
    private IMGoodsUnitCalcService unitCalcService;

    @Autowired
    private ISConfigService isConfigService;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MGoodsSpecVo> selectPage(MGoodsSpecVo searchCondition) {
        // 分页条件
        Page<MGoodsSpecEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MGoodsSpecVo> list = mapper.selectPage( pageCondition,searchCondition);
//        for(MGoodsSpecVo v:list.getRecords()){
//            MGoodsUnitConvertVo vo = new MGoodsUnitConvertVo();
//            vo.setSku_id(v.getId());
//            List<MGoodsUnitConvertVo> unitConverVoList = mGoodsUnitConvertMapper.selectList(vo);
//            v.setUnitList(unitConverVoList);
//        }
        return list;
    }

    /**
     * 查询树状列表
     * @param searchCondition
     * @return
     */
    @Override
    public List<MGoodsSpecLeftVo> selectLeft(MGoodsSpecLeftVo searchCondition) {
        // 简化为类别-物料两层结构
        List<MCategoryEntity> categorylist = categorymapper.selectList(new QueryWrapper<MCategoryEntity>());
        List<MCategoryVo> calist = BeanUtilsSupport.copyProperties(categorylist,MCategoryVo.class);
        for(MCategoryVo ca:calist){
            // 通过类别id查询物料list
            List<MGoodsEntity> goodslist = goodsmapper.selectList(new QueryWrapper<MGoodsEntity>().eq("category_id",ca.getId()));
            List<MGoodsVo> golist = BeanUtilsSupport.copyProperties(goodslist,MGoodsVo.class);
            ca.setGoodsVo(golist);
        }
        return mapper.selectLeft(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MGoodsSpecVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getSpec(),vo.getPm(),vo.getCode(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MGoodsSpecEntity entity = (MGoodsSpecEntity) BeanUtilsSupport.copyProperties(vo, MGoodsSpecEntity.class);
        if(vo.getGoods_name() != null){
            entity.setName(vo.getGoods_name());
        }
        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());
//        for(MGoodsUnitConvertVo unit:vo.getUnitList()){
//            unit.setSku_id(entity.getId());
//        }

        // 添加单位换算关系
        insertUnitCalc(entity);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 新增 默认 单位换算关系
     * @param entity
     */
    private void insertUnitCalc(MGoodsSpecEntity entity) {
        MGoodsUnitCalcVo calcVo = new MGoodsUnitCalcVo();
        calcVo.setSku_id(entity.getId());
        List<MGoodsUnitCalcVo> vos = unitCalcService.selectList(calcVo);
        if (!CollectionUtils.isEmpty(vos)) {
            return;
        }
        // 执行新增
        SConfigEntity config = isConfigService.selectByKey(SystemConstants.DEFAULT_GOODS_UNIT_CALC);
        if (null == config) {
            throw new InsertErrorException("保存失败, 未配置默认单位换算关系");
        }
        JSONObject jsonObject = new JSONObject(config.getValue());
        // 查询配置
        MGoodsUnitCalcVo vo = new MGoodsUnitCalcVo();
        vo.setSku_id(entity.getId());
        vo.setSrc_unit(jsonObject.getStr("src_unit"));
        vo.setSrc_unit_id(jsonObject.getInt("src_unit_id"));
        vo.setSrc_unit_code(jsonObject.getStr("src_unit_code"));
        vo.setCalc(jsonObject.getBigDecimal("calc"));
        vo.setRemark(jsonObject.getStr("remark"));
        unitCalcService.insert(vo);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MGoodsSpecVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getSpec(),vo.getPm(),vo.getCode(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        MGoodsSpecEntity entity = (MGoodsSpecEntity) BeanUtilsSupport.copyProperties(vo, MGoodsSpecEntity.class);
        if(vo.getGoods_name() != null){
            entity.setName(vo.getGoods_name());
        }
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public DeleteResultAo<Integer> deleteByIdsIn(List<MGoodsSpecVo> searchCondition) {
        return null;
    }

    /**
     * 启用
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MGoodsSpecVo> searchCondition) {
        List<MGoodsSpecEntity> list = mapper.selectIdsIn(searchCondition);
        for(MGoodsSpecEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 停用
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MGoodsSpecVo> searchCondition) {
        List<MGoodsSpecEntity> list = mapper.selectIdsIn(searchCondition);
        for(MGoodsSpecEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MGoodsSpecVo> searchCondition) {
        List<MGoodsSpecEntity> list = mapper.selectIdsIn(searchCondition);
        for(MGoodsSpecEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MGoodsSpecExportVo>
     */
    @Override
    public List<MGoodsSpecExportVo> export(MGoodsSpecVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }

    /**
     * 查询物料转换 商品
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MGoodsSpecVo> getConvertGoodsList(MGoodsSpecVo searchCondition) {
        // 分页条件
        Page<MGoodsSpecEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.getConvertGoodsList( pageCondition,searchCondition);
    }

    /**
     * 根据 物料id查询规格
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MGoodsSpecVo> selectListByGoodsId(MGoodsSpecVo searchCondition) {
        return mapper.selectListByGoodsId(searchCondition);
    }

    @Override
    public MGoodsSpecVo selectById(int id) {
        return mapper.selectId(id);
    }

    @Override
    public List<MGoodsSpecEntity> selectByName(String spec) {
        // 查询 数据
        return mapper.selectByName(spec);
    }

    @Override
    public MGoodsSpecVo selectByCode(String code) {
        return mapper.selectByCode(code);
    }


    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(String spec, String pm, String code, String moduleType) {
        List<MGoodsSpecEntity> selectBySpec = selectByName(spec);
        List<MGoodsSpecEntity>  pmlist = mapper.selectList(new QueryWrapper<MGoodsSpecEntity>().eq("pm",pm));
        List<MGoodsSpecEntity>  codelist = mapper.selectList(new QueryWrapper<MGoodsSpecEntity>().eq("code",code));
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectBySpec.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：规格出现重复", spec);
                }
                if (pmlist.size() > 1) {
                    return CheckResultUtil.NG("新增保存出错：品名出现重复", pm);
                }
                if (codelist.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectBySpec.size() > 1) {
                    return CheckResultUtil.NG("新增保存出错：规格出现重复", spec);
                }
                if (pmlist.size() > 1) {
                    return CheckResultUtil.NG("新增保存出错：品名出现重复", pm);
                }
                if (codelist.size() > 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

}
