package com.xinyirun.scm.core.system.serviceimpl.business.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.message.BMessageEntity;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.bean.system.vo.business.message.BMessageVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.mapper.business.message.BMessageMapper;
import com.xinyirun.scm.core.system.service.business.message.IBMessageService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * websocket 消息通知表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-22
 */
@Service
public class BMessageServiceImpl extends ServiceImpl<BMessageMapper, BMessageEntity> implements IBMessageService {

    @Autowired
    private BMessageMapper mapper;

    /**
     * 新增
     *
     * @param list        新增的serial_id 和 serial_code信息
     * @param type        0待办, 1预警, 2通知
     * @param serial_type b_monitor
     */
    @Override
    public void insert(List<BMessageBo> list, String type, String serial_type) {
        List<BMessageEntity> entityList = entityBuilder(list, type, serial_type);
        this.saveBatch(entityList);
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    public IPage<BMessageVo> selectHeaderPageList(BMessageVo param) {
        param.setStaff_id(Objects.requireNonNull(SecurityUtil.getStaff_id()).intValue());
        // 分页条件
        Page<BMonitorEntity> pageCondition = new Page<>(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, "-c_time");
        return mapper.selectPageList(pageCondition, param);
    }

    /**
     * 查询数量
     *
     * @param param
     * @return
     */
    @Override
    public BMessageVo getHeaderCount(BMessageVo param) {
        BMessageVo result = new BMessageVo();
        if(param == null) {
            param = new BMessageVo();
        }
        param.setStaff_id(Objects.requireNonNull(SecurityUtil.getStaff_id()).intValue());
        List<BMessageVo> bMessageVos = mapper.selectCountBySerialType(param);
        if (CollectionUtils.isEmpty(bMessageVos)) return result;
        for (BMessageVo bMessageVo : bMessageVos) {
            result.setSerial_type(bMessageVo.getSerial_type());
            if (DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR.equals(bMessageVo.getSerial_type()))
                result.setMonitorCount(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_B_IN.equals(bMessageVo.getSerial_type()))
                result.setInCount(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN.equals(bMessageVo.getSerial_type()))
                result.setInPlanCount(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN.equals(bMessageVo.getSerial_type()))
                result.setOutPlanCount(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_B_OUT.equals(bMessageVo.getSerial_type()))
                result.setOutCount(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_SYNC.equals(bMessageVo.getSerial_type()))
                result.setMonitorSyncErrorCount(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED.equals(bMessageVo.getSerial_type()))
                result.setMonitor_unaudited(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_M_INVENTORY_STAGNATION.equals(bMessageVo.getSerial_type()))
                result.setInventory_stagnation_warning(bMessageVo.getCount());
            else if (DictConstant.DICT_SYS_CODE_TYPE_M_MONITOR_LOSS.equals(bMessageVo.getSerial_type()))
                result.setMonitor_loss_warning(bMessageVo.getCount());
        }
        result.setWarning_count(result.getInCount() + result.getInPlanCount() + result.getOutPlanCount() + result.getOutCount() + result.getMonitorSyncErrorCount() + result.getMonitor_unaudited() + result.getInventory_stagnation_warning() + result.getMonitor_loss_warning());
        return result;
    }

    /**
     * 删除 notice 信息
     *
     * @param serialId
     * @param serialCode
     * @param serialType
     */
    @Override
    public void deleteNotice(Integer serialId, String serialCode, String serialType) {
        mapper.delete(new LambdaQueryWrapper<BMessageEntity>()
                .eq(BMessageEntity::getSerial_id, serialId)
                .eq(BMessageEntity::getSerial_code, serialCode)
                .eq(BMessageEntity::getSerial_type, serialType));
    }

    /**
     * 删除 notice 信息
     *
     * @param deleteList
     */
    @Override
    public void deleteNoticeList(List<BMessageBo> deleteList) {
        if (CollectionUtils.isEmpty(deleteList)) return;
        deleteList.forEach(item -> deleteNotice(item.getSerial_id(), item.getSerial_code(), item.getSerial_type()));
    }

    /**
     * 新增
     *
     * @param list        新增的serial_id 和 serial_code信息
     * @param type        0待办, 1预警, 2通知
     * @param serial_type b_monitor
     * @return
     */
    private List<BMessageEntity> entityBuilder(List<BMessageBo> list, String type, String serial_type) {
        List<BMessageEntity> result = new ArrayList<>();
        for (BMessageBo bMessageBo : list) {
            String msg = concatMsg(bMessageBo, serial_type);
            BMessageEntity entity = new BMessageEntity();
            entity.setStatus("0");
            entity.setType(type);
            // 如果是b_monitor, 判断一下, 如果status是7, 就是未审核预警
            if (DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR.equals(serial_type)
                    && DictConstant.DICT_B_MONITOR_STATUS_SEVEN.equals(bMessageBo.getStatus())) {
                entity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED);
            } else {
                entity.setSerial_type(serial_type);
            }
            entity.setSerial_id(bMessageBo.getSerial_id());
            entity.setSerial_code(bMessageBo.getSerial_code());
            entity.setSerial_status(bMessageBo.getStatus());
            entity.setAlarm_rules_type(DictConstant.DICT_B_ALARM_RULES_TYPE_0);
            entity.setMsg(msg);
            entity.setLabel(bMessageBo.getLabel());
            result.add(entity);
        }
        return result;
    }

    private String concatMsg(BMessageBo bMessageBo, String serial_type) {
        StringBuilder sb = new StringBuilder("");
        switch (serial_type) {
            case DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR:
                sb.append("监管任务（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）目前在");
                sb.append(bMessageBo.getStatus_name());
                sb.append("环节，已经超过");
                if (DictConstant.DICT_B_MONITOR_STATUS_FOUR.equals(bMessageBo.getStatus())) {
                    sb.append("5天未处理");
                } else if (DictConstant.DICT_B_MONITOR_STATUS_SEVEN.equals(bMessageBo.getStatus())) {
                    sb.append("72小时未审核");
                } else {
                    sb.append("24小时未处理");
                }
//                sb.append("未处理");
                bMessageBo.setLabel("监管任务");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_IN:
                sb.append("入库单（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("入库单");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY:
                sb.append("提货单（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("提货单");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN:
                sb.append("入库计划（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("入库计划");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT:
                sb.append("出库单（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("出库单");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE:
                sb.append("收货单（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("收货单");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN:
                sb.append("出库计划（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("出库计划");
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_SYNC:
                sb.append("监管任务（");
                sb.append(bMessageBo.getSerial_code());
                sb.append("）同步失败");
                bMessageBo.setLabel("监管任务同步");
                break;
            //港口停滞仓库预警
            case DictConstant.DICT_SYS_CODE_TYPE_M_INVENTORY_STAGNATION:
                sb.append(bMessageBo.getWarehouse_name());
                sb.append("仓库有");
                sb.append(bMessageBo.getQty_avaible());
                sb.append("吨商品（规格:");
                sb.append(bMessageBo.getSpec());
                sb.append("）已中转停滞，请及时处理");
                bMessageBo.setLabel("港口停滞");
                break;
            //监管任务损耗预警
            case DictConstant.DICT_SYS_CODE_TYPE_M_MONITOR_LOSS:
                sb.append("监管任务(");
                sb.append(bMessageBo.getSerial_code());
                sb.append(")损耗百分比已超过");
                sb.append(bMessageBo.getM_monitor_loss_time());
                sb.append("%");
                bMessageBo.setLabel("监管损耗");
                break;
        }
        return sb.toString();
    }
}
