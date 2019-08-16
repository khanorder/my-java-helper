package pe.me.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.seruco.encoding.base62.Base62;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oss.kisa.seed.KISA_HANDLE_CBC;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
	
	private static final Logger logger = LoggerFactory.getLogger(Helper.class);
	private final KISA_HANDLE_CBC ks_seed = new KISA_HANDLE_CBC();
	
	public void render404 (HttpServletRequest req, HttpServletResponse res) throws Exception {
		String message = "페이지를 찾을 수 없습니다.";
		if (null == req.getHeader("x-requested-with")) {
			req.setAttribute("message", message);
			req.getRequestDispatcher("/common/error.jsp").forward(req, res);
		} else {
			HashMap<String, Object> map = new HashMap<>();
			map.put("result", false);
			map.put("callback", message);
			map.put("message", message);
			res.setContentType("application/json");
			res.setCharacterEncoding("UTF-8");
			res.getWriter().println(encodeJson(map));
		}
	}
	
	public boolean isEmptyMap(String key, Map<String, ?> map) throws Exception {
		if (!map.containsKey(key)) return true;
		return null == map.get(key) || "".equals(map.get(key));
	}
	
	public boolean isBlankMap(String key, Map<String, String> map) throws Exception {
		if (!map.containsKey(key)) return true;
		return null == map.get(key) || StringUtils.isBlank(map.get(key));
	}
	
	public boolean isBlank(String str) throws Exception {
		return null == str || StringUtils.isBlank(str);
	}
	
	public boolean isNumeric(String str) throws Exception {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			logger.debug(e.getMessage());
			return false;
		}
	}
	
	public boolean isNumeric(Object args) throws Exception {
		return this.isNumeric(args.toString());
	}
	
	public boolean isInteger(String str) throws Exception {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			logger.debug(e.getMessage());
			return false;
		}
	}
	
	public boolean isInteger(Object args) throws Exception {
		return this.isInteger(args.toString());
	}
	
	public Integer parseInt(String args) throws Exception {
		Integer intNumber = null;
		try {			
			intNumber = Integer.parseInt(args);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			intNumber = Integer.parseInt(args);
		}
		return intNumber;
	}
	
	public Integer parseInt(Object args) throws Exception {
		return Integer.parseInt(args.toString());
	}
	
	public boolean inArray(String string, String[] array) throws Exception {
		return Arrays.asList(array).contains(string);
	}
	
	public String dateTimeStr () throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	public String dateTimeStr (String format_string) throws Exception {
		SimpleDateFormat format = null;
		try {
			format = new SimpleDateFormat(format_string);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return format.format(new Date());
	}
	
	public String dateTimeStr (Date date_time) throws Exception {
		String datetime_str = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			datetime_str = format.format(date_time);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return datetime_str;
	}
	
	public String dateTimeStr (Date date_time, String format_string) throws Exception {
		String datetime_str = "";
		SimpleDateFormat format = null;
		try {
			format = new SimpleDateFormat(format_string);
			datetime_str = format.format(date_time);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return datetime_str;
	}
	
	public String dateStr () throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}
	
	public String dateStr (String format_string) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			format = new SimpleDateFormat(format_string);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return format.format(new Date());
	}
	
	public String dateStr (Date date) throws Exception {
		String date_str = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date_str = format.format(date);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return date_str;
	}
	
	public String dateStr (Date date, String format_string) throws Exception {
		String date_str = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			format = new SimpleDateFormat(format_string);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		try {
			date_str = format.format(date);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return date_str;
	}
	
	public Date strToDateTime(String str_date_time) throws Exception {
		Date date_time = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date_time = format.parse(str_date_time);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return date_time;
	}
	
	public Date strToDate(String str_date) throws Exception {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = format.parse(str_date);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return date;
	}
	
	public Date strToDate(String str_date, String format_string) throws Exception {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		try {
			format = new SimpleDateFormat(format_string);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		try {
			date = format.parse(str_date);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return date;
	}
	
	public String korDayOfWeek (int day_of_week) throws Exception {
		String kor_day_of_week = "";
		switch (day_of_week) {
		case 1:
			kor_day_of_week = "일";
			break;
		case 2:
			kor_day_of_week = "월";
			break;
		case 3:
			kor_day_of_week = "화";
			break;
		case 4:
			kor_day_of_week = "수";
			break;
		case 5:
			kor_day_of_week = "목";
			break;
		case 6:
			kor_day_of_week = "금";
			break;
		case 7:
			kor_day_of_week = "토";
			break;
		}
		return kor_day_of_week;
	}
	
	public boolean regex (String regex, String input) throws Exception {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}
	
	public boolean regex (String regex, String input, Integer flags) throws Exception {
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}
	
	public String regexGroup (String regex, String input, Integer index) throws Exception {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		
		if (matcher.find()) {
			return matcher.group(index);
		} else {
			return "";
		}
	}
	
	public String regexGroup (String regex, String input, Integer index, Integer flags) throws Exception {
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher matcher = pattern.matcher(input);
		
		if (matcher.find()) {
			return matcher.group(index);
		} else {
			return "";
		}
	}
	
	public List<String> regexGroups (String regex, String input) throws Exception {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		List<String> matches = new ArrayList<>();
		
		if (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				matches.add(matcher.group(i));
			}
		}
		return matches;
	}
	
	public List<String> regexGroups (String regex, String input, Integer flags) throws Exception {
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher matcher = pattern.matcher(input);
		List<String> matches = new ArrayList<>();
		
		if (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				matches.add(matcher.group(i));
			}
		}
		return matches;
	}
	
	public String[] regexSplit (String regex, String input) throws Exception {
		Pattern pattern = Pattern.compile(regex);
		String[] split = pattern.split(input);
		
		return split;
	}
	
	public String[] regexSplit (String regex, String input, Integer flags) throws Exception {
		Pattern pattern = Pattern.compile(regex, flags);
		String[] split = pattern.split(input);
		
		return split;
	}
	
	public String lTrim(String input) throws Exception {
		return input.replaceAll("^\\s+","");
	}
	
	public String lTrim(String input, String target) throws Exception {
		return input.replaceAll("^" + escapeRegex(target) + "+","");
	}
	
	public String rTrim(String input) throws Exception {
		return input.replaceAll("\\s+$","");
	}
	
	public String rTrim(String input, String target) throws Exception {
		return input.replaceAll(escapeRegex(target) + "+$","");
	}
	
	public String trimSlashes(String input) throws Exception {
		if (input.isEmpty()) {
			return "";
		} else {
			input = lTrim(input, "/");
			return rTrim(input, "/");
		}
	}
	
	public String nl2Br(String input) throws Exception {
		return StringEscapeUtils.escapeHtml4(input).replaceAll("\\r\\n|\\n", "<br />");
	}
	
	public String bl2Nbsp(String input) throws Exception {
		return StringEscapeUtils.escapeHtml4(input).replaceAll(" ", "&nbsp;");
	}
	
	public String nlbl2Html(String input) throws Exception {
		return StringEscapeUtils.escapeHtml4(input).replaceAll(" ", "&nbsp;").replaceAll("\\r\\n|\\n", "<br />");
	}
	
	public String convertBase62 (String str) throws Exception {
		Base62 base62 = Base62.createInstance();
		final byte[] encoded = base62.encode(str.getBytes());
		return new String(encoded);
	}
	
	public String oneTimeEncStr(Integer salt_length) throws Exception {
		Random rand = new Random();
		Integer unixtime = (int) System.currentTimeMillis() / 1000;
		String seed = unixtime.toString();
		if (null != salt_length && salt_length > 0) {
			String salt_char = "9";
			String salt_string = "";
			for (int i = 0; i < salt_length; i++) {
				salt_string = salt_string + salt_char;
			}
			if (!"".equals(salt_string)) {
				Integer salt_int = parseInt(salt_string);
				if (null != salt_int && salt_int > 0) {
					seed = String.format("%0" + salt_length + "d", rand.nextInt(salt_int)) + unixtime;
				}
			}
		}
		return DigestUtils.sha256Hex(seed);
	}
	
	public String oneTimeEncStr() throws Exception {
		return oneTimeEncStr(0);
	}
	
	public String oneTimeBase62(Integer salt_length) throws Exception {
		Random rand = new Random();
		long unixtime = System.currentTimeMillis();
		String seed = Long.toString(unixtime);
		if (null != salt_length && salt_length > 0) {
			String salt_char = "9";
			String salt_string = "";
			for (int i = 0; i < salt_length; i++) {
				salt_string = salt_string + salt_char;
			}
			if (!"".equals(salt_string)) {
				Integer salt_int = parseInt(salt_string);
				if (null != salt_int && salt_int > 0) {
					seed = String.format("%0" + salt_length + "d", rand.nextInt(salt_int)) + unixtime;
				}
			}
		}
		return convertBase62(seed);
	}
	
	public String oneTimeBase62() throws Exception {
		return oneTimeBase62(0);
	}
	
	public File resizeImage(String size_type, String orig_path) throws Exception {
		String[] allowed_type = { "middle", "small", "tiny" };
		if (!Arrays.asList(allowed_type).contains(size_type)) throw new IllegalArgumentException("허용된 사진 크기(middle, small, tiny)가 아닙니다. : " + size_type);
		File orig_file = new File(orig_path);
		if (!orig_file.exists()) throw new FileNotFoundException("파일을 찾을 수 없습니다. : " + orig_path);
		Path orig_file_path = orig_file.toPath();
		String mime_type = Files.probeContentType(orig_file_path);
		String type = mime_type.split("/")[0];
		if (!"image".equalsIgnoreCase(type)) throw new IllegalArgumentException("이미지 파일만 변환할 수 있습니다.(" + mime_type + ")");
		
		int resize_width;		// 변환 넓이
		int resize_height;		// 변환 높이
		int limit_width;		// 기준 넓이
		int limit_height;		// 기준 높이
		switch (size_type) {
			case "small":
				resize_width = 360;
				resize_height = 240;
				limit_width = 720;
				limit_height = 480;
				break;
				
			case "tiny":
				resize_width = 120;
				resize_height = 80;
				limit_width = 240;
				limit_height = 160;
				break;
	
			default:
				resize_width = 1080;
				resize_height = 720;
				limit_width = 1440;
				limit_height = 1080;
				break;
		}
		
		BufferedImage orig_image = ImageIO.read(orig_file);
		int width = orig_image.getWidth();
		int height = orig_image.getHeight();
		
		if (width < limit_width && height < limit_height) throw new IllegalArgumentException("변환할 크기보다 작은 이미지입니다. : " + size_type);
		
		File resized_file = getResizedImageFile(size_type, orig_file);
		String orig_ext = FilenameUtils.getExtension(orig_file.getName());
		try {			
			Thumbnails.of(orig_file).size(resize_width, resize_height).outputFormat(orig_ext).toFile(resized_file);
		} catch (Exception e) {
			throw e;
		}
		return resized_file;
	}
	
	public String encodeJson(Object obj) throws Exception {
		String json = "";
		JsonFactory json_factory = new JsonFactory();
		json_factory.setCharacterEscapes(new HTMLCharacterEscapes());
		ObjectMapper mapper = new ObjectMapper(json_factory);
		try {
			json = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return json;
	}
	
	public <T extends Object> T decodeJson(String json, Class<T> type) throws Exception {
		Object object = new Object();
		ObjectMapper mapper = new ObjectMapper();
		try {
			object = mapper.readValue(json, type);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return type.cast(object);
	}
	
	public String renderViewToString(String view_path, HttpServletRequest req, HttpServletResponse res) throws Exception {
		String view = "";
		HttpServletResponseWrapper res_wrapper = new HttpServletResponseWrapper(res) {
			private final StringWriter sw = new StringWriter();
			@Override
			public PrintWriter getWriter() throws IOException {
				return new PrintWriter(sw);
			}
			@Override
			public String toString() {
				return sw.toString();
			}
		};
		String abs_path = req.getServletContext().getRealPath("/WEB-INF/jsp/") + view_path + ".jsp";
		File view_file = new File(abs_path);
		if (view_file.exists()) {
			try {
				req.getRequestDispatcher("/WEB-INF/jsp/" + view_path + ".jsp").include(req, res_wrapper);
				view = res_wrapper.toString();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			FileNotFoundException e = new FileNotFoundException("Can not found the file : " + abs_path);
			logger.error(e.getMessage(), e);
		}
		return view;
	}
	
	public boolean deleteFile(File file) throws Exception {
		boolean result = false;
		Path path = file.toPath();
		String content_type = Files.probeContentType(path);
		String type = "";
		if (null != content_type) {
			String[] type_arr = content_type.split("/");
			type = type_arr.length > 0 ? type_arr[0] : "";
		}
		if (file.exists()) {
			try {
				if (file.delete()) result = true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			boolean is_image = "image".equalsIgnoreCase(type);
			if (is_image) {
				File parent = file.getParentFile();
				if (parent.exists() && parent.isDirectory()) {
					String parent_path = parent.getAbsolutePath() + File.separator;
					String middle_path = parent_path + "middle" + File.separator + file.getName();
					File middle_file = new File(middle_path);
					if (middle_file.exists()) {								
						try {
							middle_file.delete();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
					String small_path = parent_path + "small" + File.separator + file.getName();
					File small_file = new File(small_path);
					if (small_file.exists()) {								
						try {
							small_file.delete();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
					String tiny_path = parent_path + "tiny" + File.separator + file.getName();
					File tiny_file = new File(tiny_path);
					if (tiny_file.exists()) {								
						try {
							tiny_file.delete();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		return result;
	}
	
	public boolean copyFile(String from, String to) throws Exception {
		if (null == from || null == to) {
			throw new IllegalArgumentException("파일 지정이 잘못됐습니다.");
		}
		File from_file = new File(from);
		File to_file = new File(to);
		return copyFile(from_file.getAbsolutePath(), to_file.getAbsolutePath());
	}
	
	public boolean copyFile(File from_file, File to_file) throws Exception {
		if (null == from_file || null == to_file) {
			throw new IllegalArgumentException("파일 지정이 잘못됐습니다.");
		}
		if (!from_file.exists()) {
			throw new FileNotFoundException("파일이 없습니다. - " + from_file.getAbsolutePath());
		}
		if (to_file.exists()) {
			throw new FileExistsException(to_file);
		}
		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(from_file.getAbsolutePath());
			to = new FileOutputStream(to_file.getAbsolutePath());
			int c;
			while ((c = from.read()) != -1) to.write(c);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (null != from) from.close();
			if (null != to) to.close();
		}
		return true;
	}
	
	public boolean moveFile(String from, String to) throws Exception {
		if (null == from || null == to) {
			throw new IllegalArgumentException("파일 지정이 잘못됐습니다.");
		}
		File from_file = new File(from);
		File to_file = new File(to);
		return moveFile(from_file, to_file);
	}
	
	public boolean moveFile(File from_file, File to_file) throws Exception {
		if (null == from_file || null == to_file) {
			throw new IllegalArgumentException("파일 지정이 잘못됐습니다.");
		}
		boolean result = false;
		try {
			result = copyFile(from_file.getAbsolutePath(), to_file.getAbsolutePath());
			result = from_file.delete();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return result;
	}
	
}
