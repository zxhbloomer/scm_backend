package com.xinyirun.scm.core.system.serviceimpl.wms.in;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.system.ao.result.*;
import com.xinyirun.scm.bean.system.result.utils.v1.*;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.wms.in.BInMapper;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 入库单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Slf4j
@Service
public class BInServiceImpl extends ServiceImpl<BInMapper, BInEntity> implements IBInService {

    @Autowired
    private BInMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInVo> startInsert(BInVo searchCondition) {
        return insert(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BInVo> insert(BInVo searchCondition) {
        try {
            // 校验业务逻辑
            CheckResultAo checkResultAo = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
            if (!checkResultAo.isSuccess()) {
                throw new BusinessException(checkResultAo.getMessage());
            }

            // 转换VO为Entity
            BInEntity entity = new BInEntity();
            BeanUtils.copyProperties(searchCondition, entity);

            // 保存主表数据
            if (this.save(entity)) {
                searchCondition.setId(entity.getId());
                return InsertResultUtil.OK(searchCondition);
            } else {
                throw new BusinessException("新增失败");
            }
        } catch (Exception e) {
            log.error("入库单新增失败", e);
            throw new BusinessException("新增失败：" + e.getMessage());
        }
    }

    @Override
    public UpdateResultAo<BInVo> startUpdate(BInVo searchCondition) {
        return update(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BInVo> update(BInVo searchCondition) {
        try {
            // 校验业务逻辑
            CheckResultAo checkResultAo = checkLogic(searchCondition, CheckResultAo.UPDATE_CHECK_TYPE);
            if (!checkResultAo.isSuccess()) {
                throw new BusinessException(checkResultAo.getMessage());
            }

            // 转换VO为Entity
            BInEntity entity = new BInEntity();
            BeanUtils.copyProperties(searchCondition, entity);

            // 更新主表数据
            if (this.updateById(entity)) {
                return UpdateResultUtil.OK(searchCondition);
            } else {
                throw new BusinessException("更新失败");
            }
        } catch (Exception e) {
            log.error("入库单更新失败", e);
            throw new BusinessException("更新失败：" + e.getMessage());
        }
    }

    @Override
    public IPage<BInVo> selectPage(BInVo searchCondition) {
        Page<BInVo> page = new Page<>(searchCondition.getCurrent(), searchCondition.getSize());
        return mapper.selectPage(page, searchCondition);
    }

    @Override
    public BInVo selectById(Integer id) {
        if (id == null) {
            return null;
        }
        return mapper.selectById(id);
    }

    @Override
    public CheckResultAo checkLogic(BInVo bean, String checkType) {
        // 基础校验
        if (bean == null) {
            return CheckResultUtil.NG("入库单信息不能为空");
        }

        // 新增校验
        if (CheckResultAo.INSERT_CHECK_TYPE.equals(checkType)) {
            // 校验必填字段
            if (StringUtils.isEmpty(bean.getCode())) {
                return CheckResultUtil.NG("入库单号不能为空");
            }
            if (StringUtils.isEmpty(bean.getType())) {
                return CheckResultUtil.NG("入库类型不能为空");  
            }
            if (bean.getOwner_id() == null) {
                return CheckResultUtil.NG("货主不能为空");
            }
            if (bean.getSku_id() == null) {
                return CheckResultUtil.NG("物料不能为空");
            }
            if (bean.getWarehouse_id() == null) {
                return CheckResultUtil.NG("仓库不能为空");
            }
        }

        // 更新校验
        if (CheckResultAo.UPDATE_CHECK_TYPE.equals(checkType)) {
            if (bean.getId() == null) {
                return CheckResultUtil.NG("入库单ID不能为空");
            }
            if (bean.getDbversion() == null) {
                return CheckResultUtil.NG("数据版本不能为空");
            }
        }

        return CheckResultUtil.OK();
    }

    @Override
    public List<BInVo> selectExportList(BInVo param) {
        return mapper.selectExportList(param);
    }

    @Override
    public BInVo querySum(BInVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    @Override
    public BInVo getPrintInfo(BInVo searchCondition) {
        return selectById(searchCondition.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BInVo> searchCondition) {
        try {
            if (CollectionUtil.isEmpty(searchCondition)) {
                throw new BusinessException("删除数据不能为空");
            }

            List<Integer> ids = new ArrayList<>();
            for (BInVo vo : searchCondition) {
                if (vo.getId() != null) {
                    ids.add(vo.getId());
                }
            }

            if (CollectionUtil.isNotEmpty(ids)) {
                if (this.removeByIds(ids)) {
                    return DeleteResultUtil.OK(ids.size());
                }
            }
            throw new BusinessException("删除失败");
        } catch (Exception e) {
            log.error("入库单删除失败", e);
            throw new BusinessException("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BInVo bInVo) {
        List<BInVo> list = new ArrayList<>();
        list.add(bInVo);
        return delete(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BInVo searchCondition) {
        try {
            if (searchCondition.getId() == null) {
                throw new BusinessException("入库单ID不能为空");
            }

            BInEntity entity = new BInEntity();
            entity.setId(searchCondition.getId());
            entity.setStatus("5"); // 设置为作废状态
            entity.setDbversion(searchCondition.getDbversion());

            if (this.updateById(entity)) {
                return UpdateResultUtil.OK(1);
            } else {
                throw new BusinessException("作废失败");
            }
        } catch (Exception e) {
            log.error("入库单作废失败", e);
            throw new BusinessException("作废失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BInVo searchCondition) {
        try {
            if (searchCondition.getId() == null) {
                throw new BusinessException("入库单ID不能为空");
            }

            BInEntity entity = new BInEntity();
            entity.setId(searchCondition.getId());
            entity.setStatus("4"); // 设置为已入库状态
            entity.setDbversion(searchCondition.getDbversion());

            if (this.updateById(entity)) {
                return UpdateResultUtil.OK(1);
            } else {
                throw new BusinessException("完成失败");
            }
        } catch (Exception e) {
            log.error("入库单完成失败", e);
            throw new BusinessException("完成失败：" + e.getMessage());
        }
    }

    /**
     * check批次是否重复
     *
     * @param lot
     * @return
     */
    @Override
    public boolean isDuplicate(String lot) {
        if (org.apache.commons.lang.StringUtils.isEmpty(lot)) {
            return false;
        }
        Integer count = mapper.countLot(lot);
        if (count == null) {
            return false;
        }
        // 发生重复
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Override
    public BInEntity setBillInForUpdate(Integer id) {
        return mapper.setBillInForUpdate(id);
    }

    // ========== BPM回调方法 ==========

    @Override
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BInVo searchCondition) {
        // TODO: 实现BPM创建流程回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCallBackApprove(BInVo searchCondition) {
        // TODO: 实现BPM审批通过回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCallBackRefuse(BInVo searchCondition) {
        // TODO: 实现BPM审批拒绝回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCallBackCancel(BInVo searchCondition) {
        // TODO: 实现BPM审批取消回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCallBackSave(BInVo searchCondition) {
        // TODO: 实现BPM保存最新审批人回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BInVo searchCondition) {
        // TODO: 实现BPM作废创建流程回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BInVo searchCondition) {
        // TODO: 实现BPM作废审批通过回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BInVo searchCondition) {
        // TODO: 实现BPM作废审批拒绝回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BInVo searchCondition) {
        // TODO: 实现BPM作废审批取消回调
        return UpdateResultUtil.OK(1);
    }

    @Override
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BInVo searchCondition) {
        // TODO: 实现BPM作废保存最新审批人回调
        return UpdateResultUtil.OK(1);
    }
}
