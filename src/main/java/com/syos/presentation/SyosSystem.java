package com.syos.presentation;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.jasper.servlet.JspServlet;
import org.apache.jasper.runtime.JspFactoryImpl;

public class SyosSystem {
	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(9090);

		File warFile = new File("target/syos-billing-system-0.0.1-SNAPSHOT.war");
		if (warFile.exists()) {
			// Deploy the WAR file for packaged application
			tomcat.addWebapp("", warFile.getAbsolutePath());
		} else {
			// For development, use src/main/webapp and add servlets programmatically
			File docBase = new File("src/main/webapp");
			Context context = tomcat.addContext("", docBase.getAbsolutePath());
			context.addWelcomeFile("index.jsp");
			context.addWelcomeFile("index.html");

			// Add all servlets from web.xml
			Tomcat.addServlet(context, "BillingServlet", new BillingServlet());
			context.addServletMappingDecoded("/api/billing/*", "BillingServlet");

			Tomcat.addServlet(context, "InventoryServlet", new InventoryServlet());
			context.addServletMappingDecoded("/api/inventory/*", "InventoryServlet");


			Tomcat.addServlet(context, "ReportServlet", new ReportServlet());
			context.addServletMappingDecoded("/api/reports/*", "ReportServlet");

			Tomcat.addServlet(context, "AuthServlet", new AuthServlet());
			context.addServletMappingDecoded("/auth", "AuthServlet");
			context.addServletMappingDecoded("/logout", "AuthServlet");

			Tomcat.addServlet(context, "ProductWebServlet", new ProductWebServlet());
			context.addServletMappingDecoded("/admin/products/*", "ProductWebServlet");



			Tomcat.addServlet(context, "default", new DefaultServlet());
			((StandardWrapper) context.findChild("default")).addInitParameter("welcomeFiles", "index.jsp,index.html");
			context.addServletMappingDecoded("/", "default");

			Tomcat.addServlet(context, "jsp", new JspServlet());
			context.addServletMappingDecoded("*.jsp", "jsp");
		}
		javax.servlet.jsp.JspFactory.setDefaultFactory(new JspFactoryImpl());

		try {
			tomcat.start();
			System.out.println("Tomcat started on port " + tomcat.getConnector().getPort());
			tomcat.getServer().await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
