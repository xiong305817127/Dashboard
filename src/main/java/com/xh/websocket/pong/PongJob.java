package com.xh.websocket.pong;

import java.io.IOException;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.xh.websocket.WebSocketManager;

public class PongJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobDataMap map = arg0.getMergedJobDataMap();
		String user = (String) map.get("user");
		try {
			WebSocketManager.sendMessage(user ,"ping", true,  "pong");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

