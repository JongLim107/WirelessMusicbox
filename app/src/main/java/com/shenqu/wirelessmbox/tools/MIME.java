package com.shenqu.wirelessmbox.tools;

public class MIME {

	//建立一个MIME类型与文件后缀名的匹配表
	private static String[][] MapTable={
	    //{后缀名，    MIME类型}
	    {".3gp",    "video/3gpp"},
	    {".apk",    "application/vnd.android.package-archive"},
	    {".asf",    "video/x-ms-asf"},
	    {".avi",    "video/x-msvideo"},
	    {".bin",    "application/octet-stream"},
	    {".bmp",      "image/bmp"},
	    {".c",        "text/plain"},
	    {".class",    "application/octet-stream"},
	    {".conf",    "text/plain"},
	    {".cpp",    "text/plain"},
	    {".doc",    "application/msword"},
	    {".exe",    "application/octet-stream"},
	    {".gif",    "image/gif"},
	    {".gtar",    "application/x-gtar"},
	    {".gz",        "application/x-gzip"},
	    {".h",        "text/plain"},
	    {".htm",    "text/html"},
	    {".html",    "text/html"},
	    {".jar",    "application/java-archive"},
	    {".java",    "text/plain"},
	    {".jpeg",    "image/jpeg"},
	    {".jpg",    "image/jpeg"},
	    {".js",        "application/x-javascript"},
	    {".log",    "text/plain"},
	    {".m3u",    "audio/x-mpegurl"},
	    {".m4a",    "audio/mp4a-latm"},
	    {".m4b",    "audio/mp4a-latm"},
	    {".m4p",    "audio/mp4a-latm"},
	    {".m4u",    "video/vnd.mpegurl"},
	    {".m4v",    "video/x-m4v"},
		{".mkv",    "video/matroska"},
        {".mov",    "video/quicktime"},
	    {".mp2",    "audio/x-mpeg"},
	    {".mp3",    "audio/x-mpeg"},
	    {".mp4",    "video/mp4"},
	    {".mpc",    "application/vnd.mpohun.certificate"},
	    {".mpe",    "video/mpeg"},
	    {".mpeg",   "video/mpeg"},
	    {".mpg",    "video/mpeg"},
	    {".mpg4",    "video/mp4"},
	    {".mpga",    "audio/mpeg"},
	    {".msg",    "application/vnd.ms-outlook"},
	    {".ogg",    "audio/ogg"},
	    {".pdf",    "application/pdf"},
	    {".png",    "image/png"},
	    {".pps",    "application/vnd.ms-powerpoint"},
	    {".ppt",    "application/vnd.ms-powerpoint"},
	    {".prop",    "text/plain"},
	    {".rar",    "application/x-rar-compressed"},
	    {".rc",        "text/plain"},
	    {".rmvb",    "audio/x-pn-realaudio"},
	    {".rtf",    "application/rtf"},
	    {".sh",        "text/plain"},
	    {".tar",    "application/x-tar"},
	    {".tgz",    "application/x-compressed"},
	    {".txt",    "text/plain"},
	    {".wav",    "audio/x-wav"},
	    {".wma",    "audio/x-ms-wma"},
	    //{".wmv",    "audio/x-ms-wmv"},
	    {".wmv",    "video/x-ms-wmv"},
	    {".wps",    "application/vnd.ms-works"},
	    //{".xml",    "text/xml"},
	    {".xml",    "text/plain"},
	    {".z",        "application/x-compress"},
	    {".zip",    "application/zip"},
	    {"",        "*/*"}
	};

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 *
	 * @param filePath
	 */
	public static String getMIMEType(String filePath)
	{
		String type = "*/*";
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = filePath.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = filePath.substring(dotIndex, filePath.length()).toLowerCase();
		if (end.equals(""))
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (String[] aMapTable : MapTable) {
			if (end.equals(aMapTable[0]))
				type = aMapTable[1];
		}
		return type;
	}

}
