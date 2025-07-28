package com.xinyirun.scm.core.system.serviceimpl.business.track.gsh56;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.xinyirun.scm.bean.app.vo.master.vehicle.AppMVehicleVo;
import com.xinyirun.scm.bean.entity.business.track.BTrackGsh56Entity;
import com.xinyirun.scm.bean.system.vo.business.track.*;
import com.xinyirun.scm.bean.system.vo.sys.log.STrackValidateTestVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.track.BTrackGsh56Mapper;
import com.xinyirun.scm.core.system.mapper.business.track.BTrackMapper;
import com.xinyirun.scm.core.system.mapper.master.vehicle.MVehicleMapper;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackApiSinoiovService;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackGsh56Service;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackService;
import com.xinyirun.scm.core.system.serviceimpl.business.track.HttpUtils;
import com.xinyirun.scm.core.system.serviceimpl.business.track.MD5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * <p>
 *  使用 腾灏供应商 接口生产轨迹
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Service
@Slf4j
public class BTrackGsh56ServiceImpl extends ServiceImpl<BTrackGsh56Mapper, BTrackGsh56Entity> implements IBTrackGsh56Service {

    @Autowired
    private MVehicleMapper mVehicleMapper;

    @Autowired
    private IBTrackService ibTrackService;

    @Autowired
    private BTrackMapper bTrackMapper;

    @Autowired
    private IBTrackApiSinoiovService ibTrackApiSinoiovService;

    @Autowired
    private BTrackGsh56Mapper bTrackGsh56Mapper;

    private static int cycle = 24;

    @Value("${spring.profiles.active}")
    private String model;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshGsh56Track(BTrackVo vo) {

        List<BStartAndEndTimeVo> statrtAndEndTimeVos = getStartAndEndTime(vo.getStart_time(), vo.getEnd_time());

        String content = getTrack(vo.getVehicle_no(), statrtAndEndTimeVos);

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
    public Boolean checkTruckExist(String vehicle_no) {
        BTrackGsh56Vo bTrackGsh56Vo = bTrackGsh56Mapper.selectOne();

        // 获取url
        BTrackApiSinoiovVo trackApiSinoiovVo = ibTrackApiSinoiovService.getDataByType(SystemConstants.SINOIOV.API_TYPE_CHECK_TRUCK_GSH56);

        String url;
        if (model.contains(SystemConstants.DEV_MODEL_PROD)) {
            url = trackApiSinoiovVo.getProd_url();
        } else {
            url = trackApiSinoiovVo.getTest_url();
        }

        Map<String, String> map = new HashMap<>();
        // 车牌号
        map.put("plate", vehicle_no);

        String sign = sign(map, bTrackGsh56Vo.getSecret_key());

        Map<String, String> params = encrypt(map, bTrackGsh56Vo.getSecret_key());
        // sign
        params.put("sign", sign);
        // openId
        params.put("openId", bTrackGsh56Vo.getOpen_id());

        String result = HttpUtils.post(url, null, params,"UTF-8",120000);

        JSONObject jsonObject = JSONObject.parseObject(result);

        Boolean status = jsonObject.getBoolean("success");

        if (Objects.equals(status, Boolean.FALSE)) {
            log.info(jsonObject.getString("msg"));
            return Boolean.FALSE;
        } else {
            return jsonObject.getBoolean("data");
        }
    }

//    @Override
//    public BVehicleValidateVo checkVehicleExist(AppMVehicleVo vo) {
//        BTrackGsh56Vo bTrackGsh56Vo = bTrackGsh56Mapper.selectOne();
//
//        // 获取url
//        BTrackApiSinoiovVo trackApiSinoiovVo = ibTrackApiSinoiovService.getDataByType(SystemConstants.SINOIOV.API_TYPE_CHECK_VEHICLE_GSH56);
//
//        if (StringUtils.isEmpty(vo.getNo_color())) {
//            MVehicleEntity mVehicleEntity = mVehicleMapper.selectByNo(vo.getNo());
//            vo.setNo_color(mVehicleEntity.getNo_color());
//        }
//
//        String url;
//        if (model.contains(SystemConstants.DEV_MODEL_PROD)) {
//            url = trackApiSinoiovVo.getProd_url();
//        } else {
//            url = trackApiSinoiovVo.getTest_url();
//        }
//
//        Map<String, String> map = new HashMap<>();
//        // 车牌号
//        map.put("plate", vo.getNo());
//
//        if (StringUtils.isNotEmpty(vo.getNo_color())) {
//            // 颜色
//            map.put("color", vo.getNo_color());
//        } else {
//
//        }
//
//        String sign = sign(map, bTrackGsh56Vo.getSecret_key());
//
//        Map<String, String> params = encrypt(map, bTrackGsh56Vo.getSecret_key());
//        // sign
//        params.put("sign", sign);
//        // openId
//        params.put("openId", bTrackGsh56Vo.getOpen_id());
//
//        String result = HttpUtils.post(url, null, params,"UTF-8",120000);
//
//        log.debug("腾颢验车结果BTrackGsh56ServiceImpl："+result);
//
//        BVehicleValidateVo bVehicleValidateVo = JSONObject.parseObject(result, BVehicleValidateVo.class);
//
//        if (bVehicleValidateVo == null) {
//            bVehicleValidateVo = new BVehicleValidateVo();
//            bVehicleValidateVo.setValidate_log("腾颢验车结果为空");
//            bVehicleValidateVo.setSuccess("false");
//            BVehicleValidateDataVo bVehicleValidateDataVo = new BVehicleValidateDataVo();
//            bVehicleValidateDataVo.setFlag(Boolean.FALSE);
//            bVehicleValidateVo.setData(bVehicleValidateDataVo);
//        } else {
//            bVehicleValidateVo.setValidate_log("腾颢验车结果："+result);
//        }
//
//
//        return bVehicleValidateVo;
//
//
//    }

    @Override
    public String getTrackMsg(STrackValidateTestVo vo) {
        List<BStartAndEndTimeVo> statrtAndEndTimeVos = getStartAndEndTime(vo.getStart_date(), vo.getEnd_date());

        BTrackGsh56Vo bTrackGsh56Vo = bTrackGsh56Mapper.selectOne();

        // 获取url
        BTrackApiSinoiovVo trackApiSinoiovVo = ibTrackApiSinoiovService.getDataByType(SystemConstants.SINOIOV.API_TYPE_TRACK_GSH56);

        String url;
        if (Objects.equals(SystemConstants.DEV_MODEL_PROD, model)) {
            url = trackApiSinoiovVo.getProd_url();
        } else {
            url = trackApiSinoiovVo.getTest_url();
        }

        JSONArray array = new JSONArray();

        for (BStartAndEndTimeVo statrtAndEndTimeVo: statrtAndEndTimeVos) {
            Map<String, String> map = new HashMap<>();
            // 车牌号
            map.put("plate", vo.getVehicle_no());
            if (StringUtils.isNotBlank(vo.getVehicle_color())) {
                map.put("color", vo.getVehicle_color());
            }
            // 开始时间
            map.put("begin", LocalDateTimeUtils.formatTime(statrtAndEndTimeVo.getStart_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            // 结束时间
            map.put("end", LocalDateTimeUtils.formatTime(statrtAndEndTimeVo.getEnd_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));

            String sign = sign(map, bTrackGsh56Vo.getSecret_key());

            Map<String, String> params = encrypt(map, bTrackGsh56Vo.getSecret_key());
            // sign
            params.put("sign", sign);
            // openId
            params.put("openId", bTrackGsh56Vo.getOpen_id());

            String result = HttpUtils.post(url, null, params,"UTF-8",120000);

            if (StringUtils.isNotEmpty(result)) {
                array.add(result);
            }
        }

        return array.toString();
    }

    // 调用api获取轨迹
    private String getTrack(String vehicle_no, List<BStartAndEndTimeVo> startAndEndTimeVos) {
        BTrackGsh56Vo bTrackGsh56Vo = bTrackGsh56Mapper.selectOne();

        // 获取url
        BTrackApiSinoiovVo trackApiSinoiovVo = ibTrackApiSinoiovService.getDataByType(SystemConstants.SINOIOV.API_TYPE_TRACK_GSH56);

        String url;
        if (Objects.equals(SystemConstants.DEV_MODEL_PROD, model)) {
            url = trackApiSinoiovVo.getProd_url();
        } else {
            url = trackApiSinoiovVo.getTest_url();
        }

        JSONArray array = new JSONArray();

        for (BStartAndEndTimeVo statrtAndEndTimeVo: startAndEndTimeVos) {
            Map<String, String> map = new HashMap<>();
            // 车牌号
            map.put("plate", vehicle_no);
            // 开始时间
            map.put("begin", LocalDateTimeUtils.formatTime(statrtAndEndTimeVo.getStart_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
            // 结束时间
            map.put("end", LocalDateTimeUtils.formatTime(statrtAndEndTimeVo.getEnd_time(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));

            String sign = sign(map, bTrackGsh56Vo.getSecret_key());

            Map<String, String> params = encrypt(map, bTrackGsh56Vo.getSecret_key());
            // sign
            params.put("sign", sign);
            // openId
            params.put("openId", bTrackGsh56Vo.getOpen_id());

            String result = HttpUtils.post(url, null, params,"UTF-8",120000);

            JSONObject jsonObject = JSONObject.parseObject(result);

            Boolean status = jsonObject.getBoolean("success");

            if (Objects.equals(status, Boolean.FALSE)) {
                throw new ApiBusinessException(jsonObject.getString("msg"));
            }

            if (StringUtils.isNotEmpty(jsonObject.getString("data"))) {
                array.addAll(jsonObject.getJSONArray("data"));
            }
        }

        return array.toString();
    }

    /**
     * 生成密文摘要
     * @param params
     * @param secret
     * @return
     */
    public static String sign(Map<String,String> params, String secret){
        try{
            String[] keys = params.keySet().toArray(new String[0]);
            Arrays.sort(keys, Collections.reverseOrder());

            StringBuilder query = new StringBuilder();
            query.append(secret);
            for (String key : keys) {
                String value = params.get(key);
                if (StringUtils.isNotEmpty(value))
                    query.append(key).append(value);
            }
            query.append(secret);
            return MD5.sign(query.toString(), "", "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("生成密文摘要失败:",e);
        }
    }

    /**
     * 加密参数
     * @param map
     * @param secret
     * @return
     */
    public static Map<String,String> encrypt(Map<String,String> map,String secret){
        for(String s:map.keySet()){
            try {
                String value = map.get(s);
                if (StringUtils.isNotEmpty(value))
                    map.put(s, AES.encrypt(secret,value));
            } catch (Exception e) {
                throw new RuntimeException("参数加密失败:",e);
            }
        }
        return map;
    }

    /**
     * 解密参数
     * @param map
     * @param secret
     * @return
     */
    public static Map<String,String> decrypt(Map<String,String> map,String secret){
        for(String s:map.keySet()){
            try {
                String value = map.get(s);
                if (StringUtils.isNotEmpty(value))
                    map.put(s, AES.decrypt(secret,value));
            } catch (Exception e) {
                throw new RuntimeException("参数解密失败:",e);
            }
        }
        return map;
    }

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

    // 获取时间
    public static void main(String[] args) {
        LocalDateTime start = LocalDateTimeUtils.parse("2022-05-05 22:32:13",new String[]{"yyyy-MM-dd HH:mm:ss"});
        LocalDateTime end = LocalDateTimeUtils.parse("2022-05-06 23:12:44",new String[]{"yyyy-MM-dd HH:mm:ss"});
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
                    break;
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

        System.out.println(list);
    }
}
