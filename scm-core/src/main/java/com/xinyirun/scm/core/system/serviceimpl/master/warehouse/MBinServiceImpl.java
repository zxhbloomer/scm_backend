package com.xinyirun.scm.core.system.serviceimpl.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MBinExportVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MBinVo;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.master.warehouse.IMBinService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MBinAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 库位 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MBinServiceImpl extends BaseServiceImpl<MBinMapper, MBinEntity> implements IMBinService {

    @Autowired
    private MBinMapper mapper;

    @Autowired
    private MWarehouseMapper warehouseMapper;

    @Autowired
    private MBinAutoCodeServiceImpl autoCodeService;

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<MBinVo> selectPage(MBinVo searchCondition) {
        // 分页条件
        Page<MBinEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        if(searchCondition.getCombine_search_condition() != null
                && searchCondition.getCombine_search_condition().split(SystemConstants.WAREHOUSE_LOCSTION_BIN_DELIMITER).length > 0) {
            searchCondition.setCombine_search_condition(searchCondition.getCombine_search_condition().split(SystemConstants.WAREHOUSE_LOCSTION_BIN_DELIMITER)[0].trim());
        }
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MBinVo> selecList(MBinVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MBinVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getLocation_id(), vo.getWarehouse_id(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MBinEntity entity = (MBinEntity) BeanUtilsSupport.copyProperties(vo, MBinEntity.class);
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
    public UpdateResultAo<Integer> update(MBinVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getLocation_id(), vo.getWarehouse_id(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MBinEntity entity = (MBinEntity) BeanUtilsSupport.copyProperties(vo, MBinEntity.class);
        // 设置拼音
        this.setPinyin(entity);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MBinEntity> selectByName(String name,int warehouse_id,int location_id) {
        // 查询 数据
        return mapper.selectByName(name,warehouse_id,location_id);
    }

    @Override
    public List<MBinEntity> selectByCode(String code,int warehouse_id,int location_id) {
        // 查询 数据
        return mapper.selectByCode(code,warehouse_id,location_id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MBinVo> searchCondition) {
        List<MBinEntity> list = mapper.selectIdsIn(searchCondition);
        for(MBinEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MBinVo> searchCondition) {
        List<MBinEntity> list = mapper.selectIdsIn(searchCondition);
        for(MBinEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MBinVo> searchCondition) {
        List<MBinEntity> list = mapper.selectIdsIn(searchCondition);
        for(MBinEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    public MBinVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 导出
     *
     * @param searchCondition 入参
     * @return List<MBinExportVo>
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public List<MBinExportVo> export(MBinVo searchCondition) {
        return mapper.exportList(searchCondition);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(String name, String code, int location_id, int warehouse_id , String moduleType) {
        // 数据查询库位名称是否重复
        List<MBinEntity> selectByName = selectByName(name,warehouse_id,location_id);
        // 数据查询库位编码是否重复
        List<MBinEntity> selectByKey = selectByCode(code,warehouse_id,location_id);
        // 数据库查询仓库库位状态是否启用
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
//                if (!warehouseEntity.getEnable_bin()) {
//                    return CheckResultUtil.NG("新增保存出错：仓库库位状态未启用", warehouseEntity.getName());
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
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    private void setPinyin(MBinEntity entity) {
        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称拼音首字母
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setName_pinyin_initial(str.toString());
    }
}
