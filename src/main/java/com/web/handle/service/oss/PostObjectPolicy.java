package com.web.handle.service.oss;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.dao.UserDAO;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
/**
 * 用来处理用户上传头像的请求
 */
@Controller
public class PostObjectPolicy{
    @Resource(name="ossSignature")
    private Properties properties;
    @Autowired
    private UserDAO userDAO;
    private String accessId;
    private String accessKey;
    private String endpoint;
    private String bucket;
    private String dir;
    private String host;
    private String callbackUrl;
    private String headImgURL;
    @PostConstruct
    public void init(){
        accessId = properties.getProperty("oss.accessId");
        accessKey = properties.getProperty("oss.accessKey");
        bucket = properties.getProperty("oss.bucket");
        endpoint = properties.getProperty("oss.endpoint");
        dir = properties.getProperty("oss.dir")+"/";
        host = properties.getProperty("oss.host");
        callbackUrl = properties.getProperty("oss.callbackURL")+"/user/oss/uploadImg/callback";
        headImgURL = properties.getProperty("oss.headImgURL")+"/";
    }
    /**
	 * 处理用户上传头像的请求。
     * 请求参数：user ID：上传头像的用户的用户名
	 */
    @RequestMapping(value = "/user/alterHeadImg")
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String userID=request.getParameter("userID");
        String fileName=getFileName(userID);
        //创建OSSClient
        OSSClient client = new OSSClient(endpoint, accessId, accessKey);
        try {
            //到期时间
            long expireTime = 30;
        	long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        	Date expiration = new Date(expireEndTime);
        	PolicyConditions policyConds = new PolicyConditions();
        	policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);
            URL url = client.generatePresignedUrl(bucket,accessKey,expiration);
            String expire = String.valueOf(expireEndTime / 1000);
            Map<String, String> respMap = new LinkedHashMap<String, String>();
            String str = "{\'bucket\':${bucket}, " +
                    "\'object\':${object}," +
                    "\'size\':${size}," +
                    "\'mimeType\':${mimeType}," +
                    "\'userID\':'"+userID+"'," +
                    "\'headImgURL\':'"+headImgURL+"'}";
            String callback = "{\"callbackUrl\":\""+callbackUrl+"\"," +
                    "\"callbackBody\":\""+str+"\"," +
                    "\"callbackBodyType\":\"application/json\"}";
            byte[] bytes = Base64.encodeBase64(callback.getBytes("UTF-8"));
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", expire);
            respMap.put("url",url.toString());
            respMap.put("callback", new String(bytes));
            respMap.put("fileName",fileName);
            String respMapStr = JSONObject.toJSONString(respMap);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
            response(request, response, respMapStr);
        } catch (Exception e) {
            System.out.println("获取签名异常");
        }
    }
    /**
     * 服务器响应结果
     */
    private void response(HttpServletRequest request, HttpServletResponse response, String results) throws IOException {
        String callbackFunName = request.getParameter("callback");
        if (callbackFunName==null || callbackFunName.equalsIgnoreCase(""))
            response.getWriter().println(results);
        else
            response.getWriter().println(callbackFunName + "( "+results+" )");
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }
    public String getFileName(String userID){
        String fileName=null;
        try {
            fileName=userDAO.getHeadImgURL(userID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (fileName == null || fileName.equals("")){
            fileName= UUID.randomUUID().toString();
        }else{
            String strs[]=fileName.split("\\.");
            strs=strs[0].split("/");
            fileName=strs[strs.length-1];
        }
        return fileName;
    }
}