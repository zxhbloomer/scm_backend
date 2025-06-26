package com.xinyirun.scm.core.api.mapper.master.enterprise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiEnterpriseMapper extends BaseMapper<MEnterpriseEntity> {

    /**
     *
     */
    @Select(" "
            +"SELECT                                                                                                                                                                         "
            +"	t1.*,                                                                                                                                                                        "
            +"	t2.logo_id as logo_file,                                                                                                                                                     "
            +"	t2.license_att_id as license_att_file,                                                                                                                                       "
            +"	t2.lr_id_front_att_id as lr_id_front_att_file,                                                                                                                               "
            +"	t2.lr_id_back_att_id as lr_id_back_att_file,                                                                                                                                 "
            +"	t2.doc_att_id as doc_att_file,                                                                                                                                               "
            +"	t5.label as status_name,                                                                                                                                               "
            +"	t6.type_ids as type_ids_str ,                                                                                                                                                  "
            +"  t6.type_names ,                                                                                                                                                                         "
            +"	t7.name as c_name,                                                                                                                   "
            +"	t8.name as u_name                                                                                                                   "
            +"FROM                                                                                                                                                                           "
            +"	m_enterprise t1                                                                                                                                                                "
            +"	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id                                                                                                                       "
            +"	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status                                                                                "
            +"		LEFT JOIN (                                                                                                   "
            +"		SELECT                                                                                                        "
            +"			subt1.enterprise_id,                                                                                      "
            +"			GROUP_CONCAT( subt1.type ORDER BY subt1.enterprise_id SEPARATOR ',' ) AS type_ids ,                       "
            +"			GROUP_CONCAT( subt2.label ORDER BY subt1.enterprise_id SEPARATOR ',' ) AS type_names                      "
            +"		FROM                                                                                                          "
            +"			m_enterprise_types subt1                                                                                  "
            +"			left join s_dict_data subt2 on subt1.type = subt2.dict_value and   subt2.code = 'm_enterprise_type'       "
            +"		GROUP BY                                                                                                      "
            +"			subt1.enterprise_id                                                                                       "
            +"		) AS t6 ON t1.id = t6.enterprise_id                                                                           "
            +"	LEFT JOIN m_staff t7 ON t1.c_id = t7.id                                                                            "
            +"	LEFT JOIN m_staff t8 ON t1.u_id = t8.id                                                                            "
            +"	WHERE TRUE                      	                                                                               "
            +"	  and t1.id = #{p1.id}	            	                                                                           "
            + "   AND t1.is_del = false                                                                                            "
    )
    MEnterpriseVo getDetail(@Param("p1") MEnterpriseVo searchCondition);
}
