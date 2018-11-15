package com.web.handle.service.oss;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.dao.UserDAO;
import com.dao.impl.UserDAOJdbcTemplate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 * 上传头像成功时的回调
 */
@Controller
public class OSSCallback{
    @Autowired
    private UserDAO userDAO;
	/**
	 * Get请求
	 */
	@RequestMapping(value = "/user/oss/uploadImg/callback",method = RequestMethod.GET)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("用户输入url:" + request.getRequestURI());
		response(request, response, "input get ", 200);
	}
    /**
     * Post请求
     */
    @RequestMapping(value = "/user/oss/uploadImg/callback",method = RequestMethod.POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result=null;
        //获取OSS服务器向应用服务器返回的上传的文件的信息
        //ossCallbackBody为一个JSON字符串，类似于{'bucket':"wifisocket", 'object':"headPicture/247BCDC4CB69F2D1C75AD760FA000614.jpg",'size':8710,'mimeType':"image/jpeg"}
        String ossCallbackBody = GetPostBody(request.getInputStream(), Integer.parseInt(request.getHeader("content-length")));
        //验证OSS回调请求
        boolean ret = VerifyOSSCallbackRequest(request, ossCallbackBody);
        JSONObject jsonObject =JSONObject.parseObject(ossCallbackBody);
        String fileName=jsonObject.getString("object");
        String userID= jsonObject.getString("userID");
        String headImgURL = jsonObject.getString("headImgURL");
        fileName = headImgURL+fileName;
        Map<String,String> msg =new HashMap<>();
        try {
            userDAO.alterHeadImgURL(userID,fileName);
            msg.put("code","1");
            msg.put("fileName",fileName);
        } catch (SQLException e) {
            msg.put("code","0");
            e.printStackTrace();
        }
        if (ret)
        {
            result=JSONObject.toJSONString(msg);
            //返回响应信息
            response(request, response, result, HttpServletResponse.SC_OK);
        }
        else
        {
            msg.put("code","0");
            result=JSONObject.toJSONString(msg);
            //返回响应信息
            response(request, response, result, HttpServletResponse.SC_BAD_REQUEST);
        }
    }
	/**
	 * 获取public key
	 * @param url
	 * @return
	 */
	@SuppressWarnings({ "finally" })
	public String executeGet(String url) {
	    BufferedReader in = null;
		String content = null;
		try {
			// 定义HttpClient
			@SuppressWarnings("resource")
            DefaultHttpClient client = new DefaultHttpClient();
			// 实例化HTTP方法
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			content = sb.toString();
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();// 最后要关闭BufferedReader
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return content;
		}
	}
	/**
	 * 获取Post消息体
	 * @param is
	 * @param contentLen
	 * @return
	 */
	public String GetPostBody(InputStream is, int contentLen) {
		if (contentLen > 0) {
			int readLen = 0;
			int readLengthThisTime = 0;
			byte[] message = new byte[contentLen];
			try {
				while (readLen != contentLen) {
					readLengthThisTime = is.read(message, readLen, contentLen - readLen);
					if (readLengthThisTime == -1) {// Should not happen.
						break;
					}
					readLen += readLengthThisTime;
				}
				return new String(message);
			} catch (IOException e) {
			}
		}
		return "";
	}
	/**
	 * 验证上传回调的Request
	 * @param request
	 * @param ossCallbackBody
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	protected boolean VerifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody) throws NumberFormatException, IOException
	{
		boolean ret = false;
		String autorizationInput = new String(request.getHeader("Authorization"));
		String pubKeyInput = request.getHeader("x-oss-pub-key-url");
		byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
		byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
		String pubKeyAddr = new String(pubKey);
        System.out.println("pubKeyAddr:"+pubKeyAddr);
		if (!pubKeyAddr.startsWith("http://gosspublic.alicdn.com/") && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/"))
		{
			System.out.println("pub key addr must be oss addrss");
			return false;
		}
		String retString = executeGet(pubKeyAddr);
		retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
		retString = retString.replace("-----END PUBLIC KEY-----", "");
		String queryString = request.getQueryString();
		String uri = request.getRequestURI();
		String decodeUri = java.net.URLDecoder.decode(uri, "UTF-8");
		String authStr = decodeUri;
		if (queryString != null && !queryString.equals("")) {
			authStr += "?" + queryString;
		}
		authStr += "\n" + ossCallbackBody;
		ret = doCheck(authStr, authorization, retString);
		return ret;
	}
	/**
	 * 验证RSA
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 */
	public static boolean doCheck(String content, byte[] sign, String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
			signature.initVerify(pubKey);
			signature.update(content.getBytes());
			boolean bverify = signature.verify(sign);
			return bverify;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 服务器响应结果
	 * @param request
	 * @param response
	 * @param results
	 * @param status
	 * @throws IOException
	 */
	private void response(HttpServletRequest request, HttpServletResponse response, String results, int status) throws IOException {
	    String callbackFunName = request.getParameter("callback");
		response.addHeader("Content-Length", String.valueOf(results.length()));
		if (callbackFunName == null || callbackFunName.equalsIgnoreCase(""))
			response.getWriter().println(results);
		else
			response.getWriter().println(callbackFunName + "( " + results + " )");
		response.setStatus(status);
		response.flushBuffer();
	}
}
