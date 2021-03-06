package com.idega.builder.business;


import com.idega.core.builder.data.ICDomain;
import com.idega.presentation.IWContext;

import java.util.List;
import java.util.Map;
import com.idega.core.builder.business.BuilderPageWriterService;
import com.idega.core.builder.business.BuilderService;
import com.idega.idegaweb.IWMainApplication;
import java.rmi.RemoteException;
import javax.faces.component.UIComponent;

import com.idega.io.serialization.ObjectWriter;
import java.util.Collection;
import com.idega.business.IBOService;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.Page;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWUserContext;

public interface IBMainService extends IBOService, BuilderService, BuilderPageWriterService {
	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPage
	 */
	public Page getPage(String pageID) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentDomain
	 */
	public ICDomain getCurrentDomain() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	public String getPageURI(int pageKey) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	public String getPageURI(String pageId) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	public String getPageURI(ICPage page) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageURI
	 */
	public String getCurrentPageURI(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPageId
	 */
	public int getRootPageId() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPageKey
	 */
	public String getRootPageKey() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPage
	 */
	public ICPage getRootPage() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageId
	 */
	public int getCurrentPageId(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageKey
	 */
	public String getCurrentPageKey(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPage
	 */
	public ICPage getCurrentPage(IWContext iwc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageTree
	 */
	public ICTreeNode getPageTree(int startNodeId, int userId) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageTree
	 */
	public ICTreeNode getPageTree(int startNodeId) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#unload
	 */
	public void unload();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageKeyByRequestURIAndServerName
	 */
	public String getPageKeyByRequestURIAndServerName(String pageRequestUri, String serverName);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCopyOfUIComponentFromIBXML
	 */
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#write
	 */
	public Object write(ICPage page, ObjectWriter writer, IWContext iwc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#movePage
	 */
	public boolean movePage(int newParentId, int nodeId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelPages
	 */
	public Collection getTopLevelPages(IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTemplateKey
	 */
	public String getTemplateKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageKey
	 */
	public String getPageKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getHTMLTemplateKey
	 */
	public String getHTMLTemplateKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelTemplateId
	 */
	public String getTopLevelTemplateId(Collection templates);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createNewPage
	 */
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createPageOrTemplateToplevelOrWithParent
	 */
	public int createPageOrTemplateToplevelOrWithParent(String name, String parentId, String type, String templateId, Map tree, IWContext creatorContext);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setProperty
	 */
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getICPage
	 */
	public ICPage getICPage(String key);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#deletePage
	 */
	public boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#checkDeletePage
	 */
	public boolean checkDeletePage(String pageId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#clearAllCachedPages
	 */
	public void clearAllCachedPages();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setTemplateId
	 */
	public void setTemplateId(String pageKey, String newTemplateId);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getIBXMLFormat
	 */
	public String getIBXMLFormat();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPropertyValues
	 */
	public String[] getPropertyValues(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] selectedValues, boolean returnSelectedValueIfNothingFound);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#removeProperty
	 */
	public boolean removeProperty(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] values);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#changePageUriByTitle
	 */
	public boolean changePageUriByTitle(String parentId, ICPage page, String pageTitle, int domainId);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#movePageToTopLevel
	 */
	public boolean movePageToTopLevel(int pageID, IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createTopLevelPageFromExistingPage
	 */
	public void createTopLevelPageFromExistingPage(int pageID, ICDomain domain, IWUserContext creatorContext);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#isPageTopLevelPage
	 */
	public boolean isPageTopLevelPage(int pageID, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#unlockRegion
	 */
	public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setCurrentPageId
	 */
	public void setCurrentPageId(IWContext iwc, String pageKey);
	
	/**
	 * @see com.idega.builder.business.IBMainServiceBean#addPropertyToModule
	 */
	public boolean addPropertyToModule(String pageKey, String moduleId, String propName, String propValue);
	
	public boolean addPropertyToModules(String pageKey, List<String> moduleIds, String propName, String propValue);
	
	public List<String> getModuleId(String pageKey, String moduleClass);
	
	public boolean isPropertySet(String pageKey, String instanceId, String propertyName, IWMainApplication iwma);
	
	public boolean isPropertyValueSet(String pageKey, String moduleId, String propertyName, String propertyValue);
	
	public boolean removeValueFromModuleProperty(String pageKey, String moduleId, String propertyName, String valueToRemove);
	
	public boolean removeValueFromModulesProperties(String pageKey, List<String> moduleIds, String propertyName, String valueToRemove);
	
	public boolean removeBlockObjectFromCache(IWContext iwc, String cacheKey);
	
	public void startBuilderSession(IWContext iwc);
}