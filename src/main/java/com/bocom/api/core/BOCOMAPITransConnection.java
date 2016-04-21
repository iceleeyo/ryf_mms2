/*jadclipse*/// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.

package com.bocom.api.core;

import com.bocom.api.core.util.ProxyAuthenticator;
import com.ibm.jsse.IBMJSSEProvider;
import com.sun.net.ssl.internal.ssl.Provider;
import java.io.*;
import java.net.*;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

// Referenced classes of package com.bocom.api.core:
//            TransConnection, APIException

public class BOCOMAPITransConnection
    implements TransConnection
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
                TrustManagerFactory tmf = null;
                if(BOCOMAPITransConnection.CURRENT_JVM_VENDOR == BOCOMAPITransConnection.IBM_JVM_VENDOR)
                    tmf = TrustManagerFactory.getInstance("IbmX509", "IBMJSSE2");
                else
                    tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
                sunX509TrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
            }
            catch(Exception exception) { }
        }
    }


    public BOCOMAPITransConnection(boolean useSSL)
    {
        this(useSSL, -1, null, 0, null, null);
    }

    public BOCOMAPITransConnection(boolean useSSL, int proxyType, String proxyhost, int proxyPort, String userName, String password)
    {
        bufLen = 4194304;
        buffer = new byte[bufLen];
        try
        {
            if(useSSL)
            {
                TrustManager myTM[] = {
                    new MyX509TrustManager()
                };
                if(CURRENT_JVM_VENDOR == IBM_JVM_VENDOR)
                {
                    System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.www2.protocol");
                    Security.addProvider(new IBMJSSEProvider());
                } else
                if(CURRENT_JVM_VENDOR == SUN_JVM_VENDOR)
                {
                    System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
                    Security.addProvider(new Provider());
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(proxyType == -1 || proxyhost == null)
        {
            return;
        } else
        {
            Authenticator.setDefault(new ProxyAuthenticator(proxyType, proxyhost, proxyPort, userName, password.toCharArray()));
            return;
        }
    }

    public String sendAndReceive(String srcUrl, String reqData)
        throws APIException
    {
        HttpURLConnection connection = null;
        InputStream in = null;
        try
        {
            URL url = new URL(srcUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE5.5; Windows NT 5.0)");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            java.io.OutputStream os = connection.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.write(reqData);
            pw.flush();
            pw.checkError();
            int contentLen = connection.getContentLength();
            in = connection.getInputStream();
            if(contentLen <= 0)
            {
                contentLen = bufLen;
                int offset = 0;
                do
                {
                    int len = in.read(buffer, offset, contentLen - offset);
                    if(len <= 0)
                        break;
                    offset += len;
                } while(true);
                contentLen = offset;
            } else
            {
                if(contentLen > bufLen)
                {
                    buffer = new byte[contentLen];
                    bufLen = contentLen;
                }
                int offset = 0;
                do
                {
                    int len = in.read(buffer, offset, contentLen - offset);
                    if(len <= 0)
                        break;
                    offset += len;
                } while(true);
                contentLen = offset;
            }
            String resMsg = connection.getHeaderField(0);
            if(resMsg.toLowerCase().indexOf("ok") < 0)
            {
                return null;
            } else
            {
                connection.disconnect();
                in.close();
//                String content = new String(buffer, 0, contentLen);
                String content = new String(buffer, 0, contentLen, "GBK");
                return content;
            }
        }
        catch(Exception e)
        {
            if(connection != null)
                connection.disconnect();
            if(in != null)
                try
                {
                    in.close();
                }
                catch(IOException ioexception) { }
            e.printStackTrace();
            throw new APIException("\u901A\u8BAF\u9519\u8BEF:" + e.getMessage());
        }
    }

    private static int IBM_JVM_VENDOR;
    private static int SUN_JVM_VENDOR;
    private static int CURRENT_JVM_VENDOR = 3;
    int bufLen;
    byte buffer[];

    static 
    {
        IBM_JVM_VENDOR = 1;
        SUN_JVM_VENDOR = 2;
        String VM_VENDOR = System.getProperty("java.vendor");
        if(VM_VENDOR != null)
            if(VM_VENDOR.indexOf("IBM") != -1)
                CURRENT_JVM_VENDOR = IBM_JVM_VENDOR;
            else
            if(VM_VENDOR.indexOf("SUN") != -1)
                CURRENT_JVM_VENDOR = SUN_JVM_VENDOR;
    }


}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\workspaces\myEclipse\workspace\b2bTest\lib\B2BBOCOMMAPI_1.2.3.jar
	Total time: 156 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/