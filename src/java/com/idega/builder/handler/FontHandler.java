package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.text.Text;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class FontHandler implements PropertyHandler {

  public FontHandler() {
  }
  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Select:");
    menu.addMenuElement(Text.FONT_FACE_ARIAL,"Arial-Helvetica");
    menu.addMenuElement(Text.FONT_FACE_TIMES,"Times");
    menu.addMenuElement(Text.FONT_FACE_COURIER,"Courier");
    menu.addMenuElement(Text.FONT_FACE_GENEVA,"Geneva");
    menu.addMenuElement(Text.FONT_FACE_VERDANA,"Verdana");
    menu.setSelectedElement(value);
    return menu;
  }

}