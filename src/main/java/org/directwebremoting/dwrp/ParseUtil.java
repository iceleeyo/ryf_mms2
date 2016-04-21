package org.directwebremoting.dwrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.extend.FormField;
import org.directwebremoting.extend.ServerException;
import org.directwebremoting.util.LocalUtil;
import org.directwebremoting.util.Messages;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ParseUtil {
	private static final FileUpload UPLOADER;
	private static final Log log;

	public static String[] splitInbound(String data) {
		String[] reply = new String[2];

		int colon = data.indexOf(":");
		if (colon == -1) {
			log.error("Missing : in conversion data (" + data + ')');
			reply[0] = "string";
			reply[1] = data;
		} else {
			reply[0] = data.substring(0, colon);
			reply[1] = data.substring(colon + 1);
		}

		return reply;
	}

	public Map<String, FormField> parseRequest(HttpServletRequest request)
			throws ServerException {
		boolean get = "GET".equals(request.getMethod());
		if (get) {
			return parseGet(request);
		}

		return parsePost(request);
	}

	private Map<String, FormField> parsePost(HttpServletRequest req)
			throws ServerException {
		Map paramMap;
		if (isMultipartContent(req)) {
			paramMap = UPLOADER.parseRequest(req);
		} else {
			paramMap = parseBasicPost(req);
		}

		if (paramMap.size() == 1) {
			parseBrokenMacPost(paramMap);
		}

		return paramMap;
	}

	public static final boolean isMultipartContent(HttpServletRequest request) {
		if (!("post".equals(request.getMethod().toLowerCase()))) {
			return false;
		}

		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}

		return (contentType.toLowerCase().startsWith("multipart/"));
	}

	private Map<String, FormField> parseBasicPost(HttpServletRequest req)
			throws ServerException {
		Map paramMap = new HashMap();

		BufferedReader in = null;
		try {
//			in = new BufferedReader(new InputStreamReader(req.getInputStream()));
			in = req.getReader();
			while (true) {
				String line = in.readLine();
				if (line == null) {
					if (!(paramMap.isEmpty())) {
						break;
					}

					Enumeration en = req.getParameterNames();
					while (en.hasMoreElements()) {
						String name = (String) en.nextElement();
						paramMap.put(name,
								new FormField(req.getParameter(name)));
					}
					break;
				}

				if (line.indexOf(38) != -1) {
					log.debug("Using iframe POST mode");
					StringTokenizer st = new StringTokenizer(line, "&");
					while (st.hasMoreTokens()) {
						String part = st.nextToken();
						part = LocalUtil.decode(part);

						parsePostLine(part, paramMap);
					}

				} else {
					parsePostLine(line, paramMap);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}

		return paramMap;
	}

	private static void parseBrokenMacPost(Map<String, FormField> paramMap) {
		log.debug("Using Broken Safari POST mode");

		Iterator it = paramMap.keySet().iterator();
		if (!(it.hasNext())) {
			throw new IllegalStateException("No entries in non empty map!");
		}

		String key = (String) it.next();
		String value = ((FormField) paramMap.get(key)).getString();
		String line = key + "=" + value;

		StringTokenizer st = new StringTokenizer(line, "\n");
		while (st.hasMoreTokens()) {
			String part = st.nextToken();
			part = LocalUtil.decode(part);

			parsePostLine(part, paramMap);
		}
	}

	private static void parsePostLine(String line,
			Map<String, FormField> paramMap) {
		if (line.length() == 0) {
			return;
		}

		int sep = line.indexOf("=");
		if (sep == -1) {
			paramMap.put(line, null);
		} else {
			String key = line.substring(0, sep);
			String value = line.substring(sep + "=".length());

			paramMap.put(key, new FormField(value));
		}
	}

	private Map<String, FormField> parseGet(HttpServletRequest req)
			throws ServerException {
		Map convertedMap = new HashMap();
		Map paramMap = req.getParameterMap();

		for (Iterator i$ = paramMap.entrySet().iterator(); i$.hasNext();) {
			Map.Entry entry = (Map.Entry) i$.next();

			String key = (String) entry.getKey();
			String[] array = (String[]) entry.getValue();

			if (array.length == 1) {
				convertedMap.put(key, new FormField(array[0]));
			} else {
				throw new ServerException(Messages.getString(
						"ParseUtil.MultiValues", key));
			}
		}

		return convertedMap;
	}

	static {
		FileUpload test;
		log = LogFactory.getLog(ParseUtil.class);
		try {
			test = new CommonsFileUpload();
			log.debug("Using commons-file-upload.");
		} catch (NoClassDefFoundError ex) {
			test = new UnsupportedFileUpload();
			log.debug("Failed to find commons-file-upload. File upload is not supported.");
		} catch (Exception ex) {
			test = new UnsupportedFileUpload();
			log.debug("Failed to start commons-file-upload. File upload is not supported.");
		}

		UPLOADER = test;
	}
}