package com.idega.builder.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.data.IBExportImportData;
import com.idega.builder.data.IBReferences;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 12, 2004
 */
public class IBPageExportBusinessBean extends IBOServiceBean implements IBPageExportBusiness {
	
	private ICPageHome pageHome = null;
	private FileBusiness fileBusiness = null;
	private IBReferences references = null;

	
	public String exportPages(List pageIds, List templateIds, IWContext iwc) throws IOException, FinderException {
		List ids = null;
		boolean pageIdsExists = (pageIds != null && ! pageIds.isEmpty());
		boolean templateIdsExists = (templateIds != null && ! templateIds.isEmpty());
		if (! pageIdsExists && ! templateIdsExists) {
			return "";
		}
		
		IBExportImportData metadata = new IBExportImportData();
		if (pageIdsExists) {
			metadata.addPageTree(iwc);
			ids = new ArrayList(pageIds);
		}
		
		if (templateIdsExists) {
			metadata.addTemplateTree(iwc);
			if (pageIdsExists) {
				ids.addAll(templateIds);
			}
			else {
				ids = new ArrayList(templateIds);
			}
		}
		return exportPages(ids, metadata);
	}
		
	private String exportPages(List pageIds,IBExportImportData metadata) throws IOException, FinderException  {
  	Iterator pageIterator = pageIds.iterator();
  	while (pageIterator.hasNext()) {
  		Integer pageId = (Integer) pageIterator.next();
  		ICPageHome pageHome = getPageHome();
  		ICPage page = pageHome.findByPrimaryKey(pageId);
  		ICFile file = page.getFile();
  		XMLData xmlData = XMLData.getInstanceForFile(file);
  		XMLDocument pageXML = xmlData.getDocument();
  		XMLElement pageRoot = pageXML.getRootElement().getChild(XMLConstants.PAGE_STRING);
  		getReferences().checkElementForReferences(pageRoot, metadata);
  		metadata.addFileEntry(page);
  	}
  	FileBusiness fileBusiness = getFileBusiness();
  	return fileBusiness.getURLForOfferingDownload(metadata);
  }
	
	private IBReferences getReferences() throws IOException {
		if (references == null) {
			references = new IBReferences(getIWMainApplication());
		}
		return references;
	}	
	
	private ICPageHome getPageHome() throws IDOLookupException {
		if (pageHome == null)  {
			pageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
		}
		return pageHome;
	}
	
	private FileBusiness getFileBusiness() throws IBOLookupException {
		if (fileBusiness == null) {
			fileBusiness =  (FileBusiness) IBOLookup.getServiceInstance( getIWApplicationContext(), FileBusiness.class);
		}
		return fileBusiness;
	}

	
}