package com.letv.mocker.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {
	public static String readLine2String(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer tempString = new StringBuffer();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				tempString.append(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "error:" + e.getMessage();
		}
		return tempString.toString();
	}

	public static List<String> readLineForStringArray(InputStream in) {
		try {
			ArrayList<String> rsList = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line;
			while ((line = br.readLine()) != null) {
				rsList.add(line);
			}
			br.close();
			return rsList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static boolean write(String contants, String codeType, String dirpath, String fileName) {
		PrintWriter writer = null;
		try {
			File file = getFile(dirpath, fileName);
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), codeType);

			writer = new PrintWriter(out, true);
			writer.write(contants);
			System.out.println("数据写入文件[" + file.getAbsolutePath() + "]完成！");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static File getFile(String dirpath, String fileName) throws Exception {
		File file = new File(dirpath);
		if (!file.exists()) {
			file.mkdirs();
			System.out.println("路径不存在,创建成功:" + file.getAbsolutePath());
		}
		if (fileName != null) {
			file = new File(dirpath + Constants.SPLIT + fileName);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("文件不存在,创建成功:" + fileName);
			}
		}
		return file;
	}

	public static void main(String[] args) throws Exception {
		getFile("E:\\fj\\fjjj\\aa", null);
	}
}

/*
 * Location: /Users/fengjing/Documents/mock/WEB-INF/classes/ Qualified Name:
 * com.letv.mock.util.IOUtil JD-Core Version: 0.6.2
 */