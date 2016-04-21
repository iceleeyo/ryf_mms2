package com.rongyifu.mms.common;

import java.io.*;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class TrustSSL {
	public static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("--------start--------");
		InputStream in = null;
		OutputStream out = null;
		byte[] buffer = new byte[4096];
		String str_return = "";
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
//			URL console = new URL("https://pbank.psbc.com/payment/main");
			URL console = new URL("https://ebanktest.95559.com.cn/corporbank/NsTrans?dse_operationName=cb2202_queryOrderOp&reqData=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22gb2312%22%3F%3E%3CBOCOMB2C%3E%3CopName%3Ecb2202_queryOrderOp%3C%2FopName%3E%3CreqParam%3E%3CmerchantID%3E301310063009501%3C%2FmerchantID%3E%3Cnumber%3E1%3C%2Fnumber%3E%3Cdetail%3E1%3C%2Fdetail%3E%3Corders%3E6807908%3C%2Forders%3E%3C%2FreqParam%3E%3C%2FBOCOMB2C%3E&signData=MIIHfAYJKoZIhvcNAQcCoIIHbTCCB2kCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCBmUw%0D%0AggM6MIICo6ADAgECAgQx5gASMA0GCSqGSIb3DQEBBQUAMDMxCzAJBgNVBAYTAkNOMRAwDgYDVQQK%0D%0AEwdCT0NUZXN0MRIwEAYDVQQDEwlCT0NUZXN0Q0EwHhcNMDgxMTAzMDU1MzA2WhcNMTYxMTAzMDU1%0D%0AMzA2WjB3MQswCQYDVQQGEwJDTjEQMA4GA1UEChMHQk9DVGVzdDERMA8GA1UECxMIQkFOS0NPTU0x%0D%0AEjAQBgNVBAsTCU1lcmNoYW50czEvMC0GA1UEAxMmMDQwQDAwNzQ3MjYwNTItMUBbMzAxMzEwMDYz%0D%0AMDA5NTAxXUAwMDAwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALlndnVpDm1RvjRE3fOq9rwL%0D%0A6Z9%2F71zXAJmR%2FAWVJK%2BvZpAyyoDbPtZ3y9lLO7l3e0ff%2F8ieT%2BeZOiCsrLOdm9mYlaJ0%2FUdOzxX4%0D%0AJfySphmaaW4aKmItNv%2BLoaULCyJ9fbdekPmX57ChZilm6CT85M0oQRxH7XsdlNcGaVK5CluVAgMB%0D%0AAAGjggEVMIIBETARBglghkgBhvhCAQEEBAMCBaAwHwYDVR0jBBgwFoAU44FmAHvJjz9ad%2BBtXOtB%0D%0AQsdIWhAwPwYDVR0gBDgwNjA0BgRVHSAAMCwwKgYIKwYBBQUHAgEWHmh0dHA6Ly8xODIuMTE5LjE3%0D%0AMS4xMDYvY3BzLmh0bTBPBgNVHR8ESDBGMESgQqBApD4wPDELMAkGA1UEBhMCQ04xEDAOBgNVBAoT%0D%0AB0JPQ1Rlc3QxDDAKBgNVBAsTA2NybDENMAsGA1UEAxMEY3JsMTALBgNVHQ8EBAMCBsAwHQYDVR0O%0D%0ABBYEFBBO2I5A1FenJwEGAzQ3DBCdXKa0MB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDAN%0D%0ABgkqhkiG9w0BAQUFAAOBgQBs4lbo0k8G0qUcfMHT3dYpqE2qsXVm9bKwu%2F9FiTfFV0gX1mxRZJxZ%0D%0APLnGnK%2FYB7OkTZF9Dwn0ZUNX5c0ipBlA18ay4U%2Byl35tJLskeeZGTlozvm7g26Y2VEfdhLPEB49u%0D%0At90OfEoxGFxuVQQHSAJ2qPEJ9TszenZQnDyU6afKPDCCAyMwggKMoAMCAQICBDHmAAEwDQYJKoZI%0D%0AhvcNAQEFBQAwMzELMAkGA1UEBhMCQ04xEDAOBgNVBAoTB0JPQ1Rlc3QxEjAQBgNVBAMTCUJPQ1Rl%0D%0Ac3RDQTAeFw0wODEwMjgwODU0MjZaFw0yODEwMjgwODU0MjZaMDMxCzAJBgNVBAYTAkNOMRAwDgYD%0D%0AVQQKEwdCT0NUZXN0MRIwEAYDVQQDEwlCT0NUZXN0Q0EwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJ%0D%0AAoGBAJE%2FIQ9wce3NKztUfiqSJRkz0F5DY8QW1z7qsWBKSN2mtdl7A38Kk644jJ1I%2FWJXmz8fM8KU%0D%0AfyXsZ4RiNo1eH08AgczXU3H2g0YgEvHQC5hL2ybTw8wMvMbTHd1R0O1UG4x29Mo40kwQiQM9H22v%0D%0A1qCJxDQaj76VAApOkzWhxx%2BbAgMBAAGjggFCMIIBPjA5BggrBgEFBQcBAQQtMCswKQYIKwYBBQUH%0D%0AMAGGHWh0dHA6Ly8xODIuMTE5LjE3MS4xMDY6MTIzMzMvMBEGCWCGSAGG%2BEIBAQQEAwIABzAfBgNV%0D%0AHSMEGDAWgBTjgWYAe8mPP1p34G1c60FCx0haEDAPBgNVHRMBAf8EBTADAQH%2FMD8GA1UdIAQ4MDYw%0D%0ANAYEVR0gADAsMCoGCCsGAQUFBwIBFh5odHRwOi8vMTgyLjExOS4xNzEuMTA2L2Nwcy5odG0wTwYD%0D%0AVR0fBEgwRjBEoEKgQKQ%2BMDwxCzAJBgNVBAYTAkNOMRAwDgYDVQQKEwdCT0NUZXN0MQwwCgYDVQQL%0D%0AEwNjcmwxDTALBgNVBAMTBGNybDEwCwYDVR0PBAQDAgH%2BMB0GA1UdDgQWBBTjgWYAe8mPP1p34G1c%0D%0A60FCx0haEDANBgkqhkiG9w0BAQUFAAOBgQCEcmMPUlDZ0%2B8bjDdUldhdL%2B4NdYwWiz2WQg7VIF7F%0D%0AcL9L5mAoh%2F5QmvNRyOEpYOPdHbKxko82nn23h9f8P%2B0Rrh4abeAD8VMs%2FLA0kj1FMBMdUx3IfXwW%0D%0A73Hl7NebcD8F3ViBe01LVFWgyteIDFy3%2Fank8loposjsz5ocWX6uCTGB4DCB3QIBATA7MDMxCzAJ%0D%0ABgNVBAYTAkNOMRAwDgYDVQQKEwdCT0NUZXN0MRIwEAYDVQQDEwlCT0NUZXN0Q0ECBDHmABIwCQYF%0D%0AKw4DAhoFADANBgkqhkiG9w0BAQEFAASBgKRQJy0Un1P%2FoO0yU8vVRtwZIYnCRsqkFHLZ3Yb2fls1%0D%0AVJ94tQq%2BquNVj%2FGD8e5VYkLhd6x0Hyuc2p1NSsBEyj9Ux8aaOgglHxDErJmVXVDKA%2B29P4IORm3s%0D%0Aw9bZEndVW%2FIOEOW%2BNdrhFanpcYLUJaiyeRZXwGTGDYp7UOvCmSB4");
//			URL console = new URL("https://211.144.193.27:224/mms/ryf/recoInte?mid=211&operId=1&operPass=1&billType=1&dateType=1&date=20130304");
			HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
//			conn.setRequestProperty("ContentType","text/xml;charset=utf-8"); 
//			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.connect();
			InputStream is = conn.getInputStream();
			DataInputStream indata = new DataInputStream(is);
			String ret = "";

			while (ret != null) {
				ret = indata.readLine();
				if (ret != null && !ret.trim().equals("")) {
					str_return = str_return + new String(ret.getBytes("ISO-8859-1"), "UTF-8");
				}
			}
			conn.disconnect();
		} catch (ConnectException e) {
			System.out.println("ConnectException");
			System.out.println(e);
			throw e;

		} catch (IOException e) {
			System.out.println("IOException");
			System.out.println(e);
			throw e;

		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}
		System.out.println("--------end--------");
		System.out.println("res="+str_return);
	}
}