package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.FaceLoginResult;
import com.ihrm.domain.system.response.QRCode;
import com.ihrm.system.dao.UserDao;
import com.ihrm.system.utils.BaiduAiUtil;
import com.ihrm.system.utils.QRCodeUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Service
public class FaceLoginService {
    @Value("${qr.url}")
    private String url;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private QRCodeUtil qrCodeUtil;
    @Autowired
    private BaiduAiUtil baiduAiUtil;
    @Autowired
    private UserDao userDao;
    //创建二维码
    public QRCode getQRCode() throws Exception {
        //1.创建唯一标识
        String code=idWorker.nextId()+"";

        //2.生成二维码
        String content=url+"?code="+code;  //二维码的相关信息
        String file=qrCodeUtil.crateQRCode(content);  //生成Data URL 数据类型的 二维码图片
        //3.将当前二维码状态存入redis
        FaceLoginResult result=new FaceLoginResult("-1");
        redisTemplate.boundValueOps(getCacheKey(code)).set(result,10, TimeUnit.MINUTES);
        return new QRCode(code,file);
    }
    //根据唯一标识，查询用户是否登录成功
    public FaceLoginResult checkQRCode(String code) {
        String key=getCacheKey(code);
        return (FaceLoginResult) redisTemplate.opsForValue().get(key);
    }
    //扫描二维码之后，使用拍摄照片进行登录
    public String loginByFace(String code, MultipartFile attachment) throws Exception {
        String userId=baiduAiUtil.faceSearch(Base64Utils.encodeToString(attachment.getBytes()));
        FaceLoginResult result=new FaceLoginResult("1");
        if (userId!=null){
            User user=userDao.findById(userId).get();
            if (user!=null){
                Subject subject=SecurityUtils.getSubject();
                subject.login(new UsernamePasswordToken(user.getMobile(),user.getPassword()));
                String token=subject.getSession().getId()+"";
                result=new FaceLoginResult("0",token,userId);
            }
            redisTemplate.boundValueOps(getCacheKey(code)).set(result,30,TimeUnit.MINUTES);
            return userId;
        }
        return  null;
    }
    //构造缓存key
    private String getCacheKey(String code) {
        return "qrcode_" + code;
    }

}
