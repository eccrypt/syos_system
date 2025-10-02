package com.syos.presentation;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class SyosSystem {
	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(9090);

		File docBase = new File("src/main/webapp");
		if (!docBase.exists()) {
			docBase = new File("target/classes");
		}

		Context context = tomcat.addContext("", docBase.getAbsolutePath());

		Tomcat.addServlet(context, "BillingServlet", new BillingServlet());
		context.addServletMappingDecoded("/api/billing/*", "BillingServlet");

		Tomcat.addServlet(context, "InventoryServlet", new InventoryServlet());
		context.addServletMappingDecoded("/api/inventory/*", "InventoryServlet");

		Tomcat.addServlet(context, "OnlineStoreServlet", new OnlineStoreServlet());
		context.addServletMappingDecoded("/api/store/*", "OnlineStoreServlet");

		Tomcat.addServlet(context, "ReportServlet", new ReportServlet());
		context.addServletMappingDecoded("/api/reports/*", "ReportServlet");

		try {
			tomcat.start();
			System.out.println("Tomcat started on port " + tomcat.getConnector().getPort());
			tomcat.getServer().await();
		} catch (Exception e) {
			e.printStackTrace();
		}

		tomcat.getServer().await();
	}
}
