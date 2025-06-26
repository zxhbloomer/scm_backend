package com.xinyirun.scm.core.system.serviceimpl.sys.mail;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xinyirun.scm.bean.entity.sys.mail.SMailConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.mail.SMailConfigVo;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.mail.SMailConfigMapper;
import com.xinyirun.scm.core.system.service.mail.ISMailConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @Author: Wqf
 * @Description: 实现
 * @CreateTime : 2023/12/12 16:45
 */

@Service
public class SMailConfigServiceImpl extends BaseServiceImpl<SMailConfigMapper, SMailConfigEntity> implements ISMailConfigService {

    private static final String fieldDecryptKey = "UCrtxSCwYZ5NCIlav";

    @Autowired
    private SMailConfigMapper mapper;


    /**
     * 根据编码查询
     *
     * @param configCode 编码
     * @return SMailConfigEntity
     */
    @Override
    public SMailConfigEntity selectByCode(String configCode) {
        return baseMapper.selectOne(Wrappers.<SMailConfigEntity>lambdaQuery().eq(SMailConfigEntity::getCode, configCode));
    }

    /**
     * 新增
     *
     * @param vo 实体
     * @return InsertResultAo
     */
    @Override
    public InsertResultAo<SMailConfigVo> insert(SMailConfigVo vo) {
        SMailConfigEntity entity =(SMailConfigEntity) BeanUtilsSupport.copyProperties(vo, SMailConfigEntity.class);
        checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        entity.setPassword(encryptValue(vo.getPassword()));
        baseMapper.insert(entity);
        return InsertResultUtil.OK(selectVoById(entity.getId()));
    }

    public SMailConfigVo selectVoById(Integer id) {
//        SMailConfigVo vo = mapper.selectVoById(id);
        return null;
    }

    /**
     * AES对称加密, 加密, 只能对字符串进行加密
     * @param fieldValue
     * @return
     */
    public String encryptValue(String fieldValue){
            // fieldDecryptKey AES秘钥,
        AES aes = SecureUtil.aes(fieldDecryptKey.getBytes(StandardCharsets.UTF_8));
        return aes.encryptBase64(fieldValue);
    }

    /**
     * AES对称加密, 解密
     * @param fieldValue
     * @return
     */
    public Object decryptValue(Object fieldValue){
        if (fieldValue instanceof String){
            AES aes = SecureUtil.aes(fieldDecryptKey.getBytes(StandardCharsets.UTF_8));
            return new String(aes.decrypt(Base64.decode((String)fieldValue)),StandardCharsets.UTF_8);
        }
        else {
            return fieldValue;
        }
    }

    /**
     * 校验
     * @param entity
     * @param type
     */
    private void checkLogic(SMailConfigEntity entity, String type) {
        switch (type) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (selectByCode(entity.getCode()) != null) {
                    throw new InsertErrorException("编码重复!");
                }
                break;
            default:
                break;
        }

    }
}
