package com.idega.builder.presentation;

import com.idega.builder.business.BuilderConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.AbstractChooser;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IBStyleChooser extends AbstractChooser {
  public IBStyleChooser(String chooserName) {
    addForm(false);
    //setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
    setChooserParameter(chooserName);
  }

  public IBStyleChooser(String chooserName,String style) {
    this(chooserName);
    setInputStyle(style);
  }

  @Override
public void main(IWContext iwc){
    IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderConstants.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose"));
  }

  @Override
public Class getChooserWindowClass() {
    return IBStyleChooserWindow.class;
  }

  public void setSelected(String style){
    super.setChooserValue(style,style);
    super.setParameterValue("style",style);
  }
  
  @Override
  public String getChooserHelperVarName() {
	  return "style_chooser_helper";
  }
}