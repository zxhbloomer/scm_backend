package com.xinyirun.scm.core.system.serviceimpl.business.alarm;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.alarm.BAlarmRulesEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmRulesVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.alarm.BAlarmRulesMapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 预警规则清单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@Service
public class BAlarmRulesServiceImpl extends ServiceImpl<BAlarmRulesMapper, BAlarmRulesEntity> implements IBAlarmRulesService {

    @Autowired
    private BAlarmRulesMapper mapper;

    /**
     * 查询事件(同步)预警原则的人员id
     * @param type 预警类型 0事件预警
     * @return
     */
    @Override
    public List<BAlarmRulesBo> selectStaffAlarm(String type) {
        List<BAlarmRulesBo> result = new ArrayList<>();
        result = mapper.selectAlarmRulesByType(type);
        return result;
    }

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    @Override
    public IPage<BAlarmRulesVo> selectPageList(BAlarmRulesVo param) {
        // 分页条件
        Page<BAlarmRulesVo> page = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(page, param.getPageCondition().getSort());
        // 查询入库计划page
        IPage<BAlarmRulesVo> result = mapper.selectPageList(page, param);
        result.getRecords().stream().forEach(item -> {
            List<JSONObject> groupList = item.getGroup_list().stream().filter(item1 -> null != item1.get("name")).collect(Collectors.toList());
            List<JSONObject> staffList = item.getStaff_list().stream().filter(item1 -> null != item1.get("name")).collect(Collectors.toList());
            item.setGroup_list(groupList);
            item.setStaff_list(staffList);
        });
        return result;
    }

    /**
     * 新增
     *
     * @param vo
     * @return
     */
    @Override
    public InsertResultAo<BAlarmRulesVo> insert(BAlarmRulesVo vo) {
        // 执行新增
        BAlarmRulesEntity entity = (BAlarmRulesEntity)BeanUtilsSupport.copyProperties(vo, BAlarmRulesEntity.class);
        // 默认可用
        entity.setIs_using(DictConstant.DICT_B_ALARM_RULES_IS_USING_TYPE_1);
        mapper.insert(entity);

        // 返回新增数据
        BAlarmRulesVo rulesVo = selectVoById(entity.getId());
        return InsertResultUtil.OK(rulesVo);
    }

    /**
     * 更新
     *
     * @param vo
     * @return
     */
    @Override
    public UpdateResultAo<BAlarmRulesVo> edit(BAlarmRulesVo vo) {
        Assert.notNull(vo.getId(), "ID 不能为空");
        // 执行新增
        BAlarmRulesEntity entity = (BAlarmRulesEntity)BeanUtilsSupport.copyProperties(vo, BAlarmRulesEntity.class);
        mapper.updateById(entity);

        // 返回新增数据
        BAlarmRulesVo rulesVo = selectVoById(entity.getId());
        return UpdateResultUtil.OK(rulesVo);
    }

    /**
     * 启用, 禁用
     *
     * @param vo
     */
    @Override
    public void enable(List<BAlarmRulesVo> vo) {
        Assert.notEmpty(vo, "集合 不能为空");

        for (BAlarmRulesVo rulesVo : vo) {
            // 执行更新
//            String is_using = DictConstant.DICT_B_ALARM_RULES_IS_USING_TYPE_0.equals(rulesVo.getIs_using()) ? DictConstant.DICT_B_ALARM_RULES_IS_USING_TYPE_1 : DictConstant.DICT_B_ALARM_RULES_IS_USING_TYPE_0;
            LambdaUpdateWrapper<BAlarmRulesEntity> wrapper = new LambdaUpdateWrapper<BAlarmRulesEntity>()
                    .eq(BAlarmRulesEntity::getId, rulesVo.getId())
                    .set(BAlarmRulesEntity::getIs_using, rulesVo.getIs_using());
            baseMapper.update(null, wrapper);
        }

    }


    /**
     * 根据 id 查询 vo
     * @param id
     * @return
     */
    private BAlarmRulesVo selectVoById(Integer id) {
        return mapper.selectVoById(id);
    }


}
