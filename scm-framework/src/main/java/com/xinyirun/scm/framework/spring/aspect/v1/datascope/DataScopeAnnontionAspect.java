package com.xinyirun.scm.framework.spring.aspect.v1.datascope;//package com.xinyirun.scm.managerstarter.spring.aspect;

import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据过滤处理
 *
 */
@Aspect
@Component
@Slf4j
public class DataScopeAnnontionAspect {

    @Autowired
    ISConfigService isConfigService;

    @Autowired
    IMPermissionService imPermissionService;

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE_ANNOTATION_WITH = "dataScopeAnnotation_with";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE_ANNOTATION = "dataScopeAnnotation";

    // 配置织入点
    @Pointcut("@annotation(com.xinyirun.scm.common.annotations.DataScopeAnnotion)")
    public void dataScopeAnnotionPointCut() {}

    @Before("dataScopeAnnotionPointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        clearDataScope(point);
        handleDataScope(point);
    }

    protected void handleDataScope(final JoinPoint joinPoint) {
        // 获得注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataScopeAnnotion controllerDataScope = method.getAnnotation(DataScopeAnnotion.class);
        if (controllerDataScope == null) {
            return;
        } else {
            dataScopeFilter(joinPoint, controllerDataScope);
        }
    }

//    /**
//     * 数据范围过滤
//     *
//     * @param joinPoint 切点
//     */
//    public void dataScopeFilter(JoinPoint joinPoint, DataScopeAnnotion dataScopeAnnotion) {
//        Integer count = imPermissionService.selectPermissionsCountByStaffId(SecurityUtil.getStaff_id());
//        if (count != null && count > 0) {
//            return;
//        }
//
//
//        SConfigEntity scentity = isConfigService.selectByKey("warehouse_gropup_enable");
//        if(scentity == null || "0".equals(scentity.getValue())){
//            return;
//        }
//
//        /**
//         * 单个仓库数据权限
//         *          * 例子
//         *          * select 1
//         *          *  where true
//         *          *    and (  exists (select 1 where 1=1)
//         *          *         )
//         */
//        if ("01".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "     and (                                                                                                       "
//                    + "      exists (                                                                                                   "
//                    + "       SELECT 1                                                                                                  "
//                    + "         FROM m_staff_org com_t1                                                                                 "
//                    + "   inner JOIN m_position com_t2                                                                                  "
//                    + "           ON com_t1.serial_id = com_t2.id                                                                       "
//                    + "          AND com_t1.serial_type = 'm_position'                                                                  "
//                    + "   inner JOIN b_warehouse_position com_t3                                                                        "
//                    + "           ON com_t3.serial_id = com_t2.id                                                                       "
//                    + "          AND com_t3.serial_type = 'm_position'                                                                  "
//                    + "   inner JOIN m_warehouse com_t4                                                                                 "
//                    + "           ON com_t3.warehouse_id = com_t4.id                                                                    "
//                    + "        where com_t1.staff_id= {}                                                                                "
//                    + "          and com_t4.id = {}                                                                                     "
//                    + "          )                                                                                                      "
//                    + "		OR EXISTS (																									"
//                    + "			SELECT                                                                                                  "
//                    + "				1                                                                                                   "
//                    + "			FROM                                                                                                    "
//                    + "				b_warehouse_relation com_t1                                                                         "
//                    + "				INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                                 "
//                    + "				AND com_t1.serial_type = 'b_warehouse_group'                                                        "
//                    + "				INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id               "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                    "
//                    + "			WHERE                                                                                                   "
//                    + "				com_t1.staff_id = {}                                                                                "
//                    + "				AND com_t4.id = {}                                                                                  "
//                    + "			)                                                                                                       "
//                    + "		OR EXISTS (                                                                                                 "
//                    + "			SELECT                                                                                                  "
//                    + "				1                                                                                                   "
//                    + "			FROM                                                                                                    "
//                    + "				b_warehouse_relation com_t1                                                                         "
//                    + "				INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                       "
//                    + "				AND com_t1.serial_type = 'm_warehouse'                                                              "
//                    + "			WHERE                                                                                                   "
//                    + "				com_t1.staff_id = {}                                                                                "
//                    + "			AND com_t2.id = {}                                                                                      "
//                    + "		)                                                                                                           "
//                    +" )                                                                                                                "
//                    +" ";
//            String sql = StringUtils.format(
//                    sql_template,
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type01_condition(),
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type01_condition(),
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type01_condition()
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        /**
//         * 例子
//         * select 1
//         *  where true
//         *    and (  exists (select 1 where 1=1)
//         *           or
//         *           exists (select 1 where 1=1)
//         *         )
//         */
//        if ("02".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + " and  (                                                                                                            "
//                    + "      exists (                                                                                                     "
//                    + "       SELECT 1                                                                                                    "
//                    + "         FROM m_staff_org com_t1                                                                                   "
//                    + "   inner JOIN m_position com_t2                                                                                    "
//                    + "           ON com_t1.serial_id = com_t2.id                                                                         "
//                    + "          AND com_t1.serial_type = 'm_position'                                                                    "
//                    + "   inner JOIN b_warehouse_position com_t3                                                                          "
//                    + "           ON com_t3.serial_id = com_t2.id                                                                         "
//                    + "          AND com_t3.serial_type = 'm_position'                                                                    "
//                    + "   inner JOIN m_warehouse com_t4                                                                                   "
//                    + "           ON com_t3.warehouse_id = com_t4.id                                                                      "
//                    + "        where com_t1.staff_id= {}                                                                                  "
//                    + "          and com_t4.id IN ({},{})                                                                                 "
//                    + "          )                                                                                                        "
//                    + "		OR EXISTS (																									"
//                    + "			SELECT                                                                                                  "
//                    + "				1                                                                                                   "
//                    + "			FROM                                                                                                    "
//                    + "				b_warehouse_relation com_t1                                                                         "
//                    + "				INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                                 "
//                    + "				AND com_t1.serial_type = 'b_warehouse_group'                                                        "
//                    + "				INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id               "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                    "
//                    + "			WHERE                                                                                                   "
//                    + "				com_t1.staff_id = {}                                                                                "
//                    + "				AND com_t4.id IN ({},{})                                                                            "
//                    + "			)                                                                                                       "
//                    + "		OR EXISTS (                                                                                                 "
//                    + "			SELECT                                                                                                  "
//                    + "				1                                                                                                   "
//                    + "			FROM                                                                                                    "
//                    + "				b_warehouse_relation com_t1                                                                         "
//                    + "				INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                       "
//                    + "				AND com_t1.serial_type = 'm_warehouse'                                                              "
//                    + "			WHERE                                                                                                   "
//                    + "				com_t1.staff_id = {}                                                                                "
//                    + "			AND com_t2.id IN ({},{})                                                                                "
//                    + "		)                                                                                                           "
//                    + " )                                                                                                               "
//                    + " ";
//
//            String sql = StringUtils.format(sql_template,
//                                            SecurityUtil.getStaff_id(),
//                                            dataScopeAnnotion.type02_condition().split(",")[0],
//                                            dataScopeAnnotion.type02_condition().split(",")[1],
//                                            SecurityUtil.getStaff_id(),
//                                            dataScopeAnnotion.type02_condition().split(",")[0],
//                                            dataScopeAnnotion.type02_condition().split(",")[1],
//                                            SecurityUtil.getStaff_id(),
//                                            dataScopeAnnotion.type02_condition().split(",")[0],
//                                            dataScopeAnnotion.type02_condition().split(",")[1]
//                                            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        /**
//         * 例子
//         * select 1
//         *  where true
//         *    and (  exists (select 1 where 1=1)
//         *           or
//         *           exists (select 1 where 1=1)
//         *         )
//         */
//        if ("02_condition_exist_or".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "		AND (                                                                                                                    "
//                    + "			EXISTS (                                                                                                             "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				m_staff_org com_t1                                                                                               "
//                    + "				INNER JOIN m_position com_t2 ON com_t1.serial_id = com_t2.id                                                     "
//                    + "				AND com_t1.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN b_warehouse_position com_t3 ON com_t3.serial_id = com_t2.id                                           "
//                    + "				AND com_t3.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND com_t4.id = {} 		                                                                                         "
//                    + "			)                                                                                                                    "
//                    + "			OR EXISTS (                                                                                                          "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				m_staff_org com_t1                                                                                               "
//                    + "				INNER JOIN m_position com_t2 ON com_t1.serial_id = com_t2.id                                                     "
//                    + "				AND com_t1.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN b_warehouse_position com_t3 ON com_t3.serial_id = com_t2.id                                           "
//                    + "				AND com_t3.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND com_t4.id = {}                                                                                               "
//                    + "			)                                                                                                                    "
//                    + "		OR EXISTS (																								                 "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                                              "
//                    + "				AND com_t1.serial_type = 'b_warehouse_group'                                                                     "
//                    + "				INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND com_t4.id = {}                                                                                               "
//                    + "		)                                                                                                                        "
//                    + "		OR EXISTS (																								                 "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                                              "
//                    + "				AND com_t1.serial_type = 'b_warehouse_group'                                                                     "
//                    + "				INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND com_t4.id = {}                                                                                               "
//                    + "		)                                                                                                                        "
//                    + "		OR EXISTS (                                                                                                              "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                                    "
//                    + "				AND com_t1.serial_type = 'm_warehouse'                                                                           "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "			AND com_t2.id = {}                                                                                                   "
//                    + "		)                                                                                                                        "
//                    + "		OR EXISTS (                                                                                                              "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                                    "
//                    + "				AND com_t1.serial_type = 'm_warehouse'                                                                           "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "			AND com_t2.id = {}                                                                                                   "
//                    + "		)                                                                                                                        "
//                    + ")                                                                                                                             "
//                    + "";
//            String sql = StringUtils.format(sql_template,
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_condition_exist_or().split(",")[0],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_condition_exist_or().split(",")[1],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_condition_exist_or().split(",")[0],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_condition_exist_or().split(",")[1],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_condition_exist_or().split(",")[0],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_condition_exist_or().split(",")[1]
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        /**
//         *    APP监管任务已完成专用
//         */
//        if ("02_monitor_finish_condition_exist_or".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "		AND (                                                                                                                    "
//                    + "			EXISTS (                                                                                                             "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				m_staff_org com_t1                                                                                               "
//                    + "				INNER JOIN m_position com_t2 ON com_t1.serial_id = com_t2.id                                                     "
//                    + "				AND com_t1.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN b_warehouse_position com_t3 ON com_t3.serial_id = com_t2.id                                           "
//                    + "				AND com_t3.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND (com_t4.id = {}  or  com_t4.id = {} )		                                                                 "
//                    + "			)                                                                                                                    "
//                    + "		OR EXISTS (																									             "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                                              "
//                    + "				AND com_t1.serial_type = 'b_warehouse_group'                                                                     "
//                    + "				INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND (com_t4.id = {}  or  com_t4.id = {} )                                                                        "
//                    + "			)                                                                                                                    "
//                    + "		OR EXISTS (                                                                                                              "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                                    "
//                    + "				AND com_t1.serial_type = 'm_warehouse'                                                                           "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "			AND (com_t2.id = {}  or  com_t2.id = {} )                                                                            "
//                    + "		)                                                                                                                        "
//                    + "	)                                                                                                                            "
//                    + "";
//            String sql = StringUtils.format(sql_template,
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_monitor_finish_condition_exist_or().split(",")[0],
//                    dataScopeAnnotion.type02_monitor_finish_condition_exist_or().split(",")[1],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_monitor_finish_condition_exist_or().split(",")[0],
//                    dataScopeAnnotion.type02_monitor_finish_condition_exist_or().split(",")[1],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_monitor_finish_condition_exist_or().split(",")[0],
//                    dataScopeAnnotion.type02_monitor_finish_condition_exist_or().split(",")[1]
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        /**
//         *    PC监管任务合计专用
//         */
//        if ("02_monitor_sum_condition_exist_or".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "		AND (                                                                                                                    "
//                    + "			EXISTS (                                                                                                             "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				m_staff_org com_t1                                                                                               "
//                    + "				INNER JOIN m_position com_t2 ON com_t1.serial_id = com_t2.id                                                     "
//                    + "				AND com_t1.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN b_warehouse_position com_t3 ON com_t3.serial_id = com_t2.id                                           "
//                    + "				AND com_t3.serial_type = 'm_position'                                                                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND (com_t4.id = {}  or  com_t4.id = {} )		                                                                 "
//                    + "			)                                                                                                                    "
//                    + "		OR EXISTS (																									             "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                                              "
//                    + "				AND com_t1.serial_type = 'b_warehouse_group'                                                                     "
//                    + "				INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id                            "
//                    + "				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                                 "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "				AND (com_t4.id = {}  or  com_t4.id = {} )                                                                        "
//                    + "			)                                                                                                                    "
//                    + "		OR EXISTS (                                                                                                              "
//                    + "			SELECT                                                                                                               "
//                    + "				1                                                                                                                "
//                    + "			FROM                                                                                                                 "
//                    + "				b_warehouse_relation com_t1                                                                                      "
//                    + "				INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                                    "
//                    + "				AND com_t1.serial_type = 'm_warehouse'                                                                           "
//                    + "			WHERE                                                                                                                "
//                    + "				com_t1.staff_id = {}                                                                                             "
//                    + "			AND (com_t2.id = {}  or  com_t2.id = {} )                                                                            "
//                    + "		)                                                                                                                        "
//                    + ")                                                                                                                             "
//                    + "";
//            String sql = StringUtils.format(sql_template,
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_monitor_sum_condition_exist_or().split(",")[0],
//                    dataScopeAnnotion.type02_monitor_sum_condition_exist_or().split(",")[1],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_monitor_sum_condition_exist_or().split(",")[0],
//                    dataScopeAnnotion.type02_monitor_sum_condition_exist_or().split(",")[1],
//                    SecurityUtil.getStaff_id(),
//                    dataScopeAnnotion.type02_monitor_sum_condition_exist_or().split(",")[0],
//                    dataScopeAnnotion.type02_monitor_sum_condition_exist_or().split(",")[1]
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        // 入库计划 仓库权限
//        if ("data_scope_b_in_plan".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "		INNER JOIN b_warehouse_position com_t3 on com_t3.warehouse_id = {}                                                                           "
//                    + "		INNER JOIN m_position com_t2  ON com_t3.serial_id = com_t2.id  AND com_t3.serial_type = 'm_position'                                         "
//                    + "		INNER JOIN m_staff_org com_t1 ON  com_t1.serial_id = com_t2.id AND com_t1.serial_type = 'm_position'  AND com_t1.staff_id = {}               "
//                    + "";
//            String sql = StringUtils.format(
//                    sql_template,
//                    dataScopeAnnotion.type_data_scope_b_in_plan_condition(),
//                    SecurityUtil.getStaff_id()
//
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        // 入库单 仓库权限
//        if ("data_scope_b_in".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "		INNER JOIN b_warehouse_position com_t3 on com_t3.warehouse_id = {}                                                                           "
//                    + "		INNER JOIN m_position com_t2  ON com_t3.serial_id = com_t2.id  AND com_t3.serial_type = 'm_position'                                         "
//                    + "		INNER JOIN m_staff_org com_t1 ON  com_t1.serial_id = com_t2.id AND com_t1.serial_type = 'm_position'  AND com_t1.staff_id = {}               "
//                    + "";
//                    String sql = StringUtils.format(
//                    sql_template,
//                    dataScopeAnnotion.type_data_scope_b_in_condition(),
//                    SecurityUtil.getStaff_id()
//
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//
//        // 出库单 仓库权限
//        if ("data_scope_b_out".equals(dataScopeAnnotion.type())) {
//            String sql_template = ""
//                    + "		INNER JOIN b_warehouse_position com_t3 on com_t3.warehouse_id = {}                                                                           "
//                    + "		INNER JOIN m_position com_t2  ON com_t3.serial_id = com_t2.id  AND com_t3.serial_type = 'm_position'                                         "
//                    + "		INNER JOIN m_staff_org com_t1 ON  com_t1.serial_id = com_t2.id AND com_t1.serial_type = 'm_position'  AND com_t1.staff_id = {}               "
//                    + "";
//            String sql = StringUtils.format(
//                    sql_template,
//                    dataScopeAnnotion.type_data_scope_b_out_condition(),
//                    SecurityUtil.getStaff_id()
//
//            );
//
//            if (StringUtils.isNotBlank(sql)) {
//                BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
//                vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql + " ");
//            }
//        }
//    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity) {
            BaseVo vo = (BaseVo) params;
            vo.getParams().put(DATA_SCOPE_ANNOTATION, "");
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     */
    public void dataScopeFilter(JoinPoint joinPoint, DataScopeAnnotion dataScopeAnnotion) {
        Integer count = imPermissionService.selectPermissionsCountByStaffId(SecurityUtil.getStaff_id());
        if (count != null && count > 0) {
            return;
        }

        SConfigEntity scentity = isConfigService.selectByKey("warehouse_gropup_enable");
        if(scentity == null || "0".equals(scentity.getValue())){
            return;
        }

        /**
         * 单个仓库数据权限
         *          * 例子
         *          * select 1
         *          *  where true
         *          *    and (  exists (select 1 where 1=1)
         *          *         )
         */
        if ("01".equals(dataScopeAnnotion.type())) {
            setDataScope01(joinPoint, dataScopeAnnotion);
        }

        /**
         * 例子
         * select 1
         *  where true
         *    and (  exists (select 1 where 1=1)
         *           or
         *           exists (select 1 where 1=1)
         *         )
         */
        if ("02".equals(dataScopeAnnotion.type())) {
            setDataScope02(joinPoint, dataScopeAnnotion);
        }

    }

    /**
     * 单个仓库数据权限
     *          * 例子
     *          * select 1
     *          *  where true
     *          *    and (  exists (select 1 where 1=1)
     *          *         )
     */
    private void setDataScope01(JoinPoint joinPoint, DataScopeAnnotion dataScopeAnnotion) {
        String sql_with_template = ""
                + " with data_scope_one as                                                                                                    "
                + "  (                                                                                                                    "
                + "                                                                                                                       "
                + "  SELECT                                                                                                               "
                + " 				com_t4.id warehouse_id                                                                                "
                + " 			FROM                                                                                                      "
                + " 				m_staff_org com_t1                                                                                    "
                + " 				INNER JOIN m_position com_t2 ON com_t1.serial_id = com_t2.id                                          "
                + " 				AND com_t1.serial_type = 'm_position'                                                                 "
                + " 				INNER JOIN b_warehouse_position com_t3 ON com_t3.serial_id = com_t2.id                                "
                + " 				AND com_t3.serial_type = 'm_position'                                                                 "
                + " 				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                      "
                + " 			WHERE                                                                                                     "
                + " 				com_t1.staff_id = {}                                                                                  "
                + "                                                                                                                       "
                + " ),  data_scope_two as (                                                                                                           "
                + "                                                                                                                       "
                + "  SELECT                                                                                                               "
                + " 					com_t3.warehouse_id                                                                               "
                + " 				FROM                                                                                                  "
                + " 					b_warehouse_relation com_t1                                                                       "
                + " 					INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                               "
                + " 					AND com_t1.serial_type = 'b_warehouse_group'                                                      "
                + " 					INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id             "
                + " 				WHERE                                                                                                 "
                + " 					com_t1.staff_id = {}                                                                              "
                + " 					                                                                                                  "
                + "                                                                                                                       "
                + " ),  data_scope_three as (                                                                                             "
                + "                                                                                                                       "
                + "  SELECT                                                                                                               "
                + " 					com_t2.id warehouse_id                                                                            "
                + " 				FROM                                                                                                  "
                + " 					b_warehouse_relation com_t1                                                                       "
                + " 					INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                     "
                + " 					AND com_t1.serial_type = 'm_warehouse'                                                            "
                + " 				WHERE                                                                                                 "
                + " 					com_t1.staff_id = {}                                                                              "
                + " 					                                                                                                  "
                + "  )                                                                                                                    ";



//        String sql_where_template = ""
//                + "     and (                                                                                                       "
//                + "       exists (                                                                                                   "
//                + "       SELECT 1                                                                                                  "
//                + "       FROM data_scope_one  where data_scope_one.warehouse_id ={}                                    "
//                +"       )                                                                                                          "
//                + "      or exists (                                                                                                "
//                + "       SELECT 1                                                                                                  "
//                + "       FROM data_scope_two  where data_scope_two.warehouse_id ={}                                     "
//                +"       )                                                                                                          "
//                + "      or exists (                                                                                                "
//                + "       SELECT 1                                                                                                  "
//                + "       FROM data_scope_three  where data_scope_three.warehouse_id ={}                                 "
//                +"       )                                                                                                          "
//                +"      )                                                                                                           "
//                +" ";

        String sql_where_template = ""
                + "     and (                                                                                           "
                + "    	       {} IN (                                                                                  "
                + "                  SELECT warehouse_id FROM                                                           "
                + "                  (                                                                                  "
                + "                      select warehouse_id from data_scope_one                                        "
                +"     		               union all                                                                    "
                + "    	      	       select warehouse_id from data_scope_two                                          "
                + "    	                   union all                                                                    "
                + "    	                 select warehouse_id from data_scope_three                                      "
                +"     		         ) tab)                                                                             "
                + "    	    or                                                                                          "
                + "    	       {} IN (                                                                                  "
                + "    		          SELECT warehouse_id FROM                                                          "
                + "                  (                                                                                  "
                + "    	                 select warehouse_id from data_scope_one                                        "
                + "    	                    union all                                                                   "
                +"     	                 select warehouse_id from data_scope_two                                        "
                + "    	                    union all                                                                   "
                + "    	                 select warehouse_id from data_scope_three                                      "
                + "    	              ) tab)                                                                            "
                +"      )                                                                                               ";


        String sql_with = StringUtils.format(
                sql_with_template,
                SecurityUtil.getStaff_id(),
                SecurityUtil.getStaff_id(),
                SecurityUtil.getStaff_id()
        );

        if (StringUtils.isNotBlank(sql_with)) {
            BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
            vo.getParams().put(DATA_SCOPE_ANNOTATION_WITH, " " + sql_with + " ");
        }


        String sql_where = StringUtils.format(
                sql_where_template,
                dataScopeAnnotion.type01_condition(),
                dataScopeAnnotion.type01_condition()
        );

        if (StringUtils.isNotBlank(sql_where)) {
            BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
            vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql_where + " ");
        }
    }

    /**
     * 例子
     * select 1
     *  where true
     *    and (  exists (select 1 IN (?,?))
     *         )
     */
    private void setDataScope02(JoinPoint joinPoint, DataScopeAnnotion dataScopeAnnotion) {
        String sql_with_template = ""
                + " with data_scope_one as                                                                                                    "
                + "  (                                                                                                                    "
                + "                                                                                                                       "
                + "  SELECT                                                                                                               "
                + " 				com_t4.id warehouse_id                                                                                "
                + " 			FROM                                                                                                      "
                + " 				m_staff_org com_t1                                                                                    "
                + " 				INNER JOIN m_position com_t2 ON com_t1.serial_id = com_t2.id                                          "
                + " 				AND com_t1.serial_type = 'm_position'                                                                 "
                + " 				INNER JOIN b_warehouse_position com_t3 ON com_t3.serial_id = com_t2.id                                "
                + " 				AND com_t3.serial_type = 'm_position'                                                                 "
                + " 				INNER JOIN m_warehouse com_t4 ON com_t3.warehouse_id = com_t4.id                                      "
                + " 			WHERE                                                                                                     "
                + " 				com_t1.staff_id = {}                                                                                  "
                + "                                                                                                                       "
                + " ),  data_scope_two as (                                                                                                           "
                + "                                                                                                                       "
                + "  SELECT                                                                                                               "
                + " 					com_t3.warehouse_id                                                                               "
                + " 				FROM                                                                                                  "
                + " 					b_warehouse_relation com_t1                                                                       "
                + " 					INNER JOIN b_warehouse_group com_t2 ON com_t1.serial_id = com_t2.id                               "
                + " 					AND com_t1.serial_type = 'b_warehouse_group'                                                      "
                + " 					INNER JOIN b_warehouse_group_relation com_t3 ON com_t2.id = com_t3.warehouse_group_id             "
                + " 				WHERE                                                                                                 "
                + " 					com_t1.staff_id = {}                                                                              "
                + " 					                                                                                                  "
                + "                                                                                                                       "
                + " ),  data_scope_three as (                                                                                             "
                + "                                                                                                                       "
                + "  SELECT                                                                                                               "
                + " 					com_t2.id warehouse_id                                                                            "
                + " 				FROM                                                                                                  "
                + " 					b_warehouse_relation com_t1                                                                       "
                + " 					INNER JOIN m_warehouse com_t2 ON com_t1.serial_id = com_t2.id                                     "
                + " 					AND com_t1.serial_type = 'm_warehouse'                                                            "
                + " 				WHERE                                                                                                 "
                + " 					com_t1.staff_id = {}                                                                              "
                + " 					                                                                                                  "
                + "  )                                                                                                                    ";



        String sql_where_template = ""
                + "     and (                                                                                           "
                + "    	       {} IN (                                                                                  "
                + "                  SELECT warehouse_id FROM                                                           "
                + "                  (                                                                                  "
                + "                      select warehouse_id from data_scope_one                                        "
                +"     		               union all                                                                    "
                + "    	      	       select warehouse_id from data_scope_two                                          "
                + "    	                   union all                                                                    "
                + "    	                 select warehouse_id from data_scope_three                                      "
                +"     		         ) tab)                                                                             "
                + "    	    or                                                                                          "
                + "    	       {} IN (                                                                                  "
                + "    		          SELECT warehouse_id FROM                                                          "
                + "                  (                                                                                  "
                + "    	                 select warehouse_id from data_scope_one                                        "
                + "    	                    union all                                                                   "
                +"     	                 select warehouse_id from data_scope_two                                        "
                + "    	                    union all                                                                   "
                + "    	                 select warehouse_id from data_scope_three                                      "
                + "    	              ) tab)                                                                            "
                +"      )                                                                                               ";


        String sql_with = StringUtils.format(
                sql_with_template,
                SecurityUtil.getStaff_id(),
                SecurityUtil.getStaff_id(),
                SecurityUtil.getStaff_id()
        );

        if (StringUtils.isNotBlank(sql_with)) {
            BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
            vo.getParams().put(DATA_SCOPE_ANNOTATION_WITH, " " + sql_with + " ");
        }


        String sql_where = StringUtils.format(
                sql_where_template,
                dataScopeAnnotion.type02_condition().split(",")[0],
                dataScopeAnnotion.type02_condition().split(",")[1]
        );

        if (StringUtils.isNotBlank(sql_where)) {
            BaseVo vo = (BaseVo)joinPoint.getArgs()[0];
            vo.getParams().put(DATA_SCOPE_ANNOTATION, " " + sql_where + " ");
        }
    }
}
