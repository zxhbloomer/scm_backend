package com.xinyirun.scm.core.system.serviceimpl.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationExportVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MLocationMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.master.warehouse.IMLocationService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MLocationAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class MLocationServiceImpl extends BaseServiceImpl<MLocationMapper, MLocationEntity> implements IMLocationService {

    @Autowired
    private MLocationMapper mapper;

    @Autowired
    private MWarehouseMapper warehouseMapper;

    @Autowired
    private MLocationAutoCodeServiceImpl autoCodeService;

    @Override
    public IPage<MLocationVo> selectPage(MLocationVo searchCondition) {
        // 分页条件
        Page<MLocationEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MLocationVo> selectList(MLocationVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MLocationVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getShort_name(), vo.getWarehouse_id(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MLocationEntity entity = (MLocationEntity) BeanUtilsSupport.copyProperties(vo, MLocationEntity.class);
        entity.setEnable(Boolean.TRUE);
        // 设置拼音
        this.setPinyin(entity);
        // 自动生成编号
        entity.setCode(autoCodeService.autoCode().getCode());

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MLocationVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getShort_name(), vo.getWarehouse_id(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MLocationEntity entity = (MLocationEntity) BeanUtilsSupport.copyProperties(vo, MLocationEntity.class);
        entity.setEnable(Boolean.TRUE);
        // 设置拼音
        this.setPinyin(entity);

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MLocationEntity> selectByName(String name,int warehouse_id) {
        // 查询 数据
        return mapper.selectByName(name,warehouse_id);
    }

    @Override
    public List<MLocationEntity> selectByCode(String code,int warehouse_id) {
        // 查询 数据
        return mapper.selectByCode(code,warehouse_id);
    }

    @Override
    public List<MLocationEntity> selectByShortName(String shortName,int warehouse_id) {
        // 查询 数据
        return mapper.selectByShortName(shortName,warehouse_id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MLocationVo> searchCondition) {
        List<MLocationEntity> list = mapper.selectIdsIn(searchCondition);
        for(MLocationEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MLocationVo> searchCondition) {
        List<MLocationEntity> list = mapper.selectIdsIn(searchCondition);
        for(MLocationEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MLocationVo> searchCondition) {
        List<MLocationEntity> list = mapper.selectIdsIn(searchCondition);
        for(MLocationEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    public MLocationVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 导出
     *
     * @param searchCondition 入参
     * @return List<MLocationExportVo>
     */
    @Override
    public List<MLocationExportVo> export(MLocationVo searchCondition) {
        return mapper.exportList(searchCondition);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(String name, String code, String shortName, int warehouse_id , String moduleType) {
        // 数据库查询是否存在库区名重复数据
        List<MLocationEntity> selectByName = selectByName(name,warehouse_id);
        // 数据库查询是否存在库区编码重复数据
        List<MLocationEntity> selectByKey = selectByCode(code,warehouse_id);
        // 数据库查询是否存在库区简称重复数据
        List<MLocationEntity> selectByShortName = selectByShortName(shortName,warehouse_id);
        // 查询仓库数据
        MWarehouseEntity warehouseEntity = warehouseMapper.selectById(warehouse_id);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                if (selectByShortName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", selectByShortName);
                }
//                if (warehouseEntity.getEnable_location() == Boolean.FALSE) {
//                    return CheckResultUtil.NG("新增保存出错：该仓库库区状态未启用");
//                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                if (selectByShortName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", selectByShortName);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    private void setPinyin(MLocationEntity entity) {
        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称拼音首字母
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setName_pinyin_initial(str.toString());

        // 简称全拼
        entity.setShort_name_pinyin(Pinyin.toPinyin(entity.getShort_name(), ""));
        // 名称拼音首字母
        str = new StringBuilder("");
        for (char c: entity.getShort_name().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin_initial(str.toString());
    }
}
