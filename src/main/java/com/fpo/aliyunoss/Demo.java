package com.fpo.aliyunoss;


import com.aliyun.oss.OSSClient;
import com.fpo.utils.Identities;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class Demo {


    public void upload(InputStream is) {
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        String accessKeyId = "LTAI3vO5csdy8kFw";
        String accessKeySecret = "VuFEeS4V3i4dQDFWNNvnReZvL8XHl7";
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String uuid = Identities.uuid();
        System.out.println(uuid);
        ossClient.putObject("ppcg-private", "purchase/"+uuid+".txt", is);
        ossClient.shutdown();
    }
}