package com.xh.util;
import java.net.InetAddress;
import java.util.Random;
/**
 * 32位UUID生成器 
 * @author tom
 * @date 2016年10月26日
 */
public class KeyGenerator {
	
	private static StringIDGenerator stringIdGenerator = new StringIDGenerator();
	/**
	 * 生成32位字符串ID
	 * @return
	 */
	public static String generateID() {
		return stringIdGenerator.generateID();
	}
	
	public static class StringIDGenerator{
		private static short counter = (short) 0;

		private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

		private static final String areaCode="";

		private static final int IP;
		static {
			int ipadd;
			try {
				ipadd = toInt(InetAddress.getLocalHost().getAddress());
			} catch (Exception e) {
				ipadd = 0;
			}
			IP = ipadd;
		}

		private static int toInt(byte[] bytes) {
			int result = 0;
			for (int i = 0; i < 4; i++) {
				result = (result << 8) - Byte.MIN_VALUE + bytes[i];
			}
			return result;
		}

		protected int getIP() {
			return IP;
		}

		/**
		 * Unique across JVMs on this machine (unless they load this class in the
		 * same quater second - very unlikely)
		 */
		protected int getJVM() {
			return JVM;
		}

		/**
		 * Unique in a millisecond for this JVM instance (unless there are >
		 * Short.MAX_VALUE instances created in a millisecond)
		 */
		private static synchronized short getCount() {
			if (counter < 0)
				counter = 0;
			return counter++;
		}

		/**
		 * Unique down to millisecond
		 */
		protected short getHiTime() {
			return (short) (System.currentTimeMillis() >>> 32);
		}

		protected int getLoTime() {
			return (int) System.currentTimeMillis();
		}

		/**
		 * 格式化成8位
		 */
		protected String format(int intval) {
			String formatted = Integer.toHexString(intval);
			StringBuffer buf = new StringBuffer("00000000");
			buf.replace(8 - formatted.length(), 8, formatted);
			return buf.toString();
		}

		/**
		 * 格式化成4位
		 */
		protected String format(short shortval) {
			String formatted = Integer.toHexString(shortval);
			StringBuffer buf = new StringBuffer("0000");
			buf.replace(4 - formatted.length(), 4, formatted);
			return buf.toString();
		}

		/**
		 * 行政区号 6位
		 */
		protected String getAreaCode() {
			return areaCode;
		}

		/**
		 * 以行政区号为前缀 如果行政区号没有设置 则以IP为前缀
		 */
		protected String getPrefix() {
			String prefix = getAreaCode();
			if (prefix == null || prefix.equals("")) {
				prefix = format(getIP());
			}else{
				if(prefix.length()>8){
					prefix = prefix.substring(0, 8);
				}
			}
			return prefix;
		}
		
		/**
		 * 生成ID 
		 */
		public String generateID() {
			return new StringBuffer(32)
					.append(getPrefix())
					.append(format(getJVM()))
					.append(format(getHiTime()))
					.append(format(getLoTime()))
					.append(format(getCount()))
					.toString();
		}
		
	}
	
	/**
	 * 
	 * <p>
	 * 生产长度为length的随机字母数字混合字符串
	 * </p>
	 * @param length
	 *            指定字符串长度
	 * @return
	 */
	public static String getCharacterAndNumber(int length) {
		StringBuilder val = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 取得大写字母还是小写字母
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val.append((char) (choice + random.nextInt(26)));
			}
			// 数字
			else if ("num".equalsIgnoreCase(charOrNum)) {
				val.append(String.valueOf(random.nextInt(10)));
			}
		}
		return val.toString();
	}
	
}
