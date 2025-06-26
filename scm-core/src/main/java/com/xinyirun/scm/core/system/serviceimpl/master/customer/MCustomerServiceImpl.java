package com.xinyirun.scm.core.system.serviceimpl.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.excel.customer.MCustomerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorMapper;
import com.xinyirun.scm.core.system.mapper.master.customer.MCustomerMapper;
import com.xinyirun.scm.core.system.service.master.customer.IMCustomerService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
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
public class MCustomerServiceImpl extends BaseServiceImpl<MCustomerMapper, MCustomerEntity> implements IMCustomerService {

    @Autowired
    private MCustomerMapper mapper;

    @Autowired
    private BMonitorMapper bMonitorMapper;

    @Override
    public IPage<MCustomerVo> selectPage(MCustomerVo searchCondition) {
        // 分页条件
        Page<MCustomerEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MCustomerVo> selectList(MCustomerVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 新增客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MCustomerVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MCustomerEntity entity = (MCustomerEntity) BeanUtilsSupport.copyProperties(vo, MCustomerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());
        entity.setEnable(Boolean.TRUE);

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MCustomerVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        MCustomerEntity entity = (MCustomerEntity) BeanUtilsSupport.copyProperties(vo, MCustomerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public MCustomerVo selectByCreditNo(String credit_no) {
        return mapper.selectByCreditNo(credit_no);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MCustomerVo> searchCondition) {
        List<MCustomerEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCustomerEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MCustomerVo> searchCondition) {
        List<MCustomerEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCustomerEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MCustomerVo> searchCondition) {
        List<MCustomerEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCustomerEntity entity : list) {
            if (entity.getEnable() == Boolean.FALSE) {
                MCustomerVo customerVo = (MCustomerVo) BeanUtilsSupport.copyProperties(entity, MCustomerVo.class);
                CheckResultAo cr = checkLogic(customerVo, CheckResultAo.ENABLE_CHECK_TYPE);
                if (!cr.isSuccess()) {
                    throw new BusinessException(cr.getMessage());
                }
                entity.setEnable(Boolean.TRUE);
            } else {
                List<BMonitorEntity> selectMonitor = bMonitorMapper.selectByCustomerId(entity.getId());
                if (selectMonitor.size() > 0) {
                    throw new BusinessException("删除出错：该客户信息被监管任务使用中");
                }

                entity.setEnable(Boolean.FALSE);
            }
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MCustomerVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 导出
     *
     * @param searchConditionList 参数
     * @return List<MCustomerExcelVo>
     */
    @Override
    public List<MCustomerExcelVo> export(MCustomerVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }


    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(MCustomerVo vo, String moduleType) {
        List<MCustomerEntity> selectByName = mapper.selectByName(vo);
        List<MCustomerEntity> selectByKey = mapper.selectByCode(vo);
        List<MCustomerEntity> selectByCreditNo = mapper.selectByCreditCode(vo);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByKey.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", vo.getCode());
                }
                if (selectByCreditNo.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：企业信用代码出现重复", vo.getCredit_no());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByKey.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", vo.getCode());
                }
                if (selectByCreditNo.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：企业信用代码出现重复", vo.getCredit_no());
                }
                break;
            case CheckResultAo.ENABLE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("启用出错：名称出现重复", vo.getName());
                }
                if (selectByKey.size() > 0) {
                    return CheckResultUtil.NG("启用出错：编码出现重复", vo.getCode());
                }
                if (selectByCreditNo.size() > 0) {
                    return CheckResultUtil.NG("启用出错：企业信用代码出现重复", vo.getCredit_no());
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

}
