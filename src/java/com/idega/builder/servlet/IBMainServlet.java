/*
 * $Id: IBMainServlet.java,v 1.4 2001/07/16 09:51:18 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.servlet;

import com.idega.jmodule.JSPModule;
import com.idega.jmodule.object.ModuleInfo;

import com.idega.builder.business.BuilderLogic;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBMainServlet extends JSPModule {



  public void initializePage(){
    ModuleInfo modinfo = getModuleInfo();
    int id;
    String page_id = modinfo.getParameter("idegaweb_page_id");
    if(page_id == null){
      id = 1;
    }
    else {
      id = Integer.parseInt(page_id);
    }
    setPage(BuilderLogic.getInstance().getPage(id,modinfo));

  }







/*


    public void __theService(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
      try {
        main(getModuleInfo());
      }
      catch(SQLException ex) {
        ex.printStackTrace(System.err);
      }
      //_jspService(request,response);
    }

  public void main(ModuleInfo modinfo)throws IOException,SQLException {
    //boolean isAdmin = AccessControl.isAdmin(modinfo);
    boolean isAdmin = true;
    PrintWriter debugger = modinfo.getResponse().getWriter();
    int id;

    String page_id = modinfo.getParameter("idegaweb_page_id");
    if(page_id == null){
      id = 1;
    }
    else {
      id = Integer.parseInt(page_id);
    }

    String language = modinfo.getParameter("language");
    if(language == null){
      language = "IS";
    }

    //IBPage ib_page = new IBPage(id);
    //IBAdminWindow window = new IBAdminWindow();

    try {

    }
    catch(Exception ex) {
      add("villa 1");
      System.err.println("ERROR!!!!!!");
      ex.printStackTrace(modinfo.getResponse().getWriter());
    }

    //AdminButton form = new AdminButton("B�ta vi�",window);
    if (isAdmin) {
      AdminButton form = new AdminButton(new Image("/common/pics/arachnea/add.gif"),window);
      form.addParameter("ib_window_action","window1");
      form.addParameter(new Parameter("page_id",Integer.toString(id)));

      //Form form = new Form(new Window("Baeta","window1.jsp"));
      //form.add(new SubmitButton("B�ta vi�"));
      //form.add(new Parameter("page_id",""+id));

      add(form);
    }

  }

*/

}
