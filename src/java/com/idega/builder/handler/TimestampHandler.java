/*
 * Created on Mar 28, 2004
 *
 */
package com.idega.builder.handler;

import java.sql.Timestamp;
import java.util.List;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.TimeInput;
import com.idega.presentation.ui.TimestampInput;

/**
 * TimeHandler
 * @author aron 
 * @version 1.0
 */
public class TimestampHandler implements PropertyHandler {
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		TimestampInput timeInput = new TimestampInput(name);
		if(!"".equals(stringValue)){
			timeInput.setTimestamp(new com.idega.util.IWTimestamp(stringValue).getTimestamp());
		}
		return timeInput;
	}
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
	
	}
}
