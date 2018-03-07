package com.fpo.core;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ossConfig")
public class OSSConfig {

    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String publicBucket;
    private String privateBucket;

    public void sts(String ossKey) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 1);
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        //Date expiration = DateUtil.parseRfc822Date(sdf.format(c.getTime()));
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(privateBucket, ossKey, HttpMethod.GET);
//设置过期时间
        request.setExpiration(c.getTime());
        // 生成URL签名(HTTP GET请求)
        URL signedUrl = ossClient.generatePresignedUrl(request);
        System.out.println("signed url for getObject: " + signedUrl);
//客户端使用使用url签名字串发送请求
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        Map<String, String> customHeaders = new HashMap<String, String>();
// 添加GetObject请求头
        customHeaders.put("Range", "bytes=100-1000");
        OSSObject object = client.getObject(signedUrl, customHeaders);
        System.out.println(JSONObject.toJSONString(object.getResponse()));
    }


    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setPublicBucket(String publicBucket) {
        this.publicBucket = publicBucket;
    }

    public void setPrivateBucket(String privateBucket) {
        this.privateBucket = privateBucket;
    }
}