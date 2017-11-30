package org.xqj.shandiandai.utils;

public interface Constants {


	//地址
	public static String URL = "http://www.shoujijiekuan.com/Service/WS4Simple.asmx";
	//命名空间
	public static String nameSpace = "http://chachaxy.com/";
	//图片地址
	public static String piURL = "http://www.shoujijiekuan.com";
	//渠道
	public static String qudao = "android";
	//注册渠道
	public static String channel = "Android-51闪电贷";
	//注册渠道1
	public static String channel1 = "tengxun";
	//App名称
	public static String appName = "51闪电贷";


	// 拍照请求码
	public static int REQUEST_CODE_TAKE_PICETURE = 105;
	// 相册请求码
	public static int REQUEST_CODE_PICK_PHOTO = 106;
	// 拍照权限请求码
	public static int REQUEST_CODE_CAMERA_PERMISSION = 107;
	// 储存权限请求码
	public static int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 108;


	/**
	 *
	 *
	 */
	 class Times {
		/** 启动页显示时间 **/
		public static final int LAUCHER_DIPLAY_MILLIS = 2000;
		/** 倒计时时间 **/
		public static final int MILLIS_IN_TOTAL = 60000;
		/** 时间间隔 **/
		public static final int COUNT_DOWN_INTERVAL = 1000;
	}
}
