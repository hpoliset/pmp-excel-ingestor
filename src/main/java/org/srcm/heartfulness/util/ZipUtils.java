package org.srcm.heartfulness.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.srcm.heartfulness.model.ParticipantFullDetails;

public class ZipUtils {
	
	/**
	 * To generate the report file in zip and get the byte array
	 * 
	 * @param participants - collection of <code>ParticipantFullDetails</code>
	 * @param sb - Headers to be generated in  the report
	 * @return content to be written in the report as byte[].
	 * @throws IOException
	 */
	 public static byte[] getByteArray( Collection<ParticipantFullDetails> participants,StringBuilder sb) throws IOException{
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 ZipOutputStream zos = new ZipOutputStream(baos);
			
		zos.putNextEntry(new ZipEntry("Report_"+new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) +".txt"));
		
		zos.write(sb.toString().getBytes());
		for (ParticipantFullDetails participant: participants ){
			zos.write(participant.toString().getBytes());
			zos.write("\n".getBytes());
		}
		zos.closeEntry();
		zos.close();
		baos.close();
		return baos.toByteArray();
	 }
}
