package com.xinyirun.scm.core.bpm.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import com.xinyirun.scm.bean.bpm.vo.props.ConditionProps;
import com.xinyirun.scm.bean.bpm.vo.props.DelayProps;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : willian fu
 * @date : 2022/7/15
 */
@Slf4j
@Component("uelTools")
public class UELTools {

//    @Autowired
//    private UserDeptOrLeaderService userDeptOrLeaderService;

    /**
     * 判断集合是否包含某元素
     *
     * @param collection 集合
     * @param val        目标元素
     * @return 匹配结果
     */
    public boolean contains(Collection<?> collection, Object val) {
        return CollectionUtil.contains(collection, val);
    }

    //判断,集合是否包含某元素
    public boolean contains(String list, Object val) {
        return ArrayUtil.contains(list.split(","), val);
    }

    /**
     * 判断用户是否属于某些用户或者部门内
     *
     * @param orgId        用户/部门ID
     * @param userAndDepts 比较的
     * @return 结果
     */
    public boolean orgContains(String orgId, List<OrgUserVo> userAndDepts) {
//        List<String> users = userAndDepts.stream().filter(v -> "user".equals(v.getType()))
//                .map(OrgUserVo::getId).collect(Collectors.toList());
//        if (users.contains(orgId)) {
//            return true;
//        }
//        List<String> collect = userAndDepts.stream().filter(v -> "dept".equals(v.getType()))
//                .map(OrgUserVo::getId).collect(Collectors.toList());
//        for (String dept : collect) {
//            if (userDeptOrLeaderService.userIsBelongToDept(orgId, dept)) {
//                return true;
//            }
//        }
        return false;
    }



    /**
     * 动态获取延时节点延时时长
     *
     * @param execution 上下文
     * @return 延时表达式
     */
    public String getDelayDuration(ExecutionEntity execution) {
        try {
            Map variable = execution.getVariable(WflowGlobalVarDef.WFLOW_NODE_PROPS, Map.class);
            DelayProps props = (DelayProps) variable.get(execution.getActivityId());
            String date = null;
            if (DelayProps.Type.AUTO.equals(props.getType())){
                date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + "T" + props.getDateTime().trim();
            } else if (DelayProps.Type.PRECISE.equals(props.getType())) {
                date = LocalDateTime.parse(props.getDateTime().trim(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .format(DateTimeFormatter.ISO_DATE_TIME);
            }
            return date;
        } catch (Exception e) {
            return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    /**
     * 条件表达式判断
     *
     * @param execution 上下文
     * @return 对比结果
     */
    public boolean conditionCompare(String conditionId, ExecutionEntity execution) {
        Map variable = execution.getVariable(WflowGlobalVarDef.WFLOW_NODE_PROPS, Map.class);
        ConditionProps props = (ConditionProps) variable.get(conditionId);
        //List<Boolean> groupResult = new ArrayList<>(props.getGroups().size() * 2);
        int groupConditionSuccess = 0;
        for (ConditionProps.Group group : props.getGroups()) {
            int subConditionSuccess = 0;
            for (ConditionProps.Group.Condition condition : group.getConditions()) {
                if (subConditionCompare(execution, condition)) {
                    subConditionSuccess++;
                    if ("OR".equals(group.getGroupType())) {
                        //或的关系，那么结束循环，组++
                        groupConditionSuccess++;
                        break;
                    }
                    //全部满足条件也结束循环
                    if (subConditionSuccess == group.getConditions().size()) {
                        groupConditionSuccess++;
                    }
                }
            }
            //判断组对比结果
            if (("OR".equals(props.getGroupsType()) && groupConditionSuccess > 0)
                    || groupConditionSuccess == props.getGroups().size()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 子条件校验
     * @param condition 子条件配置
     * @return 校验结果
     */
    private boolean subConditionCompare(ExecutionEntity execution, ConditionProps.Group.Condition condition) {
        try {
            Object value = execution.getVariable("root".equals(condition.getId()) ? WflowGlobalVarDef.INITIATOR : condition.getId());
            List<Object> values = condition.getValue();
            double val = 0;
            switch (condition.getCompare()) {
                case ">":
                    return toDouble(value) > toDouble(values.get(0));
                case "<":
                    return toDouble(value) < toDouble(values.get(0));
                case ">=":
                    return toDouble(value) >= toDouble(values.get(0));
                case "<=":
                    return toDouble(value) <= toDouble(values.get(0));
                case "=":
                    return value.toString().equals(String.valueOf(values.get(0)));
                case "!=":
                    return !value.toString().equals(String.valueOf(values.get(0)));
                case "B":
                    val = toDouble(value);
                    return toDouble(values.get(0)) < val && val < toDouble(values.get(1));
                case "AB":
                    val = toDouble(value);
                    return toDouble(values.get(0)) <= val && val < toDouble(values.get(1));
                case "BA":
                    val = toDouble(value);
                    return toDouble(values.get(0)) < val && val <= toDouble(values.get(1));
                case "ABA":
                    val = toDouble(value);
                    return toDouble(values.get(0)) <= val && val <= toDouble(values.get(1));
                case "IN":
                    return values.contains(value.toString());
//                case "DEPT":
//                    if (value instanceof List){
//                        List<String> ids = ((List<Map>) value).stream().map(v -> String.valueOf(v.get("id"))).collect(Collectors.toList());
//                        List<String> pids = values.stream().map(v -> String.valueOf(((Map)v).get("id"))).collect(Collectors.toList());
//                        for (String sid : ids) {
//                            boolean result = false;
//                            for (String pid : pids) {
//                                if (sid.equals(pid) || userDeptOrLeaderService.deptIsBelongToDept(sid, pid)){
//                                    result = true;
//                                    break;
//                                }
//                            }
//                            if (!result){
//                                return false;
//                            }
//                        }
//                    }
//                    return true;
                case "ORG":
                    List<OrgUserVo> orgs = values.stream().map(v -> {
                        if (v instanceof OrgUserVo) {
                            return (OrgUserVo) v;
                        } else if (v instanceof Map) {
                            Map<String, Object> valMap = (Map<String, Object>) v;
                            return OrgUserVo.builder().id(valMap.get("id").toString())
                                    .type(valMap.get("type").toString()).build();
                        }
                        return null;
                    }).collect(Collectors.toList());
                    if (value instanceof List){
                        List<String> ids = ((List<Map>) value).stream().map(v -> String.valueOf(v.get("id"))).collect(Collectors.toList());
                        for (String id : ids) {
                            if (orgContains(id, orgs)){
                                return true;
                            }
                        }
                    }else {
                        return orgContains(String.valueOf(value), orgs);
                    }
                    return false;
            }
        } catch (Exception e) {
            log.error("条件判断异常[{}]", condition);
        }
        return false;
    }



    private double toDouble(Object val){
        return NumberUtil.parseNumber(val.toString()).doubleValue();
    }
}
