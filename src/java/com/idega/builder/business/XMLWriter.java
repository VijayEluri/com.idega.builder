package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author       <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

 import org.jdom.Element;
 import org.jdom.Attribute;

 import java.util.List;
 import java.util.Iterator;

 import com.idega.core.data.ICObject;
 import com.idega.core.data.ICObjectInstance;


public class XMLWriter {

  private static final String PROPERTY_STRING = "property";
  private static final String VALUE_STRING = "value";
  private static final String TYPE_STRING = "type";
  private static final String NAME_STRING = "name";
  private static final String ID_STRING = "id";
  private static final String MODULE_STRING = "module";
  private static final String REGION_STRING = "region";

  //private static Element rootElement;

  private XMLWriter() {
  }

  private static Element getPageRootElement(IBXMLPage xml){
    return xml.getPageRootElement();
  }

  private static Element findRegion(IBXMLPage xml,String id){
    return findXMLElement(xml,id,REGION_STRING);
  }

  private static Element findRegion(IBXMLPage xml,String id,Element enclosingModule){
    return findXMLElementInside(xml,id,REGION_STRING,enclosingModule);
  }

  private static Element findModule(IBXMLPage xml,int id){
    return findXMLElement(xml,Integer.toString(id),MODULE_STRING);
  }

  private static Element findModule(IBXMLPage xml,int id,Element startElement){
    return findXMLElementInside(xml,Integer.toString(id),MODULE_STRING,startElement);
  }

  /**
   * Returns null if nothing found
   */
  private static Element findXMLElement(IBXMLPage xml,String id,String name){
    return findXMLElementInside(xml,id,name,getPageRootElement(xml));
  }

  /**
   * Returns null if nothing found.
   *
   * If name is null it searches all elements with any name
   */
  private static Element findXMLElementInside(IBXMLPage xml, String id,String name,Element parentElement){
    List list = parentElement.getChildren();
    boolean nameIsNull;
    if(id!=null){
      try{
        int theID = Integer.parseInt(id);
        if(theID==-1){
          return getPageRootElement(xml);
        }
      }
      catch(NumberFormatException e){

      }
    }

    if(name==null){
      nameIsNull=true;
    }
    else{
      nameIsNull=true;
    }

    if(list !=null){
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Element element = (Element)iter.next();
        //if(element.getName().equals(name)||nameIsNull){
          //List attributes = element.getAttributes();
          //if(attributes!=null){
          //Iterator iter2 = attributes.iterator();
          //while (iter2.hasNext()){
            Attribute attr = element.getAttribute(ID_STRING);
            //Attribute attr = (Attribute)iter2.next();
            //if(item2.getName().equals(ID_STRING)){
              if(attr!=null){
                if(attr.getValue().equals(id)){
                  return element;
                }
              }
            //}
          //}

          //}
        //}
        //else{
          Element el = findXMLElementInside(xml,id,null,element);
          if(el!=null){
            return el;
          }
        //}
      }
    }
    return null;
  }


  private static Element findProperty(IBXMLPage xml,int ObjectInstanceId,String propertyName){
    Element elem = findModule(xml,ObjectInstanceId);
    return findProperty(elem,propertyName);
  }

  private static Element findProperty(Element parentElement,String propertyName){
    Element elem = parentElement;
    if(elem != null){
      List properties = elem.getChildren();
      if(properties != null){
        Iterator iter = properties.iterator();
        while (iter.hasNext()) {
          Element pElement = (Element)iter.next();
          if(pElement!=null){
            if(pElement.getName().equals(PROPERTY_STRING)){
              Element name = pElement.getChild(NAME_STRING);
              if(name!=null){
                if(name.getText().equals(propertyName)){
                  return pElement;
                }
              }
            }
          }
        }
      }
    }
    return null;
  }



  static boolean setProperty(IBXMLPage xml,int ObjectInstanceId,String propertyName,String propertyValue){
    Element module = findModule(xml,ObjectInstanceId);
    Element property = findProperty(module,propertyName);

    if(property==null){
      property = getNewProperty(propertyName,propertyValue);
      module.addContent(property);
      System.out.println("property==null");
    }
    else{
      System.out.println("property!=null");
      Element value = property.getChild(VALUE_STRING);
      if(value!=null){
        value.setText(propertyValue);
      }
      else{
        value = new Element(VALUE_STRING);
        value.addContent(propertyValue);
        property.addContent(value);
      }
    }
    return true;
  }


  private static Element getNewProperty(String propertyName,Object propertyValue){

    Element element = new Element(PROPERTY_STRING);
    Element name = new Element(NAME_STRING);
    Element value = new Element(VALUE_STRING);
    Element type = new Element(TYPE_STRING);

    element.addContent(name);
    element.addContent(value);
    element.addContent(type);

    name.addContent(propertyName);
    value.addContent(propertyValue.toString());
    type.addContent(propertyValue.getClass().getName());

    return element;
  }

  //public static boolean addNewModule(String parentObjectInstanceID,int newICObjectTypeID){
  private static boolean addNewModule(Element parent,int newICObjectTypeID){
    //Element parent = findModule(parentObjectInstanceID);
    if(parent!=null){
      try{
        ICObjectInstance instance = new ICObjectInstance();
        instance.setICObjectID(newICObjectTypeID);
        instance.insert();

        Element newElement = new Element(MODULE_STRING);
        Attribute id = new Attribute(ID_STRING,Integer.toString(instance.getID()));
        newElement.addAttribute(id);
        parent.addContent(newElement);

      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }

      return true;

    }
    return false;
  }

  static boolean addNewModule(IBXMLPage xml,int parentObjectInstanceID,int newICObjectID,int xpos,int ypos){

    String regionId = parentObjectInstanceID+"."+xpos+"."+ypos;
    Element region = findRegion(xml,regionId);

    if(region==null){
      region = new Element(REGION_STRING);
      Attribute id = new Attribute(ID_STRING,regionId);
      region.addAttribute(id);
      addNewModule(region,newICObjectID);
      System.out.println("instance="+parentObjectInstanceID);
      System.out.println("x="+xpos);
      System.out.println("y="+ypos);
      Element parent = findModule(xml,parentObjectInstanceID);
      parent.addContent(region);
    }
    else{
      addNewModule(region,newICObjectID);
    }
    return true;

  }

  static boolean addNewModule(IBXMLPage xml,int parentObjectInstanceID,int newICObjectID){
    return addNewModule(findModule(xml,parentObjectInstanceID),newICObjectID);
  }


  static boolean addNewModule(IBXMLPage xml,String parentObjectInstanceID,int newICObjectID){
    try{
      return addNewModule(findModule(xml,Integer.parseInt(parentObjectInstanceID)),newICObjectID);
    }
    catch(NumberFormatException nfe){

      int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,parentObjectInstanceID.indexOf(".")));
      String theRest = parentObjectInstanceID.substring(parentObjectInstanceID.indexOf(".")+1,parentObjectInstanceID.length());

      int xpos = Integer.parseInt(theRest.substring(0,theRest.indexOf(".")));
      int ypos = Integer.parseInt(theRest.substring(theRest.indexOf(".")+1),theRest.length());

      return addNewModule(xml,parentID,newICObjectID,xpos,ypos);
    }
  }

  static boolean addNewModule(IBXMLPage xml,String parentObjectInstanceID,ICObject newObjectType){
    return addNewModule(xml,parentObjectInstanceID,newObjectType.getID());
  }


  static boolean deleteModule(IBXMLPage xml,String parentObjectInstanceID,int ICObjectInstanceID){
    Element parent = findXMLElement(xml,parentObjectInstanceID,null);
    if(parent!=null){
      try{
        Element module = findModule(xml,ICObjectInstanceID,parent);
        return deleteModule(parent,module);
      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  private static boolean deleteModule(Element parent,Element child)throws Exception{
        List children = getChildElements(child);
        if(children!=null){
          Iterator iter = children.iterator();
          while (iter.hasNext()) {
            Element childchild = (Element)iter.next();
            deleteModule(child,childchild);
          }
          Attribute attribute = child.getAttribute(ID_STRING);
          if(attribute!=null){
            String ICObjectInstanceID = attribute.getValue();
            try{
              ICObjectInstance instance = new ICObjectInstance(Integer.parseInt(ICObjectInstanceID));
              instance.delete();
            }
            catch(NumberFormatException e){
            }
          }
        }
        parent.removeContent(child);
        return true;
  }


  private static List getChildElements(Element parent){
    return parent.getChildren();
  }

  private static List getChildModules(Element parent){
    List children = parent.getChildren();
    Iterator iter = children.iterator();
    while (iter.hasNext()) {
      Element item = (Element)iter.next();
      if(item.getName().equals(REGION_STRING)){
        children.addAll(getChildModules((Element)item));
      }
      else if(!item.getName().equals(MODULE_STRING)){
        iter.remove();
      }
    }
    return children;
  }
}