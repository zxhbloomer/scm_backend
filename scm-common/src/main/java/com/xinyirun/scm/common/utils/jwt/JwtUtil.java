package com.xinyirun.scm.common.utils.jwt;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.common.constant.JWTSecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.json.JsonObject;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

public class JwtUtil {

    /**
     * 利用jwt解析token信息.
     * @param token 要解析的token信息
     * @param base64Secret 用于进行签名的秘钥
     * @return
     * @throws Exception
     */
    public static String getUserStringByToken(String token, String base64Secret) throws Exception {
        Key key;
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        key = Keys.hmacShaKeyFor(keyBytes);
        String user = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token.replace(JWTSecurityConstants.TOKEN_PREFIX, "")).getBody().getSubject();
        return user;
    }

    /**
     * 利用jwt解析token信息.
     * @param token 要解析的token信息
     * @param secret 用于进行签名的秘钥
     * @return
     * @throws Exception
     */
    public static Optional<Claims> getClaimsFromToken(String token, String secret) throws Exception {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secret).build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 验证token是否过期
     * @param tooken 要解析的token信息
     * @param secret 用于进行签名的秘钥
     * @return true 表示过期，false表示不过期，如果没有设置过期时间，则也不认为过期
     * @throws Exception
     */
    public static boolean isExpired(String tooken,String secret) throws Exception{
        Optional<Claims> claims= getClaimsFromToken(tooken,secret);
        if(claims.isPresent()){
            Date expiration = claims.get().getExpiration();
            return expiration.before(new Date());
        }
        return false;
    }

    /**
     * 获取tooken中的参数值
     * @param token 要解析的token信息
     * @param secret 用于进行签名的秘钥
     * @return
     * @throws Exception
     */
    public static Map<String,Object> extractInfo(String token,String secret) throws Exception{
        Optional<Claims> claims = getClaimsFromToken(token,secret);
        if(claims.isPresent()){
            Map<String,Object> info = new HashMap<String,Object>();
            Set<String> keySet = claims.get().keySet();
            //通过迭代，提取token中的参数信息
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                Object value =  claims.get().get(key);
                info.put(key,value);

            }
            return info;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
//        String secret = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";
//        String token_test = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJvcGVyYXRpb25TZXRcIjpbXSxcInN0YWZmX0lkXCI6MjgsXCJ0b2tlbl9leHBpcmVzX2F0XCI6XCIyMDI0LTAxLTE3IDE1OjU4OjQxLjM2MlwiLFwidXNlcl9JZFwiOjMxLFwidXNlcm5hbWVcIjpcInRlc3RcIn0iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzA1NDc4MzIxfQ.nWg1Zd9xog2rwyJexnHqlmRXpnP7amjyUrs8YwKAzF4";
//        String token_prod = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJvcGVyYXRpb25TZXRcIjpbXSxcInN0YWZmX0lkXCI6MjgsXCJ0b2tlbl9leHBpcmVzX2F0XCI6XCIyMDI0LTAxLTE3VDE2OjExOjQ4LjkxNFwiLFwidXNlcl9JZFwiOjMxLFwidXNlcm5hbWVcIjpcInRlc3RcIn0iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzA1NDc5MTA4fQ.cvOsJWf0ssUGzVVVBjFD58-44CaX47Yodle4-BLBknw";
//        String str_test = getUserStringByToken(token_test, secret);
//        String str_prod = getUserStringByToken(token_prod, secret);
//        System.out.println("test:"+str_test);
//        System.out.println("prod:"+str_prod);
        TestBean bean = new TestBean();
        bean.setCode("test");
        bean.setTime(LocalDateTime.now());
        JSON.toJSON(bean);
        System.out.println(JSON.toJSON(bean));
    }

    @Data
    public static class TestBean {
        private String code;

        private LocalDateTime time;
    }
}
