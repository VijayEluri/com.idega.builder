/*
 * $Id: IBDomainBMPBean.java,v 1.2 2002/05/10 15:55:26 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.data.IBDomain;
//import com.idega.data.IDOLegacyEntity;
import com.idega.builder.data.IBDomainHome;
import com.idega.builder.data.IBPageHome;
import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBPageBMPBean;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.sql.SQLException;
import javax.ejb.FinderException;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBDomainBMPBean extends GenericEntity implements IBDomain {
  public static final String tableName = "ib_domain";
  public static final String domain_name = "domain_name";
  public static final String domain_url = "url";
  public static final String start_page = "start_ib_page_id";
  public static final String start_template = "start_ib_template_id";

  private static Map cachedDomains;

  public IBDomainBMPBean() {
    super();
  }

  private IBDomainBMPBean(int id) throws java.sql.SQLException {
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnDomainName(),"Domain name",true,true,String.class);
    addAttribute(getColumnURL(),"Domain URL",true,true,String.class,1000);
    addAttribute(getColumnStartPage(),"Start Page",true,true,Integer.class,"many-to-one",IBPage.class);
    addAttribute(getColumnStartTemplate(),"Start Template",true,true,Integer.class,"many-to-one",IBPage.class);
  }

  public static IBDomain getDomain(int id)throws SQLException {
    IBDomain theReturn;
    theReturn = (IBDomain)getDomainsMap().get(new Integer(id));
    if (theReturn == null) {
      theReturn = ((IBDomainHome)IDOLookup.getHomeLegacy(IBDomain.class)).findByPrimaryKeyLegacy(id);
      if (theReturn != null) {
        getDomainsMap().put(new Integer(id),theReturn);
      }
    }
    return(theReturn);
  }

  private static Map getDomainsMap() {
    if (cachedDomains==null) {
      cachedDomains = new HashMap();
    }
    return(cachedDomains);
  }

  public void insertStartData() throws Exception {
    BuilderLogic instance = BuilderLogic.getInstance();
    IBDomain domain = ((IBDomainHome)IDOLookup.getHomeLegacy(IBDomain.class)).createLegacy();
    domain.setName("Default Site");

    IBPage page = ((IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
    page.setName("Web root");
    page.setType(com.idega.builder.data.IBPageBMPBean.PAGE);
    page.insert();
    instance.unlockRegion(Integer.toString(page.getID()),"-1",null);

    IBPage page2 = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
    page2.setName("Default Template");
    page2.setType(com.idega.builder.data.IBPageBMPBean.TEMPLATE);
    page2.insert();

    instance.unlockRegion(Integer.toString(page2.getID()),"-1",null);

    page.setTemplateId(page2.getID());
    page.update();

    domain.setIBPage(page);
    domain.setStartTemplate(page2);
    domain.insert();

    instance.setTemplateId(Integer.toString(page.getID()),Integer.toString(page2.getID()));
    instance.getIBXMLPage(page2.getID()).addUsingTemplate(Integer.toString(page.getID()));
  }

  public String getEntityName() {
    return(tableName);
  }

  public static String getColumnDomainName() {
    return(domain_name);
  }

  public static String getColumnURL() {
    return(domain_url);
  }

  public static String getColumnStartPage() {
    return(start_page);
  }

  public static String getColumnStartTemplate() {
    return(start_template);
  }

  public IBPage getStartPage() {
    return((IBPage)getColumnValue(getColumnStartPage()));
  }

  public int getStartPageID() {
    return(getIntColumnValue(getColumnStartPage()));
  }

  public IBPage getStartTemplate() {
    return((IBPage)getColumnValue(getColumnStartTemplate()));
  }

  public int getStartTemplateID() {
    return(getIntColumnValue(getColumnStartTemplate()));
  }

  public String getName() {
    return(getDomainName());
  }

  public String getDomainName() {
    return(getStringColumnValue(getColumnDomainName()));
  }

  public String getURL() {
    return(getStringColumnValue(getColumnURL()));
  }

  public void setIBPage(IBPage page) {
     setColumn(getColumnStartPage(),page);
  }

  public void setStartTemplate(IBPage template) {
    setColumn(getColumnStartTemplate(),template);
  }

  public void setName(String name) {
    setColumn(getColumnDomainName(),name);
  }

  public Collection ejbFindAllDomains() throws FinderException {
    String sql = "select * from " + getTableName();
    return super.idoFindIDsBySQL(sql);
  }
}