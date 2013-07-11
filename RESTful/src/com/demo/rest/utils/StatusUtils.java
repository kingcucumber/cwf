package com.demo.rest.utils;

import org.apache.commons.logging.Log;


import com.sun.org.apache.commons.logging.LogFactory;

public class StatusUtils {
	public static final int WORKFLOW_PENDING_STATUS = 0;
	public static final int WORKFLOW_PROCESS_STATUS = 1;
	public static final int WORKFLOW_FINISHED_STATUS = 2;
	public static final int WORKFLOW_FAILED_STATUS = 3;

	public static final int ACTOR_CHECKED_STATUS = 10;
	public static final int ACTOR_PROCESS_STATUS = 20;
	public static final int ACTOR_FINISHED_STATUS = 30;
	public static final int ACTOR_FAILED_STATUS = 40;

	Log logger = (Log) LogFactory.getLog(StatusUtils.class);

}
