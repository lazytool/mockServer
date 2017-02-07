package com.letv.mocker.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XmlUtil {

	public static String toXml(Object obj) {
		// 指定编码解析器,直接用jaxp dom来解释
		// XStream xstream = new XStream(new DomDriver("utf-8"));
		XStream xstream = createXstream();
		// 如果没有这句，xml中的根元素会是<包.类名> 或者说：注解根本就没生效，所以的元素名就是类的属性
		xstream.processAnnotations(obj.getClass());
		StringBuffer xmlstrbuf = new StringBuffer();
		xmlstrbuf.append("<?xml version=\"1.0\"?>");
		xmlstrbuf.append("\n");
		xmlstrbuf.append(xstream.toXML(obj));
		return xmlstrbuf.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T toBean(String xmlStr, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		Object obj = xstream.fromXML(xmlStr);
		return (T) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T toBeanFromFile(String absPath, String fileName,
			Class<T> cls) throws Exception {
		absPath = absPath + Constants.SPLIT;

		fileName = fileName.startsWith(Constants.SPLIT) ? fileName.substring(1)
				: fileName;

		String filePath = absPath + fileName;
		InputStream ins = null;
		try {
			ins = new FileInputStream(new File(filePath));
		} catch (Exception e) {
			throw new Exception("读{" + filePath + "}文件失败！", e);
		}

		XStream xstream = new XStream(new DomDriver("utf-8"));
		xstream.processAnnotations(cls);
		Object obj = null;
		try {
			obj = xstream.fromXML(ins);
		} catch (Exception e) {
			throw new Exception("解析{" + filePath + "}文件失败！", e);
		}
		if (ins != null) {
			ins.close();
		}
		return (T) obj;
	}

	public static boolean toXMLFile(Object obj, String absDir, String fileName) {
		OutputStream ous = null;
		String filePath = absDir + Constants.SPLIT + fileName;
		try {
			String strXml = toXml(obj);
			File file = IOUtil.getFile(absDir, fileName);
			// File file = new File(filePath);
			// if (!file.exists()) {
			// try {
			// file.createNewFile();
			// } catch (IOException e) {
			// System.out.println("创建{" + filePath + "}文件失败!!!");
			// e.printStackTrace();
			// return false;
			// }
			// }

			ous = new FileOutputStream(file);
			ous.write(strXml.getBytes());
			ous.flush();
		} catch (Exception e1) {
			System.err.println("写{" + filePath + "}文件失败!!!");
			e1.printStackTrace();
			return false;
		} finally {
			if (ous != null) {
				try {
					ous.close();
				} catch (IOException e) {
					System.out.println("写{" + filePath + "}文件关闭输出流异常!!!");
					e.printStackTrace();
				}
			}
		}
		System.out.println("数据写入文件[" + filePath + "]完成!");
		return true;
	}

	public static XStream createXstream() {
		return new XStream(new XppDriver() {
			private Map<String, Boolean> cDATATag = new HashMap<String, Boolean>();// key=fieldName

			@Override
			public HierarchicalStreamWriter createWriter(Writer out) {
				return new PrettyPrintWriter(out) {
					Class<?> targetClass = null;
					private String fieldName = null;

					@Override
					public void startNode(String name,
							@SuppressWarnings("rawtypes") Class clazz) {
						super.startNode(name, clazz);
						fieldName = name;
						targetClass = clazz;
						// XStreamCDATA标记的Field加上CDATA标签
						if (!name.equals("xml")) {
							cDATATag = check_CDATA_annotation(targetClass,
									name, cDATATag);
						}
					}

					@Override
					protected void writeText(QuickWriter writer, String text) {
						if (cDATATag.get(fieldName) != null
								&& cDATATag.get(fieldName) == true) {
							text = "<![CDATA[" + text + "]]>";
						}
						writer.write(text);
					}
				};
			}
		});
	}

	private static Map<String, Boolean> check_CDATA_annotation(
			Class<?> targetClass, String fieldAlias,
			Map<String, Boolean> cDATATag) {
		// scan fields
		Field[] fields = targetClass.getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();
			// exists XStreamCDATA
			if (field.getAnnotation(XStreamCDATA.class) != null) {
				cDATATag.put(fieldName, true);
			}
		}
		return cDATATag;
	}
}

/*
 * Location: /Users/fengjing/Documents/mock/WEB-INF/classes/ Qualified Name:
 * com.letv.mock.util.XmlUtil JD-Core Version: 0.6.2
 */