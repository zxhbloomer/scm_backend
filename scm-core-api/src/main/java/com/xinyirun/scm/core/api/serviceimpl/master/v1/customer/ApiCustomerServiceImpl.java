package com.xinyirun.scm.core.api.serviceimpl.master.v1.customer;

import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiCustomerMapper;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiOwnerMapper;
import com.xinyirun.scm.core.api.service.master.v1.customer.ApiCustomerService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class ApiCustomerServiceImpl extends BaseServiceImpl<ApiCustomerMapper, MCustomerEntity> implements ApiCustomerService {
    @Autowired
    private ApiCustomerMapper mapper;

    @Autowired
    private ApiOwnerMapper apiOwnerMapper;

    /**
     * 首次所有数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiCustomerVo> voList){
        saveData(voList);
    }

    /**
     * 新增同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncNewOnly(List<ApiCustomerVo> voList){
        saveData(voList);
    }

    /**
     * 修改同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncUpdateOnly(List<ApiCustomerVo> voList){
        saveData(voList);
    }

    /**
     * 同步的客户数据保存更新
     */
    private void saveData(List<ApiCustomerVo> voList) {
        List<MCustomerEntity> list = new ArrayList<>();
        checkList(voList);
        for(ApiCustomerVo vo:voList){
            // check逻辑
            checkSync(vo);

            vo.setC_time(LocalDateTime.now());
            // 名称全拼
            vo.setName_pinyin(Pinyin.toPinyin(vo.getName(), ""));
            // 名称简拼
            StringBuilder str = new StringBuilder("");
            for (char c: vo.getName().toCharArray()) {
                str.append(Pinyin.toPinyin(c).substring(0,1));
            }
            vo.setShort_name_pinyin(str.toString());

            MCustomerEntity customerEntity = (MCustomerEntity) BeanUtilsSupport.copyProperties(vo, MCustomerEntity.class);
            MOwnerEntity ownerEntity = (MOwnerEntity) BeanUtilsSupport.copyProperties(vo, MOwnerEntity.class);
            customerEntity.setEnable(Boolean.TRUE);
            ownerEntity.setEnable(Boolean.TRUE);

            // 按编码和来源 去货主表查询是否有数据
            MOwnerEntity checkOwnerEntity = apiOwnerMapper.selectByCreditNo(vo);
            // 按编码和来源 去客户表查询是否有数据
            MCustomerEntity entity = mapper.selectByCreditNo(vo);

            // 客户新增修改逻辑
            if(entity != null ){
                // 如果对象不为空则进行更新，为空则进行保存
                customerEntity.setId(entity.getId());
            }
            // 根据类型判断，是否内部企业同步货主数据
            if(SystemConstants.API_INTERIOR_ENTERPRISE_TYPE.equals(vo.getType())) {
                // 货主新增修改操作
                if(checkOwnerEntity != null) {
                    ownerEntity.setId(checkOwnerEntity.getId());
                    apiOwnerMapper.updateById(ownerEntity);
                }else{
                    apiOwnerMapper.insert(ownerEntity);
                }
            } else {
                if (checkOwnerEntity != null) {
                    throw new ApiBusinessException("同步的企业不能既是主体企业又是客户:"+vo.getName());
                }
            }
            list.add(customerEntity);
        }
        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 同步check逻辑
     */
    public void checkSync(ApiCustomerVo vo) {
        if (StringUtils.isEmpty(vo.getName())) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_NAME_NULL);
        }
        if (StringUtils.isEmpty(vo.getCode())) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_CODE_NULL);
        }
        if (StringUtils.isEmpty(vo.getCredit_no())) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_CREDIT_CODE_NULL);
        }
        if (StringUtils.isEmpty(vo.getShort_name())) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_SHORT_NAME_NULL);
        }

        if (StringUtils.isEmpty(vo.getType())) {
            throw new ApiBusinessException(ApiResultEnum.CUSTOMER_TYPE_NULL);
        }

        List<MCustomerEntity> mCustomerEntityList = mapper.selectListByName(vo.getName());
//        List<MOwnerEntity> mOwnerEntityList = apiOwnerMapper.selectListByName(vo.getName());
//        if (mOwnerEntityList.size() > 0) {
//            throw new ApiBusinessException("同步的企业不能既是主体企业又是客户:"+vo.getName());
//        } else
        if (mCustomerEntityList.size() > 1) {
            throw new ApiBusinessException("名称重复:"+vo.getName());
        } else if (mCustomerEntityList.size() == 1) {
            MCustomerEntity entity = mapper.selectByName(vo.getName());
            if (!StringUtils.equals(entity.getCredit_no(), vo.getCredit_no())) {
                throw new ApiBusinessException("名称重复:"+vo.getName());
            }
        }
    }

    /**
     * 同步check逻辑
     */
    public void checkList(List<ApiCustomerVo> voList) {
        // 内部check
        // 编号查询是否有重复数据
        List<String> codeList = voList.stream().map(ApiCustomerVo::getCredit_no).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (voList.size() != codeCount) {
            throw new ApiBusinessException("列表数据中，企业信用代码证重复");
        }
    }

    @Override
    public List<MCustomerEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectListByName(name);
    }

}
