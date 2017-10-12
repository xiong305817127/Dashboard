package com.xh.service.menu;

import java.util.HashMap;
import org.apache.commons.vfs2.FileObject;

import com.xh.common.exception.WebException;
import com.xh.entry.Menu;
import com.xh.util.ConfigPropertyUtil;
import com.xh.util.Utils;
import com.xh.util.vfs.WebVFS;

public class MenuCreateFile {

	private static final String targetRoot = "/src/main/webapp/frontend-src/src/";
	private static final String templateRoot = "/template/";
	private static final String routeReplaceNote = "//dynamic add Route,Do not delete";
	private static final String apiReplaceNote = "//dynamic add Api,Do not delete";

	public static void createFile(Menu menu) throws Exception {

		if (menu == null ) {
			throw new WebException(" menu is null! ");
		}
		
		String projectRoot = ConfigPropertyUtil.getProperty("project.source.path.root");
		if ( Utils.isEmpty(projectRoot)) {
			throw new WebException("The project root path (Dashboard path) was not found. Configure the variable 'project.source.path.root' ! ");
		}

		new MenuCreateFile(projectRoot, menu.getRoute(), menu.getName()).start();
	}

	private String projectRoot;
	private String menuRoute;
	private String menuName;

	private MenuCreateFile(String projectRoot, String menuRoute, String menuName) {
		this.projectRoot = projectRoot;
		this.menuRoute = menuRoute;
		this.menuName = menuName;
	}

	public void start() throws Exception {
		// 1.创建 routes
		createRouteFile();
		// 2.创建 models
		createModelsFile();
		// 3.创建 services
		createServicesFile();
		// 4.替换 utils/config.js 的api
		replaceConfigApi();
		// 5.替换 router.js
		replaceRouter();
	}

	private void createRouteFile() throws Exception {
		String templateFilePath = projectRoot + templateRoot + "routes/";
		String targetFilePath = projectRoot + targetRoot + "routes/" + menuName + "/";
		WebVFS.createFolder(targetFilePath);
		templateReplace(templateFilePath, targetFilePath);

	}

	private void createModelsFile() throws Exception {
		String templateFilePath = projectRoot + templateRoot + "models.js";
		String targetFilePath = projectRoot + targetRoot + "models/" + menuName + ".js";
		templateReplace(templateFilePath, targetFilePath);

	}

	private void createServicesFile() throws Exception {
		String templateFilePath = projectRoot + templateRoot + "services.js";
		String targetFilePath = projectRoot + targetRoot + "services/" + menuName + ".js";
		templateReplace(templateFilePath, targetFilePath);

	}

	private void templateReplace(String templateFile, String targetFile) throws Exception {

		FileObject templateFileObj = WebVFS.getFileObject(templateFile);
		if (templateFileObj.isFile()) {
			if ( !WebVFS.fileExists(targetFile) ) {
				templateFileObj.close();
				String fileContent = WebVFS.getTextFileContent(templateFile, "utf-8");
				if (!Utils.isEmpty(fileContent)) {
					HashMap<String, String> map = Utils.newHashMap();
					map.put("MenuRoute", menuRoute);
					map.put("MenuName", menuName);
					String newContent = Utils.replMap(fileContent, map);
					WebVFS.writerTextFileContent(targetFile, newContent, "utf-8");
				}
			}

		} else {
			for (FileObject child : templateFileObj.getChildren()) {
				String fileName = child.getName().getBaseName();
				templateReplace(templateFile + fileName, targetFile + fileName);
				child.close();
			}
		}

	}

	private synchronized void replaceConfigApi() throws Exception {

		String targetFilePath = projectRoot + targetRoot + "utils/config.js";
		String fileContent = WebVFS.getTextFileContent(targetFilePath, "utf-8");
		if (!fileContent.contains(menuName)) {
			String templateText = "    " + menuName + ": `${APIV1}/" + menuName + "`,\n    " + apiReplaceNote;
			String newContent = Utils.replace(fileContent, apiReplaceNote, templateText);
			WebVFS.writerTextFileContent(targetFilePath, newContent, "utf-8");
		}
	}

	private synchronized void replaceRouter() throws Exception {

		String targetFilePath = projectRoot + targetRoot + "router.js";
		String fileContent = WebVFS.getTextFileContent(targetFilePath, "utf-8");
		if (!fileContent.contains(menuRoute)) {
			String templateText = "{\n" + "     path: '" + menuRoute + "',\n" + "     models: () => [import('./models/"
					+ menuName + "')],\n" + "     component: () => import('./routes/" + menuName + "/'),\n"
					+ " },\n    " + routeReplaceNote;
			String newContent = Utils.replace(fileContent, routeReplaceNote, templateText);
			WebVFS.writerTextFileContent(targetFilePath, newContent, "utf-8");
		}

	}

}
