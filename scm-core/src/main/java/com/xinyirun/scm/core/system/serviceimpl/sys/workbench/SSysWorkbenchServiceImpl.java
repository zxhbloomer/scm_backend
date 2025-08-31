package com.xinyirun.scm.core.system.serviceimpl.sys.workbench;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.workbench.SSysWorkbenchEntity;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmMatterVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmNoticeVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmRemindVo;
import com.xinyirun.scm.bean.system.vo.workbench.SSysWorkbenchVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.workbench.SSysWorkbenchMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.workbench.ISSysWorkbenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.xinyirun.scm.common.utils.redis.RedisLockUtil;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-17
 */
@Service
@Slf4j
public class SSysWorkbenchServiceImpl extends ServiceImpl<SSysWorkbenchMapper, SSysWorkbenchEntity> implements ISSysWorkbenchService {

    @Autowired
    SSysWorkbenchMapper mapper;

    @Autowired
    ISConfigService sConfigService;

    /**
     * 工作台配置-获取
     * @param searchCondition
     * @return
     */
    @Override
    public SSysWorkbenchVo getInfo(SSysWorkbenchVo searchCondition) {
        /**
         * 查询code=0001的根数据
         */
        if(searchCondition==null){
            searchCondition = new SSysWorkbenchVo();
        }
        searchCondition.setCode("0001");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        // 构建分布式锁key
        String lockKey = "workbench:init:" + searchCondition.getStaff_id() + ":" + searchCondition.getCode();
        String requestId = UUID.randomUUID().toString();
        boolean lockSuccess = RedisLockUtil.tryGetDistributedLock(lockKey, requestId, 30, 3L, 1000L);
        
        if (!lockSuccess) {
            log.warn("获取分布式锁失败，可能存在并发操作: staff_id={}, code={}", searchCondition.getStaff_id(), searchCondition.getCode());
            // 等待后再次查询，避免返回null
            try {
                Thread.sleep(200);
                return mapper.getDataByCode(searchCondition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        try {
            /**
             * 双重检查：获得锁后再次查询，避免重复插入
             */
            SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);
            if (vo == null) {
                SConfigEntity _data = sConfigService.selectByKey(SystemConstants.WORK_BENCH_LAYOUT_DEFAULT);

                SSysWorkbenchEntity entity = new SSysWorkbenchEntity();
                entity.setCode("0001");
                entity.setStaff_id(SecurityUtil.getStaff_id());
                entity.setConfig(_data.getValue());
                mapper.insert(entity);

                vo = (SSysWorkbenchVo) BeanUtilsSupport.copyProperties(entity, SSysWorkbenchVo.class);
                log.info("成功初始化工作台布局配置: staff_id={}, code={}", searchCondition.getStaff_id(), searchCondition.getCode());
            }
            return vo;
        } finally {
            RedisLockUtil.releaseDistributedLock(lockKey, requestId);
        }
    }

    /**
     * 工作台配置-初始化
     * @param searchCondition
     * @return
     */
    @Override
    public SSysWorkbenchVo resetInfo(SSysWorkbenchVo searchCondition){
        /**
         * 查询code=0001的根数据
         */
        if(searchCondition==null){
            searchCondition = new SSysWorkbenchVo();
        }
        searchCondition.setCode("0001");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);

        SConfigEntity _data = sConfigService.selectByKey(SystemConstants.WORK_BENCH_LAYOUT_DEFAULT);

        /**
         * 设置返回值
         */
        vo.setConfig(_data.getValue());
        return vo;
    }


    /**
     * 工作台配置-保存
     * @param searchCondition
     * @return
     */
    @Override
    public SSysWorkbenchVo saveInfo(SSysWorkbenchVo searchCondition){
        /**
         * 先查询code=0001的根数据，获取数据后，更新config
         */
        searchCondition.setCode("0001");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);

        SSysWorkbenchEntity entity = (SSysWorkbenchEntity) BeanUtilsSupport.copyProperties(vo, SSysWorkbenchEntity.class);
        entity.setConfig(searchCondition.getConfig());

        // 保存数据
        mapper.updateById(entity);
        /**
         * 设置返回值
         */
        vo.setConfig(searchCondition.getConfig());
        return vo;
    }

    /**
     * 工作台配置-快捷操作配置-保存
     * @param searchCondition
     * @return
     */
    @Override
    public SSysWorkbenchVo saveQuick(SSysWorkbenchVo searchCondition){
        /**
         * 先查询code=0002的根数据，获取数据后，更新config
         */
        searchCondition.setCode("0002");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);

        SSysWorkbenchEntity entity = (SSysWorkbenchEntity) BeanUtilsSupport.copyProperties(vo, SSysWorkbenchEntity.class);
        entity.setConfig(searchCondition.getConfig());

        // 保存数据
        mapper.updateById(entity);
        /**
         * 设置返回值
         */
        vo.setConfig(searchCondition.getConfig());
        return vo;
    }

    /**
     * 工作台配置-常用应用配置-保存
     * @param searchCondition
     * @return
     */
    @Override
    public SSysWorkbenchVo saveOfften(SSysWorkbenchVo searchCondition) {
        /**
         * 先查询code=0003的根数据，获取数据后，更新config
         */
        searchCondition.setCode("0003");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);

        SSysWorkbenchEntity entity = (SSysWorkbenchEntity) BeanUtilsSupport.copyProperties(vo, SSysWorkbenchEntity.class);
        entity.setConfig(searchCondition.getConfig());

        // 保存数据
        mapper.updateById(entity);
        /**
         * 设置返回值
         */
        vo.setConfig(searchCondition.getConfig());
        return vo;
    }

    /**
     * 工作台配置-快捷操作配置
     * @param searchCondition
     * @return
     */
    public SSysWorkbenchVo getQuickOperation(SSysWorkbenchVo searchCondition) {
        /**
         * 查询code=0002的根数据
         */
        if(searchCondition==null){
            searchCondition = new SSysWorkbenchVo();
        }
        searchCondition.setCode("0002");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        // 构建分布式锁key
        String lockKey = "workbench:init:" + searchCondition.getStaff_id() + ":" + searchCondition.getCode();
        String requestId = UUID.randomUUID().toString();
        boolean lockSuccess = RedisLockUtil.tryGetDistributedLock(lockKey, requestId, 30, 3L, 1000L);
        
        if (!lockSuccess) {
            log.warn("获取分布式锁失败，可能存在并发操作: staff_id={}, code={}", searchCondition.getStaff_id(), searchCondition.getCode());
            // 等待后再次查询，避免返回null
            try {
                Thread.sleep(200);
                return mapper.getDataByCode(searchCondition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        try {
            /**
             * 双重检查：获得锁后再次查询，避免重复插入
             */
            SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);
            if (vo == null) {
                SConfigEntity _data = sConfigService.selectByKey(SystemConstants.WORK_BENCH_LAYOUT_DEFAULT);

                SSysWorkbenchEntity entity = new SSysWorkbenchEntity();
                entity.setCode("0002");
                entity.setStaff_id(SecurityUtil.getStaff_id());
                entity.setConfig(_data.getExtra1());
                mapper.insert(entity);

                vo = (SSysWorkbenchVo) BeanUtilsSupport.copyProperties(entity, SSysWorkbenchVo.class);
                log.info("成功初始化快捷操作配置: staff_id={}, code={}", searchCondition.getStaff_id(), searchCondition.getCode());
            }
            return vo;
        } finally {
            RedisLockUtil.releaseDistributedLock(lockKey, requestId);
        }
    }

    /**
     * 工作台配置-常用应用
     * @param searchCondition
     * @return
     */
    public SSysWorkbenchVo getOfftenOperation(SSysWorkbenchVo searchCondition){
        /**
         * 查询code=0003的根数据
         */
        if(searchCondition==null){
            searchCondition = new SSysWorkbenchVo();
        }
        searchCondition.setCode("0003");
        /**
         * 设置用户id查询条件
         */
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        // 构建分布式锁key
        String lockKey = "workbench:init:" + searchCondition.getStaff_id() + ":" + searchCondition.getCode();
        String requestId = UUID.randomUUID().toString();
        boolean lockSuccess = RedisLockUtil.tryGetDistributedLock(lockKey, requestId, 30, 3L, 1000L);
        
        if (!lockSuccess) {
            log.warn("获取分布式锁失败，可能存在并发操作: staff_id={}, code={}", searchCondition.getStaff_id(), searchCondition.getCode());
            // 等待后再次查询，避免返回null
            try {
                Thread.sleep(200);
                return mapper.getDataByCode(searchCondition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        try {
            /**
             * 双重检查：获得锁后再次查询，避免重复插入
             */
            SSysWorkbenchVo vo = mapper.getDataByCode(searchCondition);
            if (vo == null) {
                SConfigEntity _data = sConfigService.selectByKey(SystemConstants.WORK_BENCH_LAYOUT_DEFAULT);

                SSysWorkbenchEntity entity = new SSysWorkbenchEntity();
                entity.setCode("0003");
                entity.setStaff_id(SecurityUtil.getStaff_id());
                entity.setConfig(_data.getExtra2());
                mapper.insert(entity);

                vo = (SSysWorkbenchVo) BeanUtilsSupport.copyProperties(entity, SSysWorkbenchVo.class);
                log.info("成功初始化常用应用配置: staff_id={}, code={}", searchCondition.getStaff_id(), searchCondition.getCode());
            }
            return vo;
        } finally {
            RedisLockUtil.releaseDistributedLock(lockKey, requestId);
        }
    }

    /**
     * 获取事项数据
     * @return
     */
    public BpmMatterVo getMatterData() {
        BpmMatterVo bpmMatterVo = new BpmMatterVo();
        bpmMatterVo.setStaffCode(SecurityUtil.getStaff_code());
        BpmMatterVo vo = mapper.getMatterData(bpmMatterVo);
        return vo;
    }

    /**
     * 获取待办超时提醒
     * @return
     */
    @Override
    public BpmRemindVo getRemindData() {
        BpmRemindVo bpmRemindVo = new BpmRemindVo();
        bpmRemindVo.setStaffCode(SecurityUtil.getStaff_code());
        BpmRemindVo vo = mapper.getRemindData(bpmRemindVo);
        if (vo.getPendingQty()==null || vo.getPendingQty() == 0) {
            vo.setPendingQty(0);
        } else {
            vo.setOverOneDaypercentage(vo.getOverOneDay() * 100 / vo.getPendingQty());
            vo.setOverTwoDaypercentage(vo.getOverTwoDay() * 100 / vo.getPendingQty());
            vo.setOverThreeDaypercentage(vo.getOverThreeDay() * 100 / vo.getPendingQty());
            vo.setOverOneWeekpercentage(vo.getOverOneWeek() * 100 / vo.getPendingQty());
        }

        return vo;
    }

    /**
     * 获取通知list
     * @return
     */
    public BpmNoticeVo getNoticeList() {
        BpmNoticeVo bpmNoticeVo = new BpmNoticeVo();
        bpmNoticeVo.setStaffCode(SecurityUtil.getStaff_code());

        BNoticeVo queryCondition = new BNoticeVo();
        queryCondition.setStaff_id(SecurityUtil.getStaff_id());
        queryCondition.setStatus("1");
        // 获取全部
        List<BNoticeVo> noticeListAll = mapper.getNoticeListAll(queryCondition);
        bpmNoticeVo.setNoticeListAll(noticeListAll);
        // 获取系统消息
        queryCondition.setType("1");
        List<BNoticeVo> noticeListSystem = mapper.getNoticeListAll(queryCondition);
        bpmNoticeVo.setNoticeListSystem(noticeListSystem);
        // 获取个人消息
        queryCondition.setType("0");
        List<BNoticeVo> noticeListPersonal = mapper.getNoticeListAll(queryCondition);
        bpmNoticeVo.setNoticeListPersonal(noticeListPersonal);

        return bpmNoticeVo;
    }
}
