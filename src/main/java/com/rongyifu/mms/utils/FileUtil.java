package com.rongyifu.mms.utils;

import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {
	public static String getPrivateKey(String file) throws IOException {
			byte[] pri = new byte[1024];
			FileInputStream inpri = new FileInputStream(file);
			inpri.read(pri);
			inpri.close();
			String privateKey = new String(pri, "UTF-8");
			return privateKey;
	}
}
