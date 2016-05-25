package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.mail.SendMail;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

@Component
@ConfigurationProperties(locations = "classpath:dev.ftpserver.properties", ignoreUnknownFields = false, prefix = "ftp.sahajmarginfo")
public class FTPConnectionHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(FTPConnectionHelper.class);

	@Autowired
	private SendMail sendMail;

	private String username;
	private String host;
	private int port;
	private String password;
	private String backUpFolderName;

	public String getBackUpFolderName() {
		return backUpFolderName;
	}

	public void setBackUpFolderName(String backUpFolderName) {
		this.backUpFolderName = backUpFolderName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static class NotificationMail {
		private String recipientsTo;
		private String recipientsCc;
		private String subject;

		public String getRecipientsTo() {
			return recipientsTo;
		}

		public void setRecipientsTo(String recipientsTo) {
			this.recipientsTo = recipientsTo;
		}

		public String getRecipientsCc() {
			return recipientsCc;
		}

		public void setRecipientsCc(String recipientsCc) {
			this.recipientsCc = recipientsCc;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}
	}

	@NotNull
	private NotificationMail notificationmail;

	public NotificationMail getNotificationmail() {
		return notificationmail;
	}

	public void setNotificationmail(NotificationMail notificationmail) {
		this.notificationmail = notificationmail;
	}

	/**
	 * To upload a file in specified FTP location
	 * 
	 * @param welcomeMailidsLocalFilepath
	 *            -local file path
	 * @param welcomeMailidsRemoteFilepath
	 *            -remote file path
	 * @param welcomeMailidsFileName
	 *            -name of the file be upload
	 * @throws SftpException
	 * @throws IOException
	 */
	public void processUpload(String welcomeMailidsLocalFilepath, String welcomeMailidsRemoteFilepath,
			String welcomeMailidsFileName) throws SftpException, IOException {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd_MMM");
		String currentDate = dateTime.format(format);

		JSch jsch = new JSch();
		Session session = null;
		try {
			LOGGER.debug("Connecting to server...");
			session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			LOGGER.debug("Connected succesfully..!");

			if (isFileExists(sftpChannel, welcomeMailidsRemoteFilepath + welcomeMailidsFileName)) {
				sftpChannel.mkdir(backUpFolderName);
				sftpChannel.rename(welcomeMailidsRemoteFilepath + welcomeMailidsFileName, welcomeMailidsRemoteFilepath
						+ backUpFolderName + "/" + welcomeMailidsFileName);
				sftpChannel.put(welcomeMailidsLocalFilepath + currentDate + "_" + welcomeMailidsFileName,
						welcomeMailidsRemoteFilepath + welcomeMailidsFileName);
				LOGGER.debug("Old file copied to Archives folder and new file created succesfully..!");
			} else {
				sftpChannel.put(welcomeMailidsLocalFilepath + currentDate + "_" + welcomeMailidsFileName,
						welcomeMailidsRemoteFilepath + welcomeMailidsFileName);
				LOGGER.debug("File uploaded succesfully..!");
			}

			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException ex) {
			LOGGER.debug("Error while copying file to FTP - " + ex.getMessage());
		}
	}

	/**
	 * To check whether the file exists in the specified path
	 * 
	 * @param sftpChannel
	 *            - <code>ChannelSftp</code>
	 * @param path
	 *            -path to check for the file
	 * @return true if the file exists else false
	 */
	public boolean isFileExists(ChannelSftp sftpChannel, String path) {
		SftpATTRS attrs = null;
		try {
			attrs = sftpChannel.lstat(path);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * To send notification mail to the team if no new participants found to
	 * send mail for the day
	 */
	public void sendNotificationForNoEmails() {
		sendMail.sendNotificationForNoEmails(notificationmail.getRecipientsTo(), notificationmail.getRecipientsCc(),
				notificationmail.getSubject());
	}
}
