package com.rongyifu.mms.unionpaywap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;




public  class RemoteAccessor {
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpPost httpPost;
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private String content = new String();
	public RemoteAccessor(){
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 60000); //修改连接超时时间 20000 ->60000
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 60000);//修改socket超时时间 20000 -> 60000
		httpClient.getParams().setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, 1024*4);
	}
	public String getSimpleResponse(String url)throws Exception{
		httpGet = new HttpGet(url); 
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		content = httpClient.execute(httpGet, responseHandler);
		return content;
	}
	public String getResponseByPost(String url,String encode,String[] params)throws Exception{
		httpPost = new HttpPost(url); 
		List<org.apache.http.NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();
		for(int i=0; i<params.length/2;i++){
			formParams.add(new BasicNameValuePair(params[i*2], params[i*2+1]));
		}
		HttpEntity entityForm = new UrlEncodedFormEntity(formParams, encode);
		httpPost.setEntity(entityForm);
		
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		
		content = httpClient.execute(httpPost, responseHandler);
		return content;
	}
	class StreamEntity implements ContentProducer{
		public void writeTo(OutputStream outstream) throws IOException {
	        Writer writer = new OutputStreamWriter(outstream, this.encode);
	        writer.write(this.data);
	        writer.flush();
	    }
		public String encode;
		public String data;
	}
	public String getResponseByStream(String url,String encode,String data,String sessionId)throws Exception{
		httpPost = new HttpPost(url); 
		httpPost.setHeader("sessionId", sessionId);
		StreamEntity se = new StreamEntity();
		se.data=data;
		se.encode=encode;
		HttpEntity entity = new EntityTemplate(se);
		httpPost.setEntity(entity);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		content = httpClient.execute(httpPost, responseHandler);
		return content;
	}
	public String getResponseByStream(String url,String encode,String data)throws Exception{
		httpPost = new HttpPost(url); 
		StreamEntity se = new StreamEntity();
		se.data=data;
		se.encode=encode;
		HttpEntity entity = new EntityTemplate(se);
		httpPost.setEntity(entity);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		content = httpClient.execute(httpPost, responseHandler);
		return content;
	}
	public String getResponseByProxy(String url)throws Exception{
		httpClient = new DefaultHttpClient();
		
		do{
			HttpHost proxy = new HttpHost((String)getProxy().get(0), (Integer)getProxy().get(1));
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			httpGet = new HttpGet(url); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			int count = 0;
			try{
					content = httpClient.execute(httpGet, responseHandler);
			}catch(Exception e){
				System.out.println("Remote accessed by proxy["+(String)getProxy().get(0)+":"+(Integer)getProxy().get(1)+"] had Error!Try next!");
			}
			count++;
			if(count>2){break;}
		}while(content.length()==0);
		return content;
	}
	
	public void shutdown(){
		httpClient.getConnectionManager().shutdown();   
	}
	
	
	public static ArrayList getProxy(){
		ArrayList array = new ArrayList();
		ArrayList proxy = null;
		proxy = new ArrayList();proxy.add(new String("221.130.7.72"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("218.59.169.109"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("222.161.137.199"));proxy.add(8080);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("222.162.105.110"));proxy.add(8080);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.130.7.73"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.130.7.82"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("220.248.3.203"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("60.217.248.150"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.176.88.73"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("211.138.124.198"));proxy.add(8080);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("211.138.124.197"));proxy.add(8080);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.130.7.69"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("219.153.71.171"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.176.88.94"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("222.77.14.55"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.176.88.92"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("220.248.3.202"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.130.7.70"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.130.7.93"));proxy.add(80);array.add(proxy);
		proxy = new ArrayList();proxy.add(new String("221.130.7.68"));proxy.add(80);array.add(proxy);
		
		int item=(int)(Math.random()*array.size());   
		ArrayList list = (ArrayList)array.get(item); 
		return (ArrayList)array.get(item);
	}

    public final static void main(String[] args) throws Exception {
    	/**********************妯℃嫙鍟嗘埛鏌ヨ璁㈠崟****************************/
//    	String querydata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><upbp application=\"MTransInfo.Req\" version =\"1.0.0\" sendTime =\"20120314150700\" sendSeqId =\"00000888888001\"><merchantId>5101200070003100001</merchantId><merchantOrderId>DEPO0128201203353793</merchantOrderId><merchantOrderTime>20120309120000</merchantOrderTime><msgExt></msgExt><misc></misc></upbp>";
//    	/*********************缁撴潫**********************************/
//    	
//    	/**********************妯℃嫙鍟嗘埛棰勪笅鍗�****************************/
    	RemoteAccessor remoteAccessor = new RemoteAccessor();
//    	String data = "MzAxMzMwMTczMTEwMDEy|Zgx2VqndnNLehQ4E4AhKcMw1Lqa8Al++Zg1xkXAYpJDZx05DH4/tneFgYl0kMk+ljDN53SjeciiGgYbzJ8M/GdE3JEX1OlU7YsZ+4Y3eNQJlM1MEA+zry0pDq/1otX4/39rr5hxgjwgbHuK2guPxP1QSWAP/PHNa2D/tUe4rPv0=|to/rWvcxXQjQ84zuqpp5jmp8xYVe0bQA6dCVQY9ii8ArZPnJA9dMgDicqi0tGrttQfglG23ZOHeIqHG+dmyyVxap2pxw/biX3UkmMg0Xd8Nern0zCvZeheEEm9GlibkDdXTSxBjr7+3YpwBcmOvnpwTvXU/p0eU1xJAlx7W99oiceTNwN7+X5NSSsamk3TvvCfJyeHEiTmfEkCXHtb32iJ0sNdz2NEKEW6PJ/XnEa0UgSsOG0+1BWDjJV2oOAUpzJ7KlN+rLgWXKbMjJtKMfcDz3EBWc+kUVdmMvdZW8ryjMTBVj98A6siDngOsMCaIX3UkmMg0Xd8MZ/HnWGaLbUHZjL3WVvK8ozEwVY/fAOrJaambYjtMEVj5vVzsJGWLJymzIybSjH3CTvDFTNaEWCkFU4Vlswqtp4QK4gn6yWKxa6PcX5z6wj3gn/KlNswWPNZMvmQgr91dvpv+2r4WHN2hMujrX2h7X/q+lWthpakny/c9/ZGKKJpeqtK4DHCJtvbfWxnLYaQXsvXcAsGDC87LJetTb+BHVE1CvefE8rTO+wgLYwKELN6APpjdmIBI7QNqHGSXCdQ6Fd9AzQB6y+zirPewayJtq4r2UAPilcIrtvKqEdh/1Rne+cuiWd9YSgys/jNFSOys=";
    	String data = "ODM5MjQ1ODAwMjE1|mzwiDh6bt6yrnFv3FI/PVxY5R/mZMH2Krz7eDveAmflh8C5JgH1JdSPEqHZSSoBQ35h0hIFimF+NPyZ7Aalzk0h3Uoq6u2GT1N6TBpZu5ujAynpUFxt0WJsrRQqn45BR8yv4tEXXfCe4y7FuA1u1AeCIDjXsahbRCHcvvI/WbwQ=|0UaryujbS/V1jn4zaG3zAzJOqPujUa8kqHCteFFfDMnIZU8oPuJr+uDJFx+qUgZWAJy2ztBYCRFYTSJXWkrw0NmREebn3XzFmFMLJ2UcWWuqcV7GZAOYdXen5ghgJlV2cpuECtcxi2wdeteh0MhIwU1hBrQUkdHpiA1KJv5N1nCARkexBqe4n9tWFiWWABUgN026cd+pAfQAFD9ExnBkjPNjbus30+B+ih3Kn5gxlNp6D4K0KXFX864/l5Cw7Klh91HOtDVyICB1tPP3tjn0LM2xV1yXH9P+oruyHQHHiW4NlJ+dLLLrROXph/jvSKrgcgzXc2YC4ef/9WCNG1WxXx6zCcIe5sL1Ti3FYRY04ac6HLHzIjA+BqRI53GQ+TPiC+tToYpCt/mts/vrkp9WynDBW38gRgqLlsArpPIYSflJMs9fECKFjVPPyj2BMAimFmCSNdX2oNZeJYRKcfYAnpH3LScMPM8qeoyWYx6PMlsGJdf/O+SK6P/aVzKDv+IpFxrxACOyqp7qEv0a93gKJYIKA9JcUcb7tfEsUIXMEp+iySxkurISWy/K6AKym9iJawvL9muyOAzI77GAj8UZ8JbjNarZXie/2cAf4lROOhUuXVB1mWDlTo/xIZJHQktLbXWKCG+rccpdqC7rnI8Kqr9OIRqBwHB2lPOknxztNQ6P8SGSR0JLSwUqcJ5o08psF8eK4X0jgkpm4FZY2vaVyYFNwgXQjAqnoEP6TSlE2BzyaYEJ3XefUeVPiZjfLcSnG+b9sYthp44Vt+n0ONAvD9oYDDReCZqN";
    	remoteAccessor.getResponseByStream("http://mt.bypay.cn/shoppingMall/notifyDo", "utf-8", data);
//    	
//    	String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><upbp application=\"MGw.Req\" version =\"1.0.0\" sendTime =\"20120301165700\" sendSeqId =\"00000888888000\"><frontUrl>test</frontUrl><merchantOrderDesc></merchantOrderDesc><misc></misc><gwType>01</gwType><transTimeout>20120217050100</transTimeout><backUrl>test</backUrl><merchantOrderCurrency>156</merchantOrderCurrency><merchantOrderAmt>1</merchantOrderAmt><merchantId>303310048990001</merchantId><merchantOrderTime>20120309110300</merchantOrderTime><merchantOrderId>100000037</merchantOrderId><msgExt>test</msgExt><merchantUserId></merchantUserId><mobileNum>15618495718</mobileNum><userName></userName><idType></idType><idNum></idNum><cardNum></cardNum></upbp>";
//    	BASE64Encoder encoder = new BASE64Encoder();
//    	BASE64Decoder decoder = new BASE64Decoder();
//    	String merchantId = encoder.encodeBuffer("5101200070003100001".getBytes());
//    	System.out.println("merchantId is ==>"+merchantId);
//    	String mm = MD5.getHashString("654321");
////    	String privateKey = EncDecUtil.getCertKey("123456","d://303310048990001.pfx");
//    	String privateKey = EncDecUtil.getCertKey("123456","d://5101200070003100001.pfx");
//    	String desKey = encoder.encodeBuffer(RSACoder.encryptByPrivateKey(mm.getBytes(), decoder.decodeBuffer(privateKey)));
//    	System.out.println("desKey is ==>"+desKey);
//    	byte[] bodyb = DesUtil.encrypt(data.getBytes(), mm.getBytes());
//    	String miBody = encoder.encode(bodyb);
//    	String xml = merchantId + "|" + desKey + "|" + miBody;
//    	System.out.println(xml);
//    	String re = remoteAccessor.getResponseByStream("http://localhost:8080/upowp/mcom.html", "utf-8", xml);
//    	System.out.println(re);
//    	String[] strArr = re.split("\\|");
//    	String md5Xml = strArr[2];
//    	System.out.println(new String(decoder.decodeBuffer(md5Xml),"utf-8"));
    	/*********************缁撴潫**********************************/


    	
    	
//    	String testStr = "MzAzMzEwMDQ4OTkwMDAx|S/UoZISP2laXw1VEa39nb23K+B/4D1FVycECxV/xJTAwAqgugHRkoxk3S/kLnc6WozR7wFd3H2ahhsvxMnaiL99I8DIZ7ekkdUUOINGaApae5D7vGgZ4Up0jr9fTXiAzTE0FEuvyma1Pk/wI5/31kLR6fkKMsqVfm01258oenVA=|JxtUQ2KvtaBMYvowS+YxpCrZwcPytUt/+p7MCHWxjf5reLkthfLKKV0i2oiV8Z4Tp/0XBvbL9A0Rtsfv8uNiZCCEtxYH3HzsunAfFDmxahxepOJl+KVm0ZMbzBiuENSHJC13pfwnv5l30PW2PWuiEXEayP+1WF8mnzIZZJOATeqFuVPIJn9mkVIA2y22Oz05gSpxPRIlddh/fPbDRHDH+O4c5VBnvmbFRAg7slJriMMd4LgAw/5ei3Krxkb6FGAIwdOQmYY1gqrP4V+IcxxJGdWfWIH1XRbYHeC4AMP+XouQr9wuvo9eGyMBJlkRFXj7P3Wxco6pibyv94jkuKOe98DHIn4/3dkXeJlLIDLykdXTCODiou/3OFxNEd0xB2r/68bL4yBAXAtNGpXYHnnwcfuo9R0a1pUCqDts1So/OfP115TeskYsBkhHBPuELiVRJlfpzt5MoYWO4Ga0QCadt9y8CdiLm9akrQALk3hiTMgcjt/nc0JKVegePpc8f8FnKnhKMdSWxqge/LNZOwlVup+5QE402YQtb97KTUiai1RQOyJvyVIiW9AyoMRtPFhXVQtEs8yOLQg2Ba4GR27q2AoYZ9P2byk5U6qMlmtyhEXqSk/FkEbBwAD5YejuRjyqJBEW2+PzkF23tErtZ3gPlR+8VO9RDuDSD1To5rj6DVwL4wz7q3P/NqS181tnx75Q3oKLL/G0NWc7TDvb85hBokYrDEy4oRkXlbwfP9ksDQwRpQ/PXGAcN6jgRE+Q2rxCe74LdWIZ6bTYuQ9uduOHtP4GpB62xsCh";
//    	String[] strArr = testStr.split("\\|");
//    	byte[] b = decoder.decodeBuffer(strArr[2]);
//    	String key = "206cb6f01d35a60f6a16969c9f4e7a70";
////    	byte[] rb = DesUtil.decrypt(b, key.getBytes());
//    	BaseCryptor cryptor = new BaseCryptor();
//    	String rb = cryptor.decrypt3DES(b, key.getBytes());
//    	System.out.println(rb);
    	
//    	String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><upbp application=\"MGw.Resp\" version=\"1.0.0\" sendTime=\"20120305164537\" sendSeqId=\"201203155047\"><merchantName>鍥涘窛鐢典俊</merchantName><merchantId>303310048990001</merchantId><merchantOrderId>DEPO0128201203155047</merchantOrderId><merchantOrderTime>20120305164537</merchantOrderTime><merchantOrderAmt>1</merchantOrderAmt><merchantOrderCurrency>156</merchantOrderCurrency><gwInvokeCmd>http://210.51.61.172:8180/upowp/gw/MTIwMzA1MTY1MzAzMDQ5OTM=</gwInvokeCmd><merchantUserId></merchantUserId><mobileNum>15388157741</mobileNum><userName></userName><idType>01</idType><idNum></idNum><cardNum>null</cardNum><msgExt></msgExt><misc>15388157741</misc><respCode>0000</respCode><respDesc>澶勭悊鎴愬姛</respDesc></upbp>";
//    	System.out.println(MD5.getHashString(str));
    	
//    	String str = "YVqKXil9RFReE88reTY1rE0jJLzMdaK7vHVe9SntkgOMVcKQR3lH8Jbl9Zs2xdwgYv2OkApGx1b6YnPfGaq5oPvocTIjHQinDqBesZ0/IeNmA9eixb+0ukazUWIXroihWVZgG2BJfxEENROgheqiTRw4aExZRVzsaRLxs3wwBPqNaDckQtaxRXH2MGRkkqYrOeYA2e435YlKyXomF7CluHQBDYEh6TnJE5LRAwOBGi8aZz9lzhNbXiayk9kJ73eVUDP0xMLVw/mAmdDzdzDE+Ybq9fSDvGJvGmc/Zc4TW16Tv0KpU0ffU8CWOQrC5TSPaRLxs3wwBPpLERVJIr/d3o1zMCjunH/VS/8+1nCjfmOSzCD/Yf8tL6WN98v6phCulwZ422A8Cj4XVXZX/I/dJr7wtFkFCsYCe+tMkmfzp2UH6Gf9T2OW+L4zKJluiToJTZOO5dBhSYYaZz9lzhNbXliRyZBBI/SZiSXEg9zFv75SqYIfiMFIsaeHJGTT5LmUe9vBf6fEFYeZbB/y3hapl5ygk83F8GUSlxr6Xlvjg6jWFZPdPUCChFa6CwFJ616gT8aEcrJZKatKhx7pqVT3iU1Kgds7JIUsq0rRgelZdaDwu4/AF8hAqKxEN1zx/0x+Hi3jCum7SYnJIM2TFdUhlIklxIPcxb++JKbg2HYAemSWzdMYwD5DD+75fT2OzOLMk8hAgwvd8y5KqPjPr3D154+9Gvnf065UzuEP8DiGDw9Cg1wSrZbdK7nhGoXddco82Ony3O00lexbw8jLX2ejoPTh7O1ijnRtym2X3FF9omXHyvDk/pxYg2mPl0FRkgBTy+aRegE0cOjRTtF5mgoKeGTqgGWlpJcAi20UZ6BIDw4uwxLz3hVyf5EdGdSqFcDTdzpEjhtENYC4OugSgqWk5ECJC4/BGF62BH2vZpHtj4IMpytEBMhsj2YT/9F71yAYfFtBlf8DoI+ANjOUUzxFHy6p75DDXKeMszBeInYsr4g=";
//    	String desKey = "A315BZ5A3C85E86KK887WSWS";
//    	byte[] b = new BASE64Decoder().decodeBuffer(str);
//    	System.out.println(new String(DesUtil.decrypt(b, desKey.getBytes()),"UTF-8"));
    }
}
