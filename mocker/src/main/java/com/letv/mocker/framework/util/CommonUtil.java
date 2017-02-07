package com.letv.mocker.framework.util;
//package com.letv.mock.util;
//
//import java.io.UnsupportedEncodingException;
//import java.math.BigDecimal;
//import java.security.NoSuchAlgorithmException;
//import java.sql.Timestamp;
//import java.text.Format;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class CommonUtil {
//    protected static final Logger requestLogger = LoggerFactory
//            .getLogger("requestTimeLog");
//
//    public static ThreadLocal<List<String>> logList = new ThreadLocal<List<String>>();
//    public static final String UTF8 = "UTF-8";
//    public static final String GB18030 = "GB18030";
//    public static final String GBK = "gbk";
//    public static final String TIME_ZONE_UTC = "UTC";
//    public static final String downloadkey = "itv12345678!@#$%^&*";
//    public static final String TV_PLATFROM = "tv";
//
//    public static BigDecimal round(BigDecimal value) {
//        return value.setScale(2, 4);
//    }
//
//    public static String nullToEmpty(String src) {
//        return src == null ? "" : src;
//    }
//
//    public static String join(Collection<String> collectionOfStrings,
//            String delimeter) {
//        StringBuilder result = new StringBuilder();
//        for (String s : collectionOfStrings) {
//            result.append(s);
//            result.append(delimeter);
//        }
//        return result.substring(0, result.length() - 1);
//    }
//
//    public static Boolean checkSig(Map<String, Object> params) {
//        String sig = (String) params.get("sig");
//        params.remove("sig");
//        String md5sig = getMd5Str(params, "itv12345678!@#$%^&*");
//        if (!md5sig.equalsIgnoreCase(sig)) {
//            return Boolean.valueOf(false);
//        }
//
//        return Boolean.valueOf(true);
//    }
//
//    public static String getMd5Str(Map<String, Object> params, String secrectKey) {
//        List<String> listStr = new ArrayList<String>();
//        String md5 = "";
//
//        String key_value = "";
//        Iterator<String> it = params.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            String value = new StringBuilder().append(params.get(key))
//                    .append("").toString();
//            listStr.add(new StringBuilder().append(key).append("=")
//                    .append(value).toString());
//        }
//        Collections.sort(listStr);
//        for (String str : listStr) {
//            key_value = new StringBuilder().append(key_value).append(str)
//                    .append("&").toString();
//        }
//        String md5str = new StringBuilder()
//                .append(key_value.substring(0, key_value.length() - 1))
//                .append(secrectKey).toString();
//        try {
//            md5 = MessageDigestUtil.md5(md5str.getBytes("UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return md5;
//    }
//
//    public static void printLog(Long totalTime, List<String> logList) {
//        try {
//            StringBuffer sb = new StringBuffer();
//            if ((totalTime.longValue() > 200L) && (logList != null)) {
//                sb = new StringBuffer();
//                for (String log : logList) {
//                    sb.append(log);
//                }
//                requestLogger.info(new StringBuilder()
//                        .append(Thread.currentThread().getName())
//                        .append("   total:").append(totalTime)
//                        .append(sb.toString()).toString());
//            }
//        } catch (Exception e) {
//            requestLogger.error(e.getMessage(), e);
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            System.out
//                    .println(MessageDigestUtil
//                            .md5("timestamp=1403581252084&vrsVideoInfoId=20134255itv12345678!@#$%^&*"
//                                    .getBytes("UTF-8")));
//
//            System.out
//                    .println(MessageDigestUtil
//                            .md5("timestamp=1403581252084&videoid=20149708itv12345678!@#$%^&*"
//                                    .getBytes("UTF-8")));
//
//            String time = "2014-05-13 18:30:52";
//
//            Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date d = null;
//            try {
//                d = (Date) f.parseObject(time);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            Timestamp ts = new Timestamp(d.getTime());
//            System.out.println(d.getTime());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
///*
// * Location: /Users/fengjing/Documents/mock/WEB-INF/classes/
// * Qualified Name: com.letv.mock.util.CommonUtil
// * JD-Core Version: 0.6.2
// */