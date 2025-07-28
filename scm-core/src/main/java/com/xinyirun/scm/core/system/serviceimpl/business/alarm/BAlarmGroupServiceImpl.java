package com.xinyirun.scm.core.system.serviceimpl.business.alarm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.business.alarm.BAlarmGroupEntity;
import com.xinyirun.scm.bean.entity.business.alarm.BAlarmGroupStaffEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmGroupVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;
import com.xinyirun.scm.core.system.mapper.business.alarm.BAlarmGroupMapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmGroupService;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmGroupStaffService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 仓库组一级分类 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@Service
public class BAlarmGroupServiceImpl extends ServiceImpl<BAlarmGroupMapper, BAlarmGroupEntity> implements IBAlarmGroupService {

    @Autowired
    private IBAlarmGroupStaffService groupStaffService;
    /**
     * 预警分组
     *
     * @param vo  查询参数
     * @return IPage<BAlarmGroupVo>
     */
    @Override
    public IPage<BAlarmGroupVo> selectPageList(BAlarmGroupVo vo) {
        Page<BAlarmGroupVo> page = new Page<>(vo.getPageCondition().getCurrent(), vo.getPageCondition().getSize());
        PageUtil.setSort(page, vo.getPageCondition().getSort());
        return baseMapper.selectPageList(page, vo);
    }

    @Override
    public InsertResultAo<BAlarmGroupVo> insert(BAlarmGroupVo vo) {
        BAlarmGroupEntity entity = new BAlarmGroupEntity();
        insertEntity(vo, entity);
        baseMapper.insert(entity);
        BAlarmGroupVo result = baseMapper.selectVoById(entity.getId());
        return InsertResultUtil.OK(result);
    }

    /**
     * 更新
     *
     * @param vo
     * @return
     */
    @Override
    public UpdateResultAo<BAlarmGroupVo> updateAlarm(BAlarmGroupVo vo) {
        Assert.notNull(vo.getId(), "ID不能为空");
        BAlarmGroupEntity entity = baseMapper.selectById(vo.getId());
        insertEntity(vo, entity);
        baseMapper.updateById(entity);
        BAlarmGroupVo result = baseMapper.selectVoById(entity.getId());
        return UpdateResultUtil.OK(result);
    }

    /**
     * 添加员工 穿梭框
     *
     * @param bean
     * @return
     */
    @Override
    public BAlarmStaffGroupTransferVo getStaffTransferList(BAlarmStaffTransferVo bean) {
        BAlarmStaffGroupTransferVo rtn = new BAlarmStaffGroupTransferVo();
        rtn.setStaff_all(baseMapper.getAllStaffTransferList());
        // 查询已添加的员工
        List<Long> rtnList = baseMapper.getUsedStaffTransferList(bean.getGroup_id());
        rtn.setStaff_alarm(rtnList.toArray(new Long[rtnList.size()]));
        return rtn;
    }

    /**
     * 新增员工
     *
     * @param bean
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BAlarmStaffGroupTransferVo setStaffTransfer(BAlarmStaffTransferVo bean) {
        // 组 ID 不能 为空
        Assert.notNull(bean.getGroup_id(), "ID不能为空");
        // 删除当前组下的所有员工
        groupStaffService.remove(new LambdaQueryWrapper<BAlarmGroupStaffEntity>().eq(BAlarmGroupStaffEntity::getAlarm_group_id, bean.getGroup_id()));
        // 新增
        List<BAlarmGroupStaffEntity> insertList = Arrays.stream(bean.getStaff_alarm()).map(item -> {
            BAlarmGroupStaffEntity entity = new BAlarmGroupStaffEntity();
            entity.setAlarm_group_id(bean.getGroup_id());
            entity.setStaff_id(item);
            return entity;
        }).collect(Collectors.toList());
        groupStaffService.saveBatch(insertList);

        // 默认添加组规则, 都是事件预警


        BAlarmStaffGroupTransferVo bAlarmStaffGroupTransferVo = new BAlarmStaffGroupTransferVo();
        bAlarmStaffGroupTransferVo.setStaff_alarm_count(insertList.size());
        return bAlarmStaffGroupTransferVo;
    }

    /**
     * 实体类赋值
     * @param vo 入参
     * @param entity 实体类
     */
    private void insertEntity(BAlarmGroupVo vo, BAlarmGroupEntity entity) {
        Assert.hasText(vo.getName(), "名称不能为空");
        Assert.hasText(vo.getCode(), "编码不能为空");
        entity.setCode(vo.getCode());
        entity.setName(vo.getName());
        entity.setShort_name(vo.getShort_name());
        entity.setNamePinyin(Pinyin.toPinyin(vo.getName(), ""));
        entity.setShort_name_pinyin(Pinyin.toPinyin(vo.getShort_name(), ""));
        // 获取拼音首字母
        entity.setName_pinyin_abbr(getPinYinAbbr(vo.getName()));
        entity.setShort_name_pinyin_abbr(getPinYinAbbr(vo.getShort_name()));
    }

    /**
     * 获取拼音简称
     * @param name
     * @return
     */
    private String getPinYinAbbr(String name) {
        StringBuilder str = new StringBuilder("");
        for (char c: name.toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        return str.toString();
    }
}
