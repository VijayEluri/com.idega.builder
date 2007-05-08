package com.idega.builder.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLReader;
import com.idega.builder.presentation.AddModuleBlock;
import com.idega.builder.presentation.EditModuleBlock;
import com.idega.builder.presentation.IBObjectControl;
import com.idega.builder.presentation.SetModulePropertyBlock;
import com.idega.business.IBOLookup;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.slide.business.IWSlideSession;

public class BuilderEngineBean extends IBOServiceBean implements BuilderEngine {
	
	private static final long serialVersionUID = -4806588458269035118L;
	private static final Log log = LogFactory.getLog(BuilderEngineBean.class);
	
	private BuilderLogic builder = BuilderLogic.getInstance();
	
	private IWContext getIWContext() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc == null) {
			return IWContext.getInstance();
		}
		else {
			return IWContext.getIWContext(fc);
		}
	}
	
	public List<String> getBuilderInitInfo() {
		List<String> info = new ArrayList<String>();
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return info;
		}
		
		IWResourceBundle iwrb = builder.getBuilderBundle().getResourceBundle(iwc);
		
		info.add(builder.getUriToObject(AddModuleBlock.class));																// 0
		info.add(iwrb.getLocalizedString("ib_addmodule_window", "Add a new Module"));										// 1
		info.add(iwrb.getLocalizedString("set_module_properties", "Set module properties"));								// 2
		info.add(new StringBuffer(builder.getBuilderBundle().getResourcesPath()).append("/add.png").toString());			// 3
		info.add(new StringBuffer(builder.getBuilderBundle().getResourcesPath()).append("/information.png").toString());	// 4
		info.add(iwrb.getLocalizedString("no_ids_inserting_module", "Error occurred while inserting selected module!"));	// 5
		info.add(String.valueOf(iwc.getCurrentIBPageID()));																	// 6
		info.add(iwrb.getLocalizedString("adding", "Adding..."));															// 7
		info.add(iwrb.getLocalizedString("create_simple_template.Region", "Region"));										// 8
		info.add(builder.getUriToObject(EditModuleBlock.class));															// 9
		info.add(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);															// 10
		info.add(BuilderConstants.MODULE_NAME);																				// 11
		info.add(iwrb.getLocalizedString("deleting", "Deleting..."));														// 12
		info.add(iwrb.getLocalizedString("are_you_sure", "Are You sure?"));													// 13
		info.add(iwrb.getLocalizedString("saving", "Saving..."));															// 14
		info.add(iwrb.getLocalizedString("loading", "Loading..."));															// 15
		info.add(BuilderConstants.IB_PAGE_PARAMETER);																		// 16
		info.add(BuilderConstants.HANLDER_VALUE_OBJECTS_STYLE_CLASS);														// 17
		info.add(iwrb.getLocalizedString("reloading", "Reloading..."));														// 18
		
		return info;
	}
	
	public Document addSelectedModule(String pageKey, String instanceId, int objectId, String containerId, String className, int index) {
		if (pageKey == null || instanceId == null || objectId < 0 || className == null) {
			return null;
		}
		
		// Getting needed parameters
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return null;
		}
		
		// Adding region (if region doesn't exist)
		builder.addRegion(pageKey, containerId, instanceId, false);
		
		
		// Adding module
		String uuid = null;
		IWSlideSession session = getSession(iwc);
		synchronized (BuilderEngineBean.class) {
			uuid = builder.addNewModule(pageKey, instanceId, objectId, containerId, session);
		}
		if (uuid == null) {
			return null;
		}
		uuid = new StringBuffer(IBXMLReader.UUID_PREFIX).append(uuid).toString();

		// Getting instance of selected component
		Class objectClass = null;
		try {
			objectClass = RefactorClassRegistry.forName(className);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return null;
		}
		PresentationObject obj = null;
		try {
			obj = (PresentationObject) objectClass.newInstance();
			obj.setId(uuid);
		} catch (Exception e){
			log.error(e);
			return null;
		}
		// Getting needed parameters
		Page currentPage = builder.getPage(pageKey, iwc);
		if (currentPage == null) {
			return null;
		}
		IBObjectControl objectComponent =  new IBObjectControl(obj, currentPage, containerId, iwc, index);
		if (objectComponent == null) {
			return null;
		}
		
		Document renderedObject = builder.getRenderedPresentationObject(iwc, objectComponent, false);
		// Returning result
		if (renderedObject != null && session != null) {
			builder.clearAllCachedPages();	// Because IBXMLPage is saved using other thread, need to delete cache (also need to improve)
		}
		return renderedObject;
	}
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || parentId == null || instanceId == null) {
			return false;
		}
		boolean result = false;
		IWSlideSession session = getSession(getIWContext());
		synchronized (BuilderEngineBean.class) {
			result = builder.deleteModule(pageKey, parentId, instanceId, session);
		}
		if (result && session != null) {
			builder.clearAllCachedPages();
		}
		return result;
	}
	
	private IWSlideSession getSession(IWContext iwc) {
		if (iwc == null) {
			iwc = getIWContext();
			if (iwc == null) {
				return null;
			}
		}
		IWSlideSession session = null;
		try {
			session = (IWSlideSession) IBOLookup.getSessionInstance(iwc, IWSlideSession.class);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
		return session;
	}
	
	public Document getPropertyBox(String pageKey, String propertyName, String objectInstanceId) {
		if (propertyName == null || objectInstanceId == null) {
			return null;
		}
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return null;
		}
		if (pageKey == null) {
			pageKey = String.valueOf(iwc.getCurrentIBPageID());
		}
		
		iwc.setApplicationAttribute(BuilderConstants.IB_PAGE_PARAMETER, pageKey);
		iwc.setApplicationAttribute(BuilderConstants.METHOD_ID_PARAMETER, propertyName);
		iwc.setApplicationAttribute(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, objectInstanceId);
		
		PresentationObject propertyBox = new SetModulePropertyBlock();
		Document renderedBox = builder.getRenderedPresentationObject(iwc, propertyBox, false);
		
		iwc.removeApplicationAttribute(BuilderConstants.IB_PAGE_PARAMETER);
		iwc.removeApplicationAttribute(BuilderConstants.METHOD_ID_PARAMETER);
		iwc.removeApplicationAttribute(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);
		
		return renderedBox;
	}
	
	private void clearBuilderBlocks() {
		IWContext iwc = getIWContext();
		builder.removeBlockObjectFromCache(iwc, BuilderConstants.SET_MODULE_PROPERTY_CACHE_KEY);
		builder.removeBlockObjectFromCache(iwc, BuilderConstants.EDIT_MODULE_WINDOW_CACHE_KEY);
	}
	
	private boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] properties) {
		if (pageKey == null || moduleId == null || propertyName == null || properties == null) {
			return false;
		}
		
		IWMainApplication application = null;
		IWContext iwc = getIWContext();
		if (iwc == null) {
			application = IWMainApplication.getDefaultIWMainApplication();
		}
		else {
			application = iwc.getIWMainApplication();
		}
		if (application == null) {
			return false;
		}
		
		return builder.setProperty(pageKey, moduleId, propertyName, properties, application);
	}
	
	public boolean setSimpleModuleProperty(String pageKey, String moduleId, String propertyName, String propertyValue) {
		boolean result = setModuleProperty(pageKey, moduleId, propertyName, new String[] {propertyValue});
		
		if (result) {
			clearBuilderBlocks();
		}
		return result;
	}
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, List<AdvancedProperty> properties) {
		if (properties == null) {
			return false;
		}
		
		String[] parsedProperties = new String[properties.size()];
		for (int i = 0; i < properties.size(); i++) {
			parsedProperties[i] = properties.get(i).getValue();
		}
		
		boolean result = setModuleProperty(pageKey, moduleId, propertyName, parsedProperties);
		if (result) {
			clearBuilderBlocks();
		}
		return result;
	}
	
	public Document reRenderObject(String pageKey, String regionId, String instanceId) {
		if (pageKey == null || instanceId == null) {
			return null;
		}
		
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return null;
		}
		
		Page page = builder.getPage(pageKey, iwc);
		if (page == null) {
			return null;
		}
		List pageChildren = page.getChildren();
		if (pageChildren == null) {
			return null;
		}
		boolean foundRegion = false;
		PresentationObjectContainer container = null;
		Object o = null;
		for (int i = 0; (i < pageChildren.size() && !foundRegion); i++) {
			o = pageChildren.get(i);
			if (o instanceof PresentationObjectContainer) {
				container = (PresentationObjectContainer) o;
				if (regionId.equals(container.getLabel())) {
					foundRegion = true;
				}
			}
		}
		if (!foundRegion) {
			return null;
		}
		
		List regionChildren = container.getChildren();
		if (regionChildren == null) {
			return null;
		}
		boolean foundComponent = false;
		PresentationObject object = null;
		for (int i = 0; (i < regionChildren.size() && !foundComponent); i++) {
			o = regionChildren.get(i);
			if (o instanceof PresentationObject) {
				object = (PresentationObject) o;
				if (instanceId.equals(object.getId())) {
					foundComponent = true;
				}
			}
		}
		if (!foundComponent) {
			return null;
		}
		
		Document reRenderedObject = builder.getRenderedPresentationObject(iwc, object, false);
		return reRenderedObject;
	}
	
	public boolean updateHandler(String[] values) {
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return false;
		}
		
		HttpSession session = iwc.getSession();
		if (session == null) {
			return false;
		}
		
		Object o = session.getAttribute(BuilderConstants.HANDLER_PARAMETER);
		if (!(o instanceof ICPropertyHandler)) {
			return false;
		}
		ICPropertyHandler handler = (ICPropertyHandler) o;
		if (handler == null) {
			return false;
		}
		
		handler.onUpdate(values, iwc);
		
		return true;
	}
	
	public Document getRenderedPresentationObject(String className, boolean cleanHtml) {
		if (className == null) {
			return null;
		}
		Class objectClass = null;
		try {
			objectClass = RefactorClassRegistry.forName(className);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return null;
		}
		Object o = null;
		try {
			o = objectClass.newInstance();
		} catch (InstantiationException e) {
			log.error(e);
			return null;
		} catch (IllegalAccessException e) {
			log.error(e);
			return null;
		}
		if (o instanceof PresentationObject) {
			IWContext iwc = getIWContext();
			return builder.getRenderedPresentationObject(iwc, (PresentationObject) o, cleanHtml);
		}
		return null;
	}

}
