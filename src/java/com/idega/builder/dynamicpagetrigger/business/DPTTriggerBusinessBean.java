package com.idega.builder.dynamicpagetrigger.business;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageFinder;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroup;
import com.idega.builder.dynamicpagetrigger.data.PageLink;
import com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.text.Link;
import com.idega.util.IWTimestamp;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class DPTTriggerBusinessBean extends IBOServiceBean {


  public static DPTTriggerBusiness getInstance(IWApplicationContext iwac) throws IBOLookupException{
    return (DPTTriggerBusiness)IBOLookup.getServiceInstance(iwac,DPTTriggerBusiness.class);
  }

  public int createTriggerRule(ICObject source, int defaultTemplateId,int[] objectInstanceIds, ICPage[] templatesAllowed) throws SQLException{
    PageTriggerInfo pti = ((com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoHome)com.idega.data.IDOLookup.getHomeLegacy(PageTriggerInfo.class)).createLegacy();

    pti.setICObject(source);
    pti.setDefaultTemplateId(defaultTemplateId);
    pti.insert();

    if(objectInstanceIds != null){
      for (int i = 0; i < objectInstanceIds.length; i++) {
        pti.addTo(ICObjectInstance.class,objectInstanceIds[i]);
      }
    }

    if(templatesAllowed != null){
      for (int i = 0; i < templatesAllowed.length; i++) {
        pti.addTo(ICPage.class,templatesAllowed[i].getID());
      }
    }

    return pti.getID();

  }

  /*
  public void deleteTriggerRule(PageTriggerInfo pti) throws SQLException{
    pti.removeFrom(IBPage.class);
    pti.removeFrom(ICObjectInstance.class);
    // delete from pageLink where pti_id = thispti_id
    pti.delete();
  }
*/

  public void addTemplateToRule(PageTriggerInfo pti, int ibPageId) throws SQLException{
    pti.addTo(ICPage.class,ibPageId);
  }

  public void addTemplateToRule(ICPage ibp, int ptiId) throws SQLException{
    ibp.addTo(PageTriggerInfo.class,ptiId);
  }

  public void addRuleToInstance(PageTriggerInfo pti, int objectInstanceId) throws SQLException{
    pti.addTo(ICObjectInstance.class,objectInstanceId);
  }

  public void addRuleToInstance(ICObjectInstance icoi, int icoiID) throws SQLException{
    icoi.addTo(PageTriggerInfo.class,icoiID);
  }




  public void removeTemplateFromRule(PageTriggerInfo pti, int ibPageId) throws SQLException{
    pti.removeFrom(ICPage.class,ibPageId);
  }

  public void removeTemplateFromRule(ICPage ibp, int ptiId) throws SQLException{
    ibp.removeFrom(PageTriggerInfo.class,ptiId);
  }

  public void removeRuleFromInstance(PageTriggerInfo pti, int objectInstanceId) throws SQLException{
    pti.removeFrom(ICObjectInstance.class,objectInstanceId);
  }

  public void removeRuleFromInstance(ICObjectInstance icoi, int icoiID) throws SQLException{
    icoi.removeFrom(PageTriggerInfo.class,icoiID);
  }


  public PageLink createPageLink(IWContext iwc, PageTriggerInfo pti, String referencedDataId, String defaultLinkText, String standardParameters, Integer imageFileId, Integer onMouseOverImageFileId, Integer onClickImageFileId) throws SQLException {
    PageLink pl = ((com.idega.builder.dynamicpagetrigger.data.PageLinkHome)com.idega.data.IDOLookup.getHomeLegacy(PageLink.class)).createLegacy();

    pl.setPageTriggerInfoId(pti.getID());
    pl.setReferencedDataId(referencedDataId);
    pl.setDefaultLinkText(defaultLinkText);

    if(standardParameters != null){
      pl.setStandardParameters(standardParameters);
    }

    int pageId = createPage(iwc,pti.getDefaultTemplateId(), pti.getRootPageId(), defaultLinkText);

    if(pageId == -1){
      return (null);
    }

    pl.setPageId(pageId);


    pl.insert();

    return pl;

  }

  public List getPageLinkRecords(ICObjectInstance instance) throws SQLException{
    List listOfCopyRules = EntityFinder.findRelated(instance,((PageLink)com.idega.builder.dynamicpagetrigger.data.PageLinkBMPBean.getStaticInstance(PageLink.class)));

    if (listOfCopyRules != null) {
      List toReturn = new Vector();
      Iterator iter = listOfCopyRules.iterator();
      while (iter.hasNext()) {
        PageTriggerInfo item = (PageTriggerInfo)iter.next();
        List linkList = EntityFinder.findAllByColumn(com.idega.builder.dynamicpagetrigger.data.PageLinkBMPBean.getStaticInstance(PageLink.class),com.idega.builder.dynamicpagetrigger.data.PageLinkBMPBean._COLUMNNAME_PAGE_TRIGGER_INFO_ID,item.getID());
        if(linkList != null){
          toReturn.addAll(linkList);
        }
      }
/*
      IDOLegacyEntityComparator c = new IDOLegacyEntityComparator(com.idega.builder.dynamicpagetrigger.data.PageLinkBMPBean._COLUMNNAME_DEFAULT_LINK_TEXT);
      Collections.sort(toReturn,c);
*/
      return toReturn;

    } else {
      return null;
    }
  }

  /*
  public int triggerPage(){

  }
  */


  private int createPage(IWContext iwc, int dptTemplateId, int parentId, String name, Map createdPages) throws SQLException{
    BuilderLogic instance = BuilderLogic.getInstance();

    Map tree = PageTreeNode.getTree(iwc);

    int id = IBPageHelper.getInstance().createNewPage(Integer.toString(parentId),name,IBPageHelper.DPT_PAGE,Integer.toString(dptTemplateId),tree,iwc);

    if(id == -1){
      return (-1);
    }

    try {
      ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(id);
    }
    catch(SQLException e) {
      return (-1);
    }
/*    IBPage page = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
    if (name == null){
      name = "Untitled";
    }
    page.setName(name);
    page.setType(com.idega.builder.data.IBPageBMPBean.PAGE);
    page.setTemplateId(dptTemplateId);

    try {
      page.insert();
      IBPage ibPageParent = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(parentId);
      ibPageParent.addChild(page);
    }
    catch(SQLException e) {
      return(-1);
    }
*/
    copyPagePermissions(Integer.toString(dptTemplateId), Integer.toString(id));


    createdPages.put(Integer.toString(dptTemplateId),Integer.toString(id));
/*
    instance.setTemplateId(Integer.toString(page.getID()),Integer.toString(dptTemplateId));
    IBXMLPage ibxmlPage =  instance.getIBXMLPage(dptTemplateId);
    ibxmlPage.addUsingTemplate(Integer.toString(page.getID()));

*/

    IBXMLPage currentXMLPage = instance.getIBXMLPage(id);
    Page current = currentXMLPage.getPopulatedPage();
    List children = current.getChildrenRecursive();
/*
    if (children != null) {
      Iterator it = children.iterator();
      while (it.hasNext()) {
        PresentationObject obj = (PresentationObject)it.next();
        boolean ok = changeInstanceId(obj,currentXMLPage,true);
        if(!ok){
          return(-1);
        }
      }
    }
*/
    if(children != null){
      Iterator iter = children.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        if(!(item instanceof Link)){
          iter.remove();
        }else{
          Link link = (Link)item;
          if(link.getDPTTemplateId() == 0){
            iter.remove();
          }
        }
      }
      String pageIDString = Integer.toString(id);
      iter = children.iterator();
      while(iter.hasNext()){
        Link item = (Link)iter.next();

        int templateId = item.getDPTTemplateId();
        String createdPage = (String)createdPages.get(Integer.toString(templateId));
        if(createdPage == null){
          String subpageName = item.getText();
          if(subpageName == null){
            subpageName = "Untitled";
          }
          int newID = this.createPage(iwc,templateId, id, subpageName,createdPages);
          if(newID == -1){
            return (-1);
          }
          instance.changeLinkPageId(item,pageIDString,Integer.toString(newID));
        } else {
          instance.changeLinkPageId(item,pageIDString,createdPage);
        }
      }
    }

    return id;
  }


  private int createPage(IWContext iwc, int dptTemplateId, int parentId, String name) throws SQLException{
    return createPage(iwc, dptTemplateId, parentId, name, new Hashtable());
  }


  public void copyInstancePermissions( String oldInstanceID, String newInstanceID) throws SQLException{
    AccessControl.copyObjectInstancePermissions(oldInstanceID,newInstanceID);
    //
    //
    //
    /**
     * getTemplateGroups (linked to copyRule)
     * copy group
     * add templateGroup to new group
     * set group to have same permission as templateGroup for new module id
     */
    //
    //
    //
  }

  public void copyPagePermissions( String oldPageID, String newPageID) throws SQLException{
    AccessControl.copyPagePermissions(oldPageID,newPageID);
    //
    //
    //
    /**
     * getTemplateGroups (linked to copyRule)
     * copy group
     * add templateGroup to new group
     * set group to have same permission as templateGroup for new module id
     */
    //
    //
    //
  }



  /**
   *
   *//*
  private static boolean changeInstanceId(PresentationObject obj, IBXMLPage xmlpage, boolean copyPermissions) {
    if (obj.getChangeInstanceIDOnInheritance()) {
      int object_id = obj.getICObjectID();
      int ic_instance_id = obj.getICObjectInstanceID();
      ICObjectInstance instance = null;

      try {
        instance = ((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).createLegacy();
        instance.setICObjectID(object_id);
        instance.insert();
        if(copyPermissions){
          copyInstencePermissions(Integer.toString(ic_instance_id),Integer.toString(instance.getID()));
        }
      }
      catch(SQLException e) {
        //System.err.println("DPTTriggerBusiness: "+e.getMessage());
        //e.printStackTrace();
        return(false);
      }

      if(obj instanceof IWBlock){
        boolean ok = ((IWBlock)obj).copyBlock(instance.getID());
        if (!ok){
          return(false);
        }
      }

      XMLElement element = new XMLElement(XMLConstants.CHANGE_IC_INSTANCE_ID);
      XMLAttribute from = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_FROM,Integer.toString(ic_instance_id));
      XMLAttribute to = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_TO,Integer.toString(instance.getID()));
      element.setAttribute(from);
      element.setAttribute(to);

      XMLWriter.addNewElement(xmlpage,-1,element);
    }

    return(true);
  }
*/

  public List getDPTPermissionGroups(PageTriggerInfo pti) throws SQLException{
    return EntityFinder.findRelated(pti, com.idega.core.data.GenericGroupBMPBean.getStaticInstance());
  }

  public static void createDPTPermissionGroup(PageTriggerInfo pti, String name, String description) throws SQLException {
    DPTPermissionGroup newGroup = ((com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupHome)com.idega.data.IDOLookup.getHomeLegacy(DPTPermissionGroup.class)).createLegacy();
    newGroup.setName(name);
    newGroup.setDescription(description);

    newGroup.insert();


    pti.addTo(newGroup);

  }


  public boolean invalidatePageLink(IWContext iwc, PageLink l, int userId){
    try {
      l.setDeleted(true);
      l.setDeletedBy(userId);
      l.setDeletedWhen(IWTimestamp.getTimestampRightNow());
      l.update();


      com.idega.builder.business.IBPageHelper.getInstance().deletePage(Integer.toString(l.getPageId()),true,PageTreeNode.getTree(iwc),userId);

      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }


  public boolean addObjectInstancToSubPages(ICObjectInstance objectTemplate){
    System.out.println("addObjectInstancToSubPages begins");
    List pages = IBPageFinder.getAllPagesExtendingTemplate(objectTemplate.getIBPageID());
    if(pages != null){
      System.out.println("addObjectInstancToSubPages - pages != null");
      Iterator iter = pages.iterator();
      int counter = 1;
      while (iter.hasNext()) {
        System.out.println("-----------");
        System.out.println("addObjectInstancToSubPages - addElementToPage : "+counter++);
        ICPage item = (ICPage)iter.next();
        IBPageHelper.getInstance().addElementToPage(item,objectTemplate.getID());
      }
    }else {
      System.out.println("addObjectInstancToSubPages - pages == null");
    }
    System.out.println("addObjectInstancToSubPages ends");
    return(true);
  }

  public boolean addObjectInstancToSubPages(int templateObjectInstanceID) throws SQLException{
    ICObjectInstance objinst = (ICObjectInstance)com.idega.data.IDOLookup.findByPrimaryKeyLegacy(ICObjectInstance.class,templateObjectInstanceID);
    return addObjectInstancToSubPages(objinst);
  }



}