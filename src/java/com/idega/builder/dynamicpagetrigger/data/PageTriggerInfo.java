package com.idega.builder.dynamicpagetrigger.data;

import com.idega.data.IDOAddRelationshipException;
import com.idega.user.data.Group;


public interface PageTriggerInfo extends com.idega.data.IDOLegacyEntity
{
 public int getDefaultTemplateId();
 public int getICObjectID();
 public java.lang.String getName();
 public int getRootPageId();
 public void setDefaultTemplateId(int p0);
 public void setICObject(com.idega.core.component.data.ICObject p0);
 public void setICObject(int p0);
 public void setName(java.lang.String p0);
 public void setRootPageId(int p0);
 public void setRelatedGroup(Group gr) throws IDOAddRelationshipException;
}
