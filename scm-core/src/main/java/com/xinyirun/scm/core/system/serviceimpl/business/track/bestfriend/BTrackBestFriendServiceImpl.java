package com.xinyirun.scm.core.system.serviceimpl.business.track.bestfriend;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.track.BTrackGsh56Entity;
import com.xinyirun.scm.bean.system.vo.business.track.BStartAndEndTimeVo;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackApiSinoiovVo;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.business.track.bestfriend.BTrackBestFriend56Vo;
import com.xinyirun.scm.bean.system.vo.sys.log.STrackValidateTestVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.track.BTrackGsh56Mapper;
import com.xinyirun.scm.core.system.mapper.business.track.bestfriend.BTrackBestFriend56Mapper;
import com.xinyirun.scm.core.system.service.track.bestfriend.IBTrackBestFriendService;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackApiSinoiovService;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackService;
import com.xinyirun.scm.core.system.serviceimpl.business.track.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  使用 好伙伴供应商 接口生产轨迹
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Service
@Slf4j
public class BTrackBestFriendServiceImpl extends ServiceImpl<BTrackGsh56Mapper, BTrackGsh56Entity> implements IBTrackBestFriendService {

    @Autowired
    private BTrackBestFriend56Mapper bestFriend56Mapper;

    @Autowired
    private IBTrackService ibTrackService;

    @Autowired
    private IBTrackApiSinoiovService ibTrackApiSinoiovService;

    private static int cycle = 24;

    @Value("${spring.profiles.active}")
    private String model;


    /**
     * 验证车辆是否入网
     *
     * @param vehicle_no
     */
    @Override
    public Map<String, String> CheckVehicleExsit(String vehicle_no) {
        Map<String, String> resultMap = new HashMap<>();

        // 获取验车信息
        String request = getValidateResult(vehicle_no);

        log.debug("返回值 -->{}", request);
        JSONObject jsonObject = JSONObject.parseObject(request);

        log.debug("好伙伴验车结果返回:"+request);
//        resultMap.put("info", "好伙伴验车结果返回:"+request);

        Boolean status = jsonObject.getBoolean("success");

        if (Objects.equals(status, Boolean.FALSE)) {
            throw new AppBusinessException("北斗入网确认服务调用失败，请联系系统管理员！错误内容："+jsonObject.getString("msg"));
        }

        JSONObject result = jsonObject.getObject("result", JSONObject.class);
        if (0 == result.getInteger("code")) {
            String time = result.getString("result").replace("最后一次定位时间:", "");
            resultMap.put("result", "true");
            resultMap.put("gps_time", time);
            return resultMap;
        } else {
            resultMap.put("result", "false");
            resultMap.put("info", jsonObject.getString("msg"));
            return resultMap;
        }

    }

    /**
     * 刷新轨迹
     *
     * @param vo
     */
    @Override
    public void refreshGsh56Track(BTrackVo vo) {
        List<BStartAndEndTimeVo> statrtAndEndTimeVos = getStartAndEndTime(vo.getStart_time(), vo.getEnd_time());

        String content = getTrack(vo.getVehicle_no(), statrtAndEndTimeVos, getToken(), getInfo().getAccount_num());

        BTrackVo bTrackVo = new BTrackVo();
        bTrackVo.setColor(SystemConstants.SINOIOV.COLOR_YELLOW);
        bTrackVo.setContent(content);
        bTrackVo.setStart_time(vo.getStart_time());
        bTrackVo.setEnd_time(vo.getEnd_time());

        bTrackVo.setVehicle_no(vo.getVehicle_no());
        bTrackVo.setWaybill_no(vo.getWaybill_no());

        ibTrackService.delete(bTrackVo);
        if (StringUtils.isNotEmpty(bTrackVo.getContent()) && !"[]".equals(bTrackVo.getContent())) {
            ibTrackService.insert(bTrackVo);
        }
    }

    @Override
    public String getValidateResult(String vehicleNo) {
        // 获取信息
        BTrackBestFriend56Vo info = getInfo();
        // 获取token
        String token = getToken();
        // 获取url
        String url = getUrl(SystemConstants.SINOIOV.API_TYPE_CHECK_VEHICLE_BEST_FRIEND);

        Map<String, String> map = new HashMap<>();
        // 车牌号
        map.put("vclN", vehicleNo);
        // 客户号
        map.put("customerId",info.getAccount_num());
        map.put("plateEnterpriseId", "00000000-0000-0000-0000-000000000002");
        map.put("agentEnterpriseId", "00000000-0000-0000-0000-000000000002");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);

        String request = HttpUtils.post(url, headers, null, "UTF-8", 120000, JSONObject.toJSONString(map));

        return request;
    }

    /**
     * 轨迹测试
     *
     * @param vo
     * @return
     */
    @Override
    public String getTrackMsg(STrackValidateTestVo vo) {
        List<BStartAndEndTimeVo> startAndEndTimeVos = getStartAndEndTime(vo.getStart_date(), vo.getEnd_date());
        String accountNum = getInfo().getAccount_num();
        String token = getToken();
        // 获取url
        String url = getUrl(SystemConstants.SINOIOV.API_TYPE_TRACK_BEST_FRIEND);

        List<String> array = new ArrayList<>();

        for (BStartAndEndTimeVo satrtAndEndTimeVo: startAndEndTimeVos) {
            Map<String, String> map = new HashMap<>();
            // 车牌号
            map.put("plateNumber", vo.getVehicle_no());
            // 开始时间
            map.put("startTime", LocalDateTimeUtils.formatTime(satrtAndEndTimeVo.getStart_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            // 结束时间
            map.put("endTime", LocalDateTimeUtils.formatTime(satrtAndEndTimeVo.getEnd_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            map.put("customerId", accountNum);
            map.put("plateEnterpriseId", "00000000-0000-0000-0000-000000000002");
            map.put("agentEnterpriseId", "00000000-0000-0000-0000-000000000002");

            log.debug("查询轨迹参数:"+map);

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", token);

            // 发送post请求, 参数JSON格式
            String request = HttpUtils.post(url, headers, null, "UTF-8", 120000, JSONObject.toJSONString(map));
            array.add(request);
        }
        return JSONObject.toJSONString(array);
    }

    /**
     * 获取名称, 登录名等
     * @return
     */
    private BTrackBestFriend56Vo getInfo() {
        return bestFriend56Mapper.selectOne();
    }

    /**
     * 获取地址
     * @return
     */
    private String getUrl(String type) {
        // 获取url
        BTrackApiSinoiovVo trackApiSinoiovVo = ibTrackApiSinoiovService.getDataByType(type);

        String url;
        if (Objects.equals(SystemConstants.DEV_MODEL_PROD, model)) {
            url = trackApiSinoiovVo.getProd_url();
        } else {
            url = trackApiSinoiovVo.getTest_url();
        }
        return url;
    }

    // 获取token
    private String getToken() {
        // 获取登录信息
        BTrackBestFriend56Vo info = getInfo();
        // 获取url
        String url = getUrl(SystemConstants.SINOIOV.API_TYPE_TRACK_BEST_FRIEND_TOKEN);

        // 发送请求
        Map<String, String> map = new HashMap<>();
        map.put("AccountNumber", info.getAccount_num());
        map.put("Password", info.getPassword());
        map.put("IsAddedAction", info.getIs_added_action() ? "true" : "false" );
        String request = HttpUtils.getRequest(url, map, "UTF-8", 120000);
        log.debug("返回值--> {}", request);
        JSONObject jsonObject = JSONObject.parseObject(request);
        return "Bearer " + jsonObject.getString("result");
    }

    // 调用api获取轨迹
    private String getTrack(String vehicle_no, List<BStartAndEndTimeVo> startAndEndTimeVos, String token, String customer_id) {

        // 获取url
        String url = getUrl(SystemConstants.SINOIOV.API_TYPE_TRACK_BEST_FRIEND);

        List<Map<String, Object>> array = new ArrayList<>();

        for (BStartAndEndTimeVo statrtAndEndTimeVo: startAndEndTimeVos) {
            Map<String, String> map = new HashMap<>();
            // 车牌号
            map.put("plateNumber", vehicle_no);
            // 开始时间
            map.put("startTime", LocalDateTimeUtils.formatTime(statrtAndEndTimeVo.getStart_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            // 结束时间
            map.put("endTime", LocalDateTimeUtils.formatTime(statrtAndEndTimeVo.getEnd_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            map.put("customerId", customer_id);
            map.put("plateEnterpriseId", "00000000-0000-0000-0000-000000000002");
            map.put("agentEnterpriseId", "00000000-0000-0000-0000-000000000002");

            log.debug("查询轨迹参数:"+map);

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", token);

            // 发送post请求, 参数JSON格式
            String request = HttpUtils.post(url, headers, null, "UTF-8", 120000, JSONObject.toJSONString(map));

            JSONObject jsonObject = JSONObject.parseObject(request);

            log.debug("好伙伴查询轨迹结果返回:"+request);

            Boolean status = jsonObject.getBoolean("success");

            if (Objects.equals(status, Boolean.FALSE)) {
                throw new AppBusinessException("轨迹服务调用失败，请联系系统管理员！错误内容："+jsonObject.getString("msg"));
            }

            String result = jsonObject.getString("result");
            if (StringUtils.isNotEmpty(result)) {
                JSONObject result1 = JSONObject.parseObject(result);
                List<Map> trackInfos = JSON.parseArray(result1.getString("trackInfos"), Map.class);
                for (Map trackInfo : trackInfos) {
                    List<Map> points = JSON.parseArray(JSONObject.toJSONString(trackInfo.get("points")), Map.class);
                    List<Map<String, Object>> collect = points.stream().map(item -> {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("lat", item.get("latitude"));
                        resultMap.put("lon", item.get("longitude"));
                        resultMap.put("spd", item.get("speed"));
                        resultMap.put("area", item.get("country"));
                        resultMap.put("city", item.get("city"));
                        resultMap.put("plate", item.get("plateNumber"));
                        resultMap.put("address", null);
                        resultMap.put("gpsTime", item.get("create_time"));
                        resultMap.put("province", item.get("province"));
                        return resultMap;
                    }).collect(Collectors.toList());
                    array.addAll(collect);
                }
            }
        }
        return JSONObject.toJSONString(array);
    }


    // 获取时间
//    public static void main(String[] args) {
//        LocalDateTime start = LocalDateTimeUtils.parse("2022-05-05 22:32:13",new String[]{"yyyy-MM-dd HH:mm:ss"});
//        LocalDateTime end = LocalDateTimeUtils.parse("2022-05-06 23:12:44",new String[]{"yyyy-MM-dd HH:mm:ss"});
//        long hours = LocalDateTimeUtils.betweenTwoTime(start, end, ChronoUnit.HOURS);
//        System.out.println(hours);
//
//        LocalDateTime startTemp = null;
//        LocalDateTime endTemp = null;
//
//        List<BStartAndEndTimeVo> list = new ArrayList<>();
//
//        int i = 0;
//        while (hours >= cycle) {
//            Map<String, String> map = new HashMap<>();
//            if (i == 0) {
//                startTemp = start;
//                endTemp = startTemp.plusHours(cycle);
//            } else {
//                if ((endTemp.plusHours(cycle)).compareTo(end) >= 0) {
//                    startTemp = startTemp.plusHours(cycle);
//                    endTemp = end;
//                    BStartAndEndTimeVo vo = new BStartAndEndTimeVo();
//                    vo.setStart_time(startTemp);
//                    vo.setEnd_time(endTemp);
//                    list.add(vo);
//                    break;
//                } else {
//                    startTemp = startTemp.plusHours(cycle);
//                    endTemp = startTemp.plusHours(cycle);
//                }
//            }
//            BStartAndEndTimeVo vo = new BStartAndEndTimeVo();
//            vo.setStart_time(startTemp);
//            vo.setEnd_time(endTemp);
//            list.add(vo);
//            i++;
//        }
//
//        if (hours < cycle) {
//            BStartAndEndTimeVo vo = new BStartAndEndTimeVo();
//            vo.setStart_time(start);
//            vo.setEnd_time(end);
//            list.add(vo);
//        }
//
//        System.out.println(list);
//    }

    // 获取时间
    private List<BStartAndEndTimeVo> getStartAndEndTime(LocalDateTime start, LocalDateTime end) {
        long hours = LocalDateTimeUtils.betweenTwoTime(start, end, ChronoUnit.HOURS);
        System.out.println(hours);

        LocalDateTime startTemp = null;
        LocalDateTime endTemp = null;

        List<BStartAndEndTimeVo> list = new ArrayList<>();

        int i = 0;
        while (hours >= cycle) {
            Map<String, String> map = new HashMap<>();
            if (i == 0) {
                startTemp = start;
                endTemp = startTemp.plusHours(cycle);
            } else {
                if ((endTemp.plusHours(cycle)).compareTo(end) >= 0) {
                    startTemp = startTemp.plusHours(cycle);
                    endTemp = end;
                    BStartAndEndTimeVo vo = new BStartAndEndTimeVo();
                    vo.setStart_time(startTemp);
                    vo.setEnd_time(endTemp);
                    list.add(vo);
                    return list;
                } else {
                    startTemp = startTemp.plusHours(cycle);
                    endTemp = startTemp.plusHours(cycle);
                }
            }
            BStartAndEndTimeVo vo = new BStartAndEndTimeVo();
            vo.setStart_time(startTemp);
            vo.setEnd_time(endTemp);
            list.add(vo);
            i++;
        }

        if (hours < cycle) {
            BStartAndEndTimeVo vo = new BStartAndEndTimeVo();
            vo.setStart_time(start);
            vo.setEnd_time(end);
            list.add(vo);
        }

        return list;
    }
}
