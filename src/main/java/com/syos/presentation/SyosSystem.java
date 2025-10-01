package com.syos.presentation;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class SyosSystem {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        // Create a temporary directory for Tomcat
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File docBase = new File("src/main/webapp");
        if (!docBase.exists()) {
            docBase = new File("target/classes");
        }

        Context context = tomcat.addContext("", docBase.getAbsolutePath());

        // Add servlets programmatically
        Tomcat.addServlet(context, "BillingServlet", new BillingServlet());
        context.addServletMappingDecoded("/api/billing/*", "BillingServlet");

        Tomcat.addServlet(context, "InventoryServlet", new InventoryServlet());
        context.addServletMappingDecoded("/api/inventory/*", "InventoryServlet");

        Tomcat.addServlet(context, "OnlineStoreServlet", new OnlineStoreServlet());
        context.addServletMappingDecoded("/api/store/*", "OnlineStoreServlet");

        Tomcat.addServlet(context, "ReportServlet", new ReportServlet());
        context.addServletMappingDecoded("/api/reports/*", "ReportServlet");

        System.out.println("Starting SYOS System on http://localhost:8080");
        tomcat.start();
        tomcat.getServer().await();
    }
}
