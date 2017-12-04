package c.g.z.Utils;


import android.text.TextUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class C {

	public static void Unzip(final File zipFile, String dest, String passwd,
                             String charset)
			throws ZipException {
		ZipFile zFile = new ZipFile(zipFile);
		if (TextUtils.isEmpty(charset)) {
			charset = "UTF-8";
		}
		zFile.setFileNameCharset(charset);
		if (!zFile.isValidZipFile()) {
			throw new ZipException(
					"Compressed files are not illegal, may be damaged.");
		}
		File destDir = new File(dest); // Unzip directory
		if (destDir.isDirectory() && !destDir.exists()) {
			destDir.mkdir();
		}
		if (zFile.isEncrypted()) {
			zFile.setPassword(passwd.toCharArray());
		}
		zFile.extractAll(dest);

	}

}
