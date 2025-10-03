package com.syos.presentation;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.jasper.servlet.JspServlet;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.jasper.servlet.JasperInitializer;

public class SyosSystem {
	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(9090);

		File warFile = new File("target/syos-billing-system-0.0.1-SNAPSHOT.war");
		if (warFile.exists()) {
			tomcat.addWebapp("", warFile.getAbsolutePath());
		} else {
			File docBase = new File("src/main/webapp");
			Context context = tomcat.addContext("", docBase.getAbsolutePath());
			context.setParentClassLoader(SyosSystem.class.getClassLoader());
			context.addWelcomeFile("index.jsp");
			context.addWelcomeFile("index.html");

			Tomcat.addServlet(context, "BillingServlet", new BillingServlet());
			context.addServletMappingDecoded("/billing/*", "BillingServlet");

			Tomcat.addServlet(context, "InventoryServlet", new InventoryServlet());
			context.addServletMappingDecoded("/inventory/*", "InventoryServlet");


			Tomcat.addServlet(context, "ReportServlet", new ReportServlet());
			context.addServletMappingDecoded("/reports/*", "ReportServlet");

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

			try {
				context.addServletContainerInitializer(new JasperInitializer(), null);
			} catch (Exception e) {
				System.err.println("Failed to initialize Jasper: " + e.getMessage());
				e.printStackTrace();
			}
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
