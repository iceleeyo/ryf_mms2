package com.bocom.netpay.b2cAPI;

import com.rongyifu.mms.common.TrustSSL.TrustAnyTrustManager;
import com.rongyifu.mms.utils.LogUtil;
import com.sun.net.ssl.internal.ssl.Provider;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class B2CConnection
{
    class MyX509TrustManager
        implements X509TrustManager
    {

        public X509Certificate[] getAcceptedIssuers()
        {
            try
            {
                if(sunX509TrustManager != null)
                    return sunX509TrustManager.getAcceptedIssuers();
                else
                    return null;
            }
            catch(Exception exception)
            {
                return null;
            }
        }

        public void checkClientTrusted(X509Certificate arg0[], String arg1)
            throws CertificateException
        {
            int i = 0;
            i++;
        }

        public void checkServerTrusted(X509Certificate arg0[], String arg1)
            throws CertificateException
        {
            int i = 0;
            i++;
        }

        X509TrustManager sunX509TrustManager;

        public MyX509TrustManager()
        {
            try
            {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
                sunX509TrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
            }
            catch(Exception exception) { }
        }
    }


    public B2CConnection(boolean useSSL)
    {
        bufLen = 1048576;
        buffer = new byte[bufLen];
        try
        {
            if(useSSL)
            {
                TrustManager myTM[] = {
                    new MyX509TrustManager()
                };
                System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
                Security.addProvider(new Provider());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public B2CConnection(boolean useSSL, int num)
    {
        bufLen = num * 1024 * 1024;
        buffer = new byte[bufLen];
        try
        {
            if(useSSL)
            {
                TrustManager myTM[] = {
                    new MyX509TrustManager()
                };
                System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
                Security.addProvider(new Provider());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String sendAndReceive(String srcUrl)
    {
    	return postRequest(srcUrl);
    }

    int bufLen;
    byte buffer[];
    
    public String postRequest(String srcUrl) {
    	HttpsURLConnection conn = null;
		InputStream in = null;
		String str_return = "";
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			URL console = new URL(null, srcUrl, new sun.net.www.protocol.https.Handler());
			conn = (HttpsURLConnection) console.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/MSIE");  
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.connect();
			InputStream is = conn.getInputStream();
			DataInputStream indata = new DataInputStream(is);
			String ret = "";

			while (ret != null) {
				ret = indata.readLine();
				if (ret != null && !ret.trim().equals("")) {
					str_return = str_return + new String(ret.getBytes("ISO-8859-1"), "GBK");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				conn.disconnect();
			try {
				if(in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		LogUtil.printInfoLog("B2CConnection", "postRequest", "bocomm query response:\n" + str_return);
		return str_return;
	}
    
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}