package com.ihrm.common.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.Map;

/**
 * @author LPJ
 */
@Getter
@Setter
@ConfigurationProperties("jwt.config")
public class JwtUtil {

    private String key;

    private long ttl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * 签发 token
     * id用户id subject用户名  key为系统设置的变量
     * @param id
     * @param subject
     * @param map
     * @return
     */
    public String createJWT(String id, String subject, Map<String,Object> map) {
        long now=System.currentTimeMillis();
        long exp=now+ttl;
        JwtBuilder jwtBuilder = Jwts.builder().setId(id)
                .setSubject(subject).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, key);
        //jwtBuilder.setClaims(map);使用map会覆盖之前的值
        for (Map.Entry<String,Object> entry:map.entrySet()){
            jwtBuilder.claim(entry.getKey(),entry.getValue());
        }
        String token = jwtBuilder.compact();
        return token;
    }
    /**
     * 解析jwt
     */
    public Claims parseJWT(String token){
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token).getBody();
        }catch (Exception e){
        }
        return claims;
    }


}
