package com.xinyirun.scm.bean.system.vo.master;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 地址簿
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "地址簿", description = "地址簿")
@EqualsAndHashCode(callSuper=false)
public class MAddressVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8109278266189641803L;

    private Long id;

    /**
     * 邮编
     */
    private String postal_code;

    /**
     * 联系人
     */
    private String link_man;

    /**
     * 电话
     */
    private String phone;

    /**
     * 默认
     */
    private Boolean is_default;

    /**
     * 标签
     */
    private String tag;
    private String tag_name;

    /**
     * 省
     */
    private Integer province_code;
    private String province_name;

    /**
     * 市
     */
    private Integer city_code;
    private String city_name;

    /**
     * 区
     */
    private Integer area_code;
    private String area_name;

    private String cascader_text;
    private List<Integer> cascader_areas;
    public List<Integer> getCascader_areas(){
        if(province_code == null) {
            return null;
        }
        if(city_code == null) {
            return null;
        }
        if(area_code == null) {
            return null;
        }
        List<Integer> rtnList = new ArrayList<>();
        rtnList.add(province_code);
        rtnList.add(city_code);
        rtnList.add(area_code);
        return rtnList;
    }

    /**
     * 详细地址
     */
    private String detail_address;

    /**
     * 关联单号
     */
    private Long serial_id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 是否删除
     */
    private Boolean is_del;

    private Long c_id;
    private String c_name;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
