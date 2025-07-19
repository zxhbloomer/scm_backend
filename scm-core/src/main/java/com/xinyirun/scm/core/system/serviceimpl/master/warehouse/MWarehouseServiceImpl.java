package com.xinyirun.scm.core.system.serviceimpl.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.busniess.wms.warehouse.BWarehouseGroupRelationEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.warehouse.relation.BWarehouseRelationEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.*;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupRelationService;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MWarehouseAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
public class MWarehouseServiceImpl extends BaseServiceImpl<MWarehouseMapper, MWarehouseEntity> implements IMWarehouseService {

    @Autowired
    private MWarehouseMapper mapper;

    @Autowired
    private MLocationServiceImpl locationService;

    @Autowired
    private MBinServiceImpl binService;

    @Autowired
    private MWarehouseAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IMInventoryService inventoryService;

    @Autowired
    private IBWarehouseGroupRelationService ibWarehouseGroupRelationService;

    @Autowired
    private IBWarehouseRelationService ibWarehouseRelationService;

    @Override
    public IPage<MWarehouseVo> selectPage(MWarehouseVo searchCondition) {
        // 分页条件
        Page<MWarehouseEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.id")
    public List<MWarehouseVo> selectList(MWarehouseVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 获取仓库库区库位信息
     */
    @Override
    public MWarehouseLocationBinVo selectWarehouseLocationBin(int warehouse_id) {
        return mapper.selectWarehouseLocationBin(warehouse_id);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MWarehouseVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MWarehouseEntity entity = (MWarehouseEntity) BeanUtilsSupport.copyProperties(vo, MWarehouseEntity.class);
        entity.setEnable(Boolean.TRUE);
        // 设置拼音
        this.setPinyin(entity);
        if (StringUtils.isEmpty(entity.getCode())) {
            // 若未填编号，自动生成
            entity.setCode(autoCodeService.autoCode().getCode());
        }

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());
        // 生成默认库区
        MLocationVo location  = new MLocationVo();
        if (entity.getEnable_location() == null || entity.getEnable() == Boolean.FALSE) {
            entity.setEnable_location(Boolean.FALSE);
            location.setName(SystemConstants.DEFAULT_LOCATION);
            location.setShort_name(SystemConstants.DEFAULT_LOCATION);
            location.setWarehouse_id(entity.getId());
            location.setIs_default(Boolean.TRUE);
            locationService.insert(location);
        } else {
            entity.setEnable_location(Boolean.TRUE);
        }

        if (entity.getEnable_bin() == null || entity.getEnable() == Boolean.FALSE) {
            entity.setEnable_bin(Boolean.FALSE);
            MBinVo bin = new MBinVo();
            bin.setName(SystemConstants.DEFAULT_BIN);
            bin.setLocation_id(location.getId());
            bin.setWarehouse_id(entity.getId());
            binService.insert(bin);
        } else {
            entity.setEnable_bin(Boolean.TRUE);
        }

        mapper.updateById(entity);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MWarehouseVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MWarehouseEntity entity = (MWarehouseEntity) BeanUtilsSupport.copyProperties(vo, MWarehouseEntity.class);
        // 设置拼音
        this.setPinyin(entity);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MWarehouseEntity> selectByName(String name, Integer id) {
        // 查询 数据
        return mapper.selectByName(name, id);
    }

    @Override
    public List<MWarehouseEntity> selectByCode(String code, Integer id) {
        // 查询 数据
        return mapper.selectByCode(code, id);
    }

    @Override
    public List<MWarehouseEntity> selectByShortName(String shortName, Integer id) {
        // 查询 数据
        return mapper.selectByShortName(shortName, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MWarehouseVo> searchCondition) {
        List<MWarehouseEntity> list = mapper.selectIdsIn(searchCondition);
        for(MWarehouseEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MWarehouseVo> searchCondition) {
        List<MWarehouseEntity> list = mapper.selectIdsIn(searchCondition);
        // 根据 仓库ID 查询仓库库存量大于0, 锁定库存不等于0 的库存, 如果集合大于0, 则有仓库库存不为0, 无法禁用
        checkInventory(searchCondition);
        for(MWarehouseEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MWarehouseVo> searchCondition) {
        List<MWarehouseEntity> list = mapper.selectIdsIn(searchCondition);
        for(MWarehouseEntity entity : list) {
            if (entity.getEnable()) {
                // 禁用, 需判断仓库商品库存数量是否为0
                checkInventory(searchCondition);
            }
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    public MWarehouseVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 仓库导出 全部
     *
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    @Override
    public List<MWarehouseExportVo> exportAll(MWarehouseVo searchCondition) {
        return mapper.exportAll(searchCondition);
    }

    /**
     * 仓库导出 部分
     *
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    @Override
    public List<MWarehouseExportVo> export(List<MWarehouseVo> searchCondition) {
        return mapper.export(searchCondition);
    }

    /**
     * 根据 仓库 code 查询三大件
     *
     * @param warehouse_code
     * @return
     */
    @Override
    public List<MBLWBo> selectBLWByCode(String warehouse_code) {
        return mapper.selectBLWByCode(warehouse_code);
    }

    @Override
    public MWarehouseGroupTransferVo getWarehouseStaffTransferList(MWGroupTransferVo searchCondition) {

        MWarehouseGroupTransferVo vo = new MWarehouseGroupTransferVo();
        // 获取全部仓库组
        vo.setWarehouse_group_all(mapper.getAllWarehouseGroupTransferList(searchCondition));
        // 获取该该关系已经设置过的仓库组
        List<Long> rtnList = mapper.getWarehouseGroupTransferList(searchCondition);
        vo.setWarehouse_groups(rtnList.toArray(new Long[rtnList.size()]));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String setWarehouseGroupTransfer(MWGroupTransferVo bean) {
        // 删除剔除的仓库
        mapper.deleteWarehouseGroupRelationByWarehouseId(bean);
        // 增加选择的仓库
        List<BWarehouseGroupRelationEntity> lists = new ArrayList<>();
        for(int i=0; i < bean.getWarehouse_groups().length; i++){
            BWarehouseGroupRelationEntity entity = new BWarehouseGroupRelationEntity();
            entity.setWarehouse_id(bean.getWarehouse_id());
            entity.setWarehouse_group_id(bean.getWarehouse_groups()[i]);
            lists.add(entity);
        }

        ibWarehouseGroupRelationService.saveBatch(lists);
        return "OK";
    }

    @Override
    public MWarehouseStaffTransferVo getWarehouseStaffTransferList(MWStaffTransferVo searchCondition) {
        MWarehouseStaffTransferVo vo = new MWarehouseStaffTransferVo();
        // 获取全部仓库组
        vo.setWarehouse_all(mapper.getAllWarehouseStaffTransferList(searchCondition));
        // 获取该该关系已经设置过的仓库组
        List<Long> rtnList = mapper.getWarehouseStaffTransferList(searchCondition);
        vo.setWarehouses(rtnList.toArray(new Long[rtnList.size()]));
        return vo;
    }

    @Override
    public String setWarehouseStaffTransfer(MWStaffTransferVo bean) {
        // 删除剔除的仓库
        mapper.deleteWarehouseStaffRelationByStaffId(bean);
        // 增加选择的仓库
        List<BWarehouseRelationEntity> lists = new ArrayList<>();
        for(int i=0; i < bean.getWarehouse_ids().length; i++){
            BWarehouseRelationEntity entity = new BWarehouseRelationEntity();
            entity.setSerial_id(bean.getWarehouse_ids()[i]);
            entity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_WAREHOUSE);
            entity.setStaff_id(bean.getStaff_id());
            lists.add(entity);
        }

        ibWarehouseRelationService.saveBatch(lists);
        return "OK";
    }

    /**
     * 检查商品库存数量是否为 0
     * 不为0, 抛出异常
     */
    private void checkInventory(List<MWarehouseVo> searchCondition) {
        List<MInventoryVo> mInventoryVos = inventoryService.selectInventoryByWarehouse(searchCondition);
        if (!CollectionUtils.isEmpty(mInventoryVos) || mInventoryVos.size() != 0) {
            throw new BusinessException("请将所选仓库商品库存调整为0");
        }
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(MWarehouseVo vo, String moduleType) {
        List<MWarehouseEntity> selectByName = selectByName(vo.getName(), vo.getId());
        List<MWarehouseEntity> selectByKey = selectByCode(vo.getCode(), vo.getId());
        List<MWarehouseEntity> selectByShortName = selectByShortName(vo.getShort_name(), vo.getId());

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", vo.getCode());
                }
                if (selectByShortName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", selectByShortName);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", vo.getCode());
                }
                if (selectByShortName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", selectByShortName);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    private void setPinyin(MWarehouseEntity entity) {
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
