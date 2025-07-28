package com.xinyirun.scm.core.system.serviceimpl.business.wms.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.BWarehouseGroupEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MWarehouseRelationVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.warehouse.BWarehouseGroupMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupService;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IMWarehouseRelationService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWarehouseGroupAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 仓库组一级分类 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@Service
public class BWarehouseGroupServiceImpl extends ServiceImpl<BWarehouseGroupMapper, BWarehouseGroupEntity> implements IBWarehouseGroupService {

    @Autowired
    private BWarehouseGroupMapper mapper;

    @Autowired
    private BWarehouseGroupAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IMWarehouseRelationService imWarehouseRelationService;
   
    @Override
    public IPage<BWarehouseGroupVo> selectPage(BWarehouseGroupVo searchCondition) {
        // 分页条件
        Page<BWarehouseGroupEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<BWarehouseGroupVo> selectList(BWarehouseGroupVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BWarehouseGroupVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        BWarehouseGroupEntity entity = (BWarehouseGroupEntity) BeanUtilsSupport.copyProperties(vo, BWarehouseGroupEntity.class);
        // 设置拼音
        this.setPinyin(entity);
        // 自动生成编号
        entity.setCode(autoCodeService.autoCode().getCode());

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        MWarehouseRelationVo mWarehouseRelationVo = new MWarehouseRelationVo();
        mWarehouseRelationVo.setParent_id(1L);
        mWarehouseRelationVo.setSerial_id(vo.getId().longValue());
        mWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_WAREHOUSE_GROUP);
        mWarehouseRelationVo.setType(vo.getType());
        imWarehouseRelationService.insert(mWarehouseRelationVo);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BWarehouseGroupVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        BWarehouseGroupEntity entity = mapper.selectById(vo.getId());
        entity.setName(vo.getName());
        entity.setShort_name(vo.getShort_name());
        // 设置拼音
        this.setPinyin(entity);

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(BWarehouseGroupVo vo) {
        Integer count = mapper.selectCountByGroupId(vo.getId());
        if(count > 0){
            throw new BusinessException("该仓库组下存在仓库，不能删除");
        }
        mapper.deleteById(vo.getId());
    }

    @Override
    public BWarehouseGroupVo selectById(int id) {
        return mapper.selectDataById(id);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BWarehouseGroupVo vo, String moduleType) {
        // 数据库查询是否存在名重复数据
        List<BWarehouseGroupVo> selectByName = mapper.selectByName(vo.getName(), vo.getType());
        // 数据库查询是否存在编码重复数据
        List<BWarehouseGroupVo> selectByKey = mapper.selectByCode(vo.getCode());
        // 数据库查询是否存在简称重复数据
        List<BWarehouseGroupVo> selectByShortName = mapper.selectByShortName(vo.getShort_name(), vo.getType());

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByKey.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", vo.getCode());
                }
                if (selectByShortName.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", vo.getShort_name());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
//                if (selectByName.size() > 1) {
//                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
//                }
                if (selectByKey.size() > 2) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", vo.getCode());
                }
//                if (selectByShortName.size() > 1) {
//                    return CheckResultUtil.NG("新增保存出错：简称出现重复", vo.getShort_name());
//                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    private void setPinyin(BWarehouseGroupEntity entity) {
        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称拼音首字母
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setName_pinyin_abbr(str.toString());

        // 简称全拼
        entity.setShort_name_pinyin(Pinyin.toPinyin(entity.getShort_name(), ""));
        // 名称拼音首字母
        str = new StringBuilder("");
        for (char c: entity.getShort_name().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin_abbr(str.toString());
    }

}
