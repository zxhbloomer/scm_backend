package com.xinyirun.scm.core.whapp.serviceimpl.sys.syscode;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppInsertResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppCheckResultUtil;
import com.xinyirun.scm.bean.app.result.utils.v1.AppInsertResultUtil;
import com.xinyirun.scm.bean.app.result.utils.v1.AppUpdateResultUtil;
import com.xinyirun.scm.bean.app.vo.sys.platform.syscode.AppSCodeVo;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.autocode.AutoCodeException;
import com.xinyirun.scm.common.utils.CodeGenerator;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.core.app.utils.mybatis.AppPageUtil;
import com.xinyirun.scm.core.whapp.mapper.sys.platform.WhAppSCodeMapper;
import com.xinyirun.scm.core.whapp.service.sys.syscode.WhAppISCodeService;
import com.xinyirun.scm.core.whapp.serviceimpl.base.v1.WhAppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Service
public class WhAppSCodeServiceImpl extends WhAppBaseServiceImpl<WhAppSCodeMapper, SCodeEntity> implements WhAppISCodeService {

    @Autowired
    private WhAppSCodeMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<AppSCodeVo> selectPage(AppSCodeVo searchCondition) {
        // 分页条件
        Page<SCodeEntity> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        AppPageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }
    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<AppSCodeVo> select(AppSCodeVo searchCondition) {
        // 查询 数据
        List<AppSCodeVo> list = mapper.select(searchCondition);
        return list;
    }


    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public AppSCodeVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppInsertResultAo<Integer> insert(SCodeEntity entity) {
        // 插入前check
        AppCheckResultAo cr = checkLogic(entity, AppCheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new AppBusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        return AppInsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppUpdateResultAo<Integer> update(SCodeEntity entity) {
        // 更新前check
        AppCheckResultAo cr = checkLogic(entity, AppCheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new AppBusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
        return AppUpdateResultUtil.OK(mapper.updateById(entity));
    }


    /**
     * 获取列表，查询所有数据
     *
     * @param type
     * @return
     */
    @Override
    public List<SCodeEntity> selectByType(String type, Long equal_id) {
        // 查询 数据
        List<SCodeEntity> rtn = mapper.selectByType(type, equal_id);
        return rtn;
    }

    /**
     * check逻辑
     *
     * @return
     */
    public AppCheckResultAo checkLogic(SCodeEntity entity, String moduleType) {

        switch (moduleType) {
            case AppCheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<SCodeEntity> typeList_insert = selectByType(entity.getType(), null);
                if (typeList_insert.size() >= 1) {
                    return AppCheckResultUtil.NG("新增保存出错：类型出现重复", typeList_insert);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 新增场合，不能重复
                List<SCodeEntity> typeList_update = selectByType(entity.getType(), entity.getId());
                // 更新场合，不能重复设置
                if (typeList_update.size() > 1) {
                    return AppCheckResultUtil.NG("新增保存出错：类型出现重复", typeList_update);
                }
                break;
            default:
        }
        return AppCheckResultUtil.OK();
    }


    /**
     * 获取编号
     * @param type
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppUpdateResultAo<SCodeEntity> createCode(String type) {
        try {
            // 获取数据 for update nowait;
            log.debug("获取数据 for update nowait");
            SCodeEntity entity = mapper.selectForUpdateWait(type);
            // 1、如果不存在，报错需要配置
            if(entity == null) {
                throw new AppBusinessException("自动生成编号发生错误，请在编码规则中维护规则");
            }
            // 获取编码方式
            log.debug("获取编码方式");
            createCodeProcess(entity);

            // 反写到数据库中
            mapper.updateById(entity);

            return AppUpdateResultUtil.OK(entity);
        } catch (Exception e) {
            log.debug("生成code出错");
            log.error("createCode error", e);
            throw new AppBusinessException("服务器繁忙，请稍后再试");
        }


    }

    /**
     * 生成编号
     * @param entity
     */
    private void createCodeProcess(SCodeEntity entity) {
        String code;
        String first;
        long days;
        String suffix;
        // 获取编码方式
        switch (entity.getRule()) {
            case DictConstant.DICT_SYS_CODE_RULE_TYPE_ONE:
                // YYYYMMDD??999
                // 获取系统实际 YYYYMMDD
                first = DateTimeUtil.dateTime();
                // 获取随机码两位
                String second_radomchar = CodeGenerator.randomAlphabet(2);
                // 自增编号
//                days = ChronoUnit.DAYS.between(entity.getU_time(), LocalDateTime.now());
                days = ChronoUnit.DAYS.between(LocalDateTimeUtils.getDayStart(entity.getU_time()), LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
                if(days >= 1) {
                    entity.setAuto_create((long)1);
                } else {
                    entity.setAuto_create((entity.getAuto_create() == null ? 0 : entity.getAuto_create()) + 1);
                }
                suffix = CodeGenerator.addLeftZeroForNum(3, entity.getAuto_create());
                // 设置更新时间和更新id
                entity.setU_time(LocalDateTime.now());
                if(ServletUtil.getUserSession() != null) {
                    entity.setU_id(((UserSessionBo) ServletUtil.getUserSession()).getAccountId());
                }
                // 合并并设置到entity
                entity.setCode(entity.getPrefex() == null ? "" : entity.getPrefex().toUpperCase() + first + second_radomchar + suffix);
                break;
            case DictConstant.DICT_SYS_CODE_RULE_TYPE_TWO:
                // P9999
                entity.setAuto_create((entity.getAuto_create() == null ? 0 : entity.getAuto_create()) + 1);
                code = String.format("%04d", entity.getAuto_create());
                entity.setCode("P" + code);
                break;
            case DictConstant.DICT_SYS_CODE_RULE_TYPE_THREE:
                // PC9999
                entity.setAuto_create((entity.getAuto_create() == null ? 0 : entity.getAuto_create()) + 1);
                code = String.format("%08d", entity.getAuto_create());
                entity.setCode("P" + code);
                break;
            case DictConstant.DICT_SYS_CODE_RULE_TYPE_FOUR:
                // YYYYMMDD9999
                // 获取系统实际 YYYYMMDD
                first = DateTimeUtil.dateTime();
                // 自增编号
//                days = ChronoUnit.DAYS.between(entity.getU_time(), LocalDateTime.now());
                days = ChronoUnit.DAYS.between(LocalDateTimeUtils.getDayStart(entity.getU_time()), LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
                if(days >= 1) {
                    entity.setAuto_create((long)1);
                } else {
                    entity.setAuto_create((entity.getAuto_create() == null ? 0 : entity.getAuto_create()) + 1);
                }
                suffix = CodeGenerator.addLeftZeroForNum(4, entity.getAuto_create());
                // 设置更新时间和更新id
                entity.setU_time(LocalDateTime.now());
                if(ServletUtil.getUserSession() != null) {
                    entity.setU_id(((UserSessionBo) ServletUtil.getUserSession()).getAccountId());
                }
                // 合并并设置到entity
                entity.setCode(entity.getPrefex() == null ? "" : entity.getPrefex().toUpperCase() + first + suffix);
                break;
            default:
                throw new AutoCodeException("自动编号出现异常！");
        }
    }
}
