package org.srcm.heartfulness.filter;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Gopinath 
 *
 */
@Component
public class ExtensionFilter {
	
	private final List<String> allowedImageExtensions = Arrays.asList("jpg", "jfif", "jpeg", "exif", "bmp"
			, "tiff", "tif", "png", "jp2" ,"ppm", "pgm", "pbm", "pnm", "webp", "heif", "bpg", "img", "svg");

	private final List<String> allowedVideoExtensions = Arrays.asList("mpeg", "mp4", "flv", "avi", "mkv"
			, "aaf", "3gp", "gif", "asf", "avchd", "bik", "cam", "collab", "dat", "dsh", "dvr-ms", "qt"
			, "flv", "mpg", "fla", "flr", "sol", "m4v", "mkv", "wrap", "mng", "mov", "mpg"
			, "mpe", "thp", "mp4", "mxf", "roq", "nsv", "ogg", "rm", "svi", "smi", "smk", "swf", "vob"
			, "wmv", "wtv", "yuv", "webm", "m4a", "asf", "3g2", "f4v", "f4p", "f4a", "f4b"); 

	private final List<String> allowedAudioExtensions = Arrays.asList("wav", "raw", "bwf", "flac", "wma", "amr", "mp1"
			, "mp2", "mp3", "spx", "gsm", "wma", "aac", "mpc", "vqf", "ots", "swa", "vox", "voc", "dwd", "smp", "ogg");
			
	private final List<String> allowedFileExtensions = Arrays.asList("xls", "xlm", "xlsx", "xlsm", "doc", "docx", "docm", "odt", "pdf", "ppt"
			, "pptx", "odp", "txt", "rtf");
	
	public List<String> getAllowedImageExtensions() {
		return allowedImageExtensions;
	}

	public List<String> getAllowedVideoExtensions() {
		return allowedVideoExtensions;
	}

	public List<String> getAllowedFileExtensions() {
		return allowedFileExtensions;
	}
	
	public List<String> getAllowedAudioExtensions() {
		return allowedAudioExtensions;
	}

	public boolean checkFileExtension(String extension){
		if(this.allowedFileExtensions.contains(extension) 
				|| this.allowedVideoExtensions.contains(extension) 
				|| this.allowedImageExtensions.contains(extension)
				|| this.allowedAudioExtensions.contains(extension)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean validateSessionFileExtension(String extension){
		if(this.allowedVideoExtensions.contains(extension) 
				|| this.allowedImageExtensions.contains(extension)
				|| this.allowedAudioExtensions.contains(extension)){
			return true;
		}else{
			return false;
		}
	}
	
	public String getSessionFileType(String fileName){
		String extention = FilenameUtils.getExtension(fileName);
		extention = extention.toLowerCase();
		if(getAllowedImageExtensions().contains(extention)){
			return "IMAGE";
		}else if(getAllowedVideoExtensions().contains(extention)){
			return "VIDEO";
		}else{
			return null;
		}
	}
	
	public String getFileType(String fileName){
		String extention = FilenameUtils.getExtension(fileName);
		extention = extention.toLowerCase();
		if(getAllowedImageExtensions().contains(extention)){
			return "IMAGE";
		}else if(getAllowedVideoExtensions().contains(extention)){
			return "VIDEO";
		}else if(getAllowedFileExtensions().contains(extention)){
			return "DOCUMENT";
		}else if(getAllowedAudioExtensions().contains(extention)){
			return "AUDIO";
		}else{
			return "OTHERS";
		}
	}
	
}
