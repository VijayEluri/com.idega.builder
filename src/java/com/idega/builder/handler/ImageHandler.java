/*
 * $Id: ImageHandler.java,v 1.5 2003/04/03 09:10:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.block.media.presentation.ImageInserter;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ImageHandler implements PropertyHandler {
  /**
   *
   */
  public ImageHandler() {
  }

  /**
   *
   */
  public List getDefaultHandlerTypes() {
    return(null);
  }

  /**
   *
   */
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    ImageInserter po = new ImageInserter(name,false);
    po.setNullImageIDDefault();
    try {
      po.setImageId(Integer.parseInt(value));
    }
    catch(NumberFormatException e) {
    }

    return(po);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
