/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.xh.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.cache.WeakRefFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFile;
import com.xh.common.exception.WebException;
import com.xh.util.Utils;

public class WebVFS {

	protected final Log logger = LogFactory.getLog(getClass());

	private static final WebVFS webVFS = new WebVFS();
	private final DefaultFileSystemManager fsm;

	private static Properties defaultProperties;

	static {
		// Create a new empty variable space...
		//
		defaultProperties = new Properties();
	}

	private WebVFS() {
		fsm = new StandardFileSystemManager();
		try {
			fsm.setFilesCache(new WeakRefFilesCache());
			fsm.init();
		} catch (FileSystemException e) {
			e.printStackTrace();
		}

		// Install a shutdown hook to make sure that the file system manager is
		// closed
		// This will clean up temporary files in vfs_cache
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (fsm != null) {
					try {
						fsm.close();
					} catch (Exception ignored) {
						// Exceptions can be thrown due to a closed classloader
					}
				}
			}
		}));
	}

	public FileSystemManager getFileSystemManager() {
		return fsm;
	}

	public static WebVFS getInstance() {
		return webVFS;
	}

	public static FileObject getFileObject(String vfsFilename) throws WebException {
		return getFileObject(vfsFilename, defaultProperties);
	}

	public static FileObject getFileObject(String vfsFilename, Properties space) throws WebException {
		return getFileObject(vfsFilename, space, null);
	}

	public static FileObject getFileObject(String vfsFilename, FileSystemOptions fsOptions) throws WebException {
		return getFileObject(vfsFilename, defaultProperties, fsOptions);
	}

	public static FileObject getFileObject(String vfsFilename, Properties space, FileSystemOptions fsOptions)
			throws WebException {
		try {
			FileSystemManager fsManager = getInstance().getFileSystemManager();

			// We have one problem with VFS: if the file is in a subdirectory of
			// the current one: somedir/somefile
			// In that case, VFS doesn't parse the file correctly.
			// We need to put file: in front of it to make it work.
			// However, how are we going to verify this?
			//
			// We are going to see if the filename starts with one of the known
			// protocols like file: zip: ram: smb: jar: etc.
			// If not, we are going to assume it's a file.
			//
			boolean relativeFilename = true;
			String[] schemes = fsManager.getSchemes();
			for (int i = 0; i < schemes.length && relativeFilename; i++) {
				if (vfsFilename.startsWith(schemes[i] + ":")) {
					relativeFilename = false;
					// We have a VFS URL, load any options for the file system
					// driver
					fsOptions = buildFsOptions(space, fsOptions, vfsFilename, schemes[i]);
				}
			}

			String filename;
			if (vfsFilename.startsWith("\\\\")) {
				File file = new File(vfsFilename);
				filename = file.toURI().toString();
			} else {
				if (relativeFilename) {
					File file = new File(vfsFilename);
					filename = file.getAbsolutePath();
				} else {
					filename = vfsFilename;
				}
			}

			FileObject fileObject = null;

			if (fsOptions != null) {
				fileObject = fsManager.resolveFile(filename, fsOptions);
			} else {
				fileObject = fsManager.resolveFile(filename);
			}

			return fileObject;
		} catch (IOException e) {
			throw new WebException("Unable to get VFS File object for filename '" + cleanseFilename(vfsFilename)
					+ "' : " + e.getMessage());
		}
	}

	/**
	 * Private method for stripping password from filename when a FileObject can
	 * not be obtained. getFriendlyURI(FileObject) or getFriendlyURI(String) are
	 * the public methods.
	 */
	private static String cleanseFilename(String vfsFilename) {
		return vfsFilename.replaceAll(":[^:@/]+@", ":<password>@");
	}

	private static FileSystemOptions buildFsOptions(Properties varSpace, FileSystemOptions sourceOptions,
			String vfsFilename, String scheme) throws IOException {
		if (varSpace == null || vfsFilename == null) {
			// We cannot extract settings from a non-existant variable space
			return null;
		}

		IWebFileSystemConfigBuilder configBuilder = WebFileSystemConfigBuilderFactory.getConfigBuilder(varSpace,
				scheme);

		FileSystemOptions fsOptions = (sourceOptions == null) ? new FileSystemOptions() : sourceOptions;

		Set<Object> varList = varSpace.keySet();

		for (Object varObj : varList) {
			String var = varObj.toString();
			if (var.startsWith("vfs.")) {
				String param = configBuilder.parseParameterName(var, scheme);
				if (param != null) {
					configBuilder.setParameter(fsOptions, param, varSpace.getProperty(var), var, vfsFilename);
				} else {
					throw new IOException("FileSystemConfigBuilder could not parse parameter: " + var);
				}
			}
		}
		return fsOptions;
	}

	/**
	 * Read a text file (like an XML document). WARNING DO NOT USE FOR DATA
	 * FILES.
	 *
	 * @param vfsFilename
	 *            the filename or URL to read from
	 * @param charSetName
	 *            the character set of the string (UTF-8, ISO8859-1, etc)
	 * @return The content of the file as a String
	 * @throws IOException
	 */
	public static String getTextFileContent(String vfsFilename, String charSetName) throws WebException {
		return getTextFileContent(vfsFilename, null, charSetName);
	}

	public static String getTextFileContent(String vfsFilename, Properties space, String charSetName)
			throws WebException {
		try {
			InputStream inputStream = null;

			if (space == null) {
				inputStream = getInputStream(vfsFilename);
			} else {
				inputStream = getInputStream(vfsFilename, space);
			}
			InputStreamReader reader = new InputStreamReader(inputStream, charSetName);
			int c;
			StringBuilder aBuffer = new StringBuilder();
			while ((c = reader.read()) != -1) {
				aBuffer.append((char) c);
			}
			reader.close();
			inputStream.close();

			return aBuffer.toString();
		} catch (IOException e) {
			throw new WebException(e);
		}
	}

	public static boolean fileExists(String vfsFilename) throws WebException {
		return fileExists(vfsFilename, null);
	}

	public static boolean fileExists(String vfsFilename, Properties space) throws WebException {
		FileObject fileObject = null;
		try {
			fileObject = getFileObject(vfsFilename, space);
			return fileObject.exists();
		} catch (IOException e) {
			throw new WebException(e);
		} finally {
			if (fileObject != null) {
				try {
					fileObject.close();
				} catch (Exception e) { /* Ignore */
				}
			}
		}
	}

	public static InputStream getInputStream(FileObject fileObject) throws FileSystemException {
		FileContent content = fileObject.getContent();
		return content.getInputStream();
	}

	public static InputStream getInputStream(String vfsFilename) throws WebException {
		return getInputStream(vfsFilename, defaultProperties);
	}

	public static InputStream getInputStream(String vfsFilename, Properties space) throws WebException {
		try {
			FileObject fileObject = getFileObject(vfsFilename, space);

			return getInputStream(fileObject);
		} catch (IOException e) {
			throw new WebException(e);
		}
	}

	public static OutputStream getOutputStream(FileObject fileObject, boolean append) throws IOException {
		FileObject parent = fileObject.getParent();
		if (parent != null) {
			if (!parent.exists()) {
				throw new IOException("Parent Directory " + getFriendlyURI(parent) + " Does Not Exist ");
			}
		}
		try {
			fileObject.createFile();
			FileContent content = fileObject.getContent();
			return content.getOutputStream(append);
		} catch (FileSystemException e) {
			// Perhaps if it's a local file, we can retry using the standard
			// File object. This is because on Windows there is a bug in VFS.
			//
			if (fileObject instanceof LocalFile) {
				try {
					String filename = getFilename(fileObject);
					return new FileOutputStream(new File(filename), append);
				} catch (Exception e2) {
					throw e; // throw the original exception: hide the retry.
				}
			} else {
				throw e;
			}
		}
	}

	public static OutputStream getOutputStream(String vfsFilename, boolean append) throws WebException {
		return getOutputStream(vfsFilename, defaultProperties, append);
	}

	public static OutputStream getOutputStream(String vfsFilename, Properties space, boolean append)
			throws WebException {
		try {
			FileObject fileObject = getFileObject(vfsFilename, space);
			return getOutputStream(fileObject, append);
		} catch (IOException e) {
			throw new WebException(e);
		}
	}

	public static OutputStream getOutputStream(String vfsFilename, Properties space, FileSystemOptions fsOptions,
			boolean append) throws WebException {
		try {
			FileObject fileObject = getFileObject(vfsFilename, space, fsOptions);
			return getOutputStream(fileObject, append);
		} catch (IOException e) {
			throw new WebException(e);
		}
	}

	public static String getFilename(FileObject fileObject) {
		FileName fileName = fileObject.getName();
		String root = fileName.getRootURI();
		if (!root.startsWith("file:")) {
			return fileName.getURI(); // nothing we can do about non-normal
										// files.
		}
		if (root.startsWith("file:////")) {
			return fileName.getURI(); // we'll see 4 forward slashes for a
										// windows/smb network share
		}
		if (root.endsWith(":/")) { // Windows
			root = root.substring(8, 10);
		} else { // *nix & OSX
			root = "";
		}
		String fileString = root + fileName.getPath();
		if (!"/".equals(File.separator)) {
			fileString = Utils.replace(fileString, "/", Utils.FILE_SEPARATOR);
		}
		return fileString;
	}

	public static String getFriendlyURI(String filename) {
		if (filename == null) {
			return null;
		}
		String friendlyName;
		try {
			friendlyName = getFriendlyURI(WebVFS.getFileObject(filename));
		} catch (Exception e) {
			// unable to get a friendly name from VFS object.
			// Cleanse name of pwd before returning
			friendlyName = cleanseFilename(filename);
		}
		return friendlyName;
	}

	public static String getFriendlyURI(FileObject fileObject) {
		return fileObject.getName().getFriendlyURI();
	}

	public static FileObject createTempFile(String prefix, String suffix, String directory) throws WebException {
		return createTempFile(prefix, suffix, directory, null);
	}

	public static FileObject createTempFile(String prefix, String suffix, String directory, Properties space)
			throws WebException {
		try {
			FileObject fileObject;
			do {
				// Build temporary file name using UUID to ensure uniqueness.
				// Old mechanism would fail using Sort Rows (for
				// example)
				// when there multiple nodes with multiple JVMs on each node. In
				// this case, the temp file names would end up
				// being
				// duplicated which would cause the sort to fail.
				String filename = new StringBuilder(50).append(directory).append('/').append(prefix).append('_')
						.append(Utils.getUUIDAsString()).append(suffix).toString();
				fileObject = getFileObject(filename, space);
			} while (fileObject.exists());
			return fileObject;
		} catch (IOException e) {
			throw new WebException(e);
		}
	}

	public static Comparator<FileObject> getComparator() {
		return new Comparator<FileObject>() {
			@Override
			public int compare(FileObject o1, FileObject o2) {
				String filename1 = getFilename(o1);
				String filename2 = getFilename(o2);
				return filename1.compareTo(filename2);
			}
		};
	}

	/**
	 * Get a FileInputStream for a local file. Local files can be read with NIO.
	 *
	 * @param fileObject
	 * @return a FileInputStream
	 * @throws IOException
	 * @deprecated because of API change in Apache VFS. As a workaround use
	 *             FileObject.getName().getPathDecoded(); Then use a regular
	 *             File() object to create a File Input stream.
	 */
	@Deprecated
	public static FileInputStream getFileInputStream(FileObject fileObject) throws IOException {

		if (!(fileObject instanceof LocalFile)) {
			// We can only use NIO on local files at the moment, so that's what
			// we limit ourselves to.
			//
			throw new IOException("Fixed Input.Only Local Files Are Supported!");
		}

		return new FileInputStream(fileObject.getName().getPathDecoded());
	}

	/**
	 * Check if filename starts with one of the known protocols like file: zip:
	 * ram: smb: jar: etc. If yes, return true otherwise return false
	 * 
	 * @param vfsFileName
	 * @return boolean
	 */
	public static boolean startsWithScheme(String vfsFileName) {
		FileSystemManager fsManager = getInstance().getFileSystemManager();

		boolean found = false;
		String[] schemes = fsManager.getSchemes();
		for (int i = 0; i < schemes.length; i++) {
			if (vfsFileName.startsWith(schemes[i] + ":")) {
				found = true;
				break;
			}
		}

		return found;
	}

}
