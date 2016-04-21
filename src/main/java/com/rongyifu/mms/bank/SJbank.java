package com.rongyifu.mms.bank;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


import java.util.Map;

import com.rongyifu.mms.api.BizObj;

/**
 * @author zhangliting
 * 
 */
public class SJbank implements BizObj {

    // Bisafe签名服务器的Url
    private static final String signUrl = "http://192.168.30.10:4437";

    private static final String charset = "utf-8";

    private static final String dataTag = "sign";

    private static final String resultTag = "result";

    // private static String titleTag = "title";

    private static final String sicTag = "sic";

    private static final String certDnTag = "certdn";
    
    /*
     * 签名
     */
    public static String sign(String signStr) throws Exception {
        SvsSign svsSign = new SvsSign();
        String signedStr = svsSign.signData(signStr);
        System.out.println("signedStr" + signedStr);
        // 签名失败则抛出
        if ("-1".equals(signedStr))
            throw new Exception("sign failed!");
        else
            return signedStr;

    }
    /*
     * 验签
     */
    /*
     * 签名
     */
    public static String unSign(String signStr) {
        SvsSign svsSign = new SvsSign();

        // 解码
        String unsignStr = svsSign.unsignData(signStr);
        // 获取XML报文
        System.out.println("unsignData=" + unsignStr);
        String sic = svsSign.getSicData(unsignStr);
        System.out.println("sic=" + sic);
        return sic;
    }

    static class SvsSign {

        /**
         * @param oriData
         * 需要签名的源数据，此方法返回签名后的数据
         * @return -1表示签名失败
         */
        public String signData(String oriData) {
            String signData = sendSignRequest(oriData);
            if ("-1".equals(signData))
                return "-1";
            String srcData = "-1";
            try {
                String ret = signData.substring(signData.indexOf("<" + resultTag + ">") + resultTag.length() + 2, signData.indexOf("</" + resultTag + ">"));
                if (!"0".equals(ret)) {
                    System.out.println("签名失败，返回值：" + ret);
                    return "-1";
                }
                srcData = signData.substring(signData.indexOf("<" + dataTag + ">") + dataTag.length() + 2, signData.indexOf("</" + dataTag + ">"));
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
            return srcData.replaceAll("\r\n", "").replaceAll("\n", "");
        }

        public String unsignData(String oriData) {
            oriData = oriData.replaceAll(" ", "+");
            oriData = oriData.replaceAll("reSign=","");
            String signData = sendSignRequestUnSign(oriData);
            System.out.println("signData=" + signData);
            try {
                String ret = signData.substring(signData.indexOf("<" + resultTag + ">") + resultTag.length() + 2, signData.indexOf("</" + resultTag + ">"));
                if (!"0".equals(ret)) {
                    System.out.println("签名失败，返回值：" + ret);
                    return "-1";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
            return signData;
        }

        public String getSicData(String signData) {
            if (signData.indexOf("<sic>") == -1) {
                System.out.println("无法获取sic原文信息！");
                // Trace.logError(Trace.COMPONENT_ACTION,"无法获取sic原文信息！");
                return "-1";
            } else {
                /*
                 * try { signData = new
                 * String(signData.getBytes("UTF-"),"gb2312"); } catch
                 * (UnsupportedEncodingException e) { // TODO Auto-generated
                 * catch block e.printStackTrace(); }
                 */
            }
            String srcData = "-1";
            srcData = signData.substring(signData.indexOf("<" + sicTag + ">") + sicTag.length() + 2, signData.indexOf("</" + sicTag + ">"));
            return srcData.replaceAll("\r\n", "").replaceAll("\n", "");
        }

        public String getCertdnData(String signData) {
            if (signData.indexOf("<certdn>") == -1) {
                System.out.println("无法获取DN信息！");
                // Trace.logError(Trace.COMPONENT_ACTION,"无法获取DN信息！");
                return "-1";
            } else {
                try {
                    signData = new String(signData.getBytes("iso8859-1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String srcData = "-1";
            srcData = signData.substring(signData.indexOf("<" + certDnTag + ">") + certDnTag.length() + 2, signData.indexOf("</" + certDnTag + ">"));
            return srcData.replaceAll("\r\n", "").replaceAll("\n", "");
        }

        public String sendSignRequestUnSign(String oriData) {
            StringBuffer sb = new StringBuffer("");
            HttpURLConnection connection = null;
            BufferedInputStream in = null;
            BufferedOutputStream o = null;
            try {
                connection = (HttpURLConnection) new URL(signUrl).openConnection();
                connection.setRequestProperty("content-type", " INFOSEC_VERIFY_SIGN/1.0");
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                o = new BufferedOutputStream(connection.getOutputStream());
                o.write(oriData.getBytes(charset));
                o.flush();
                in = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, len, charset));
                }
            } catch (Exception e) {
                sb = new StringBuffer("-1");
                e.printStackTrace();
            } finally {
                if (o != null) {
                    try {
                        o.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }

        public String sendSignRequest(String oriData) {
            StringBuffer sb = new StringBuffer("");
            HttpURLConnection connection = null;
            BufferedInputStream in = null;
            BufferedOutputStream o = null;
            try {
                connection = (HttpURLConnection) new URL(signUrl).openConnection();
                connection.setRequestProperty("content-type", "INFOSEC_SIGN/1.0");
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                o = new BufferedOutputStream(connection.getOutputStream());
                o.write(oriData.getBytes(charset));
                o.flush();
                in = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, len, charset));
                }
            } catch (Exception e) {
                sb = new StringBuffer("-1");
                e.printStackTrace();
            } finally {
                if (o != null) {
                    try {
                        o.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }

        /**
         * 
         * @param str
         * @param url
         * @return
         * @throws UnsupportedEncodingException
         */
        public String signOnlineData(String oriData) {

            String signData = this.signData(oriData);
            String lastSignData = "sign=";
            try {
                lastSignData += URLEncoder.encode(signData, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(lastSignData + " lastSignData");
            return lastSignData;
        }

        public static String getHttp(String str, String url) {

            try

            {
                URL server = new URL(url);
                HttpURLConnection httpConnection = (HttpURLConnection) server.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8|SJBPAYGATE:SJBPAYGATE");

                httpConnection.setConnectTimeout(120000);
                httpConnection.setReadTimeout(120000);
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);

                OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(httpConnection.getOutputStream()), "UTF-8");
                out.write(str);
                out.flush();
                out.close();

                byte[] msgBody = null;
                DataInputStream dis = new DataInputStream(httpConnection.getInputStream());
                int length = httpConnection.getContentLength();

                if (length >= 0) {
                    msgBody = new byte[length];
                    dis.readFully(msgBody);
                }

                else {
                    byte[] temp = new byte[1024];
                    int n = 0;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while ((n = dis.read(temp)) != -1) {
                        bos.write(temp, 0, n);
                    }
                    msgBody = bos.toByteArray();
                    bos.close();
                }
                dis.close();
                String data = new String(msgBody, "UTF-8").trim();

                httpConnection.disconnect();
                // System.out.println(data);
                return data;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;

        }
    }

    @Override
    public Object doBiz(Map<String, String> params) throws Exception {
        String reSign = params.get("reSign");
        return unSign(reSign);
    }

}
