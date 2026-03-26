///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package korisni;
//
//import jakarta.servlet.ServletContextEvent;
//import jakarta.servlet.ServletContextListener;
//import jakarta.servlet.annotation.WebListener;
//import java.util.logging.*;
//import java.io.IOException;
//
//@WebListener
//public class LogConfigListener implements ServletContextListener {
//
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        try {
//            // 1. Dohvatanje root loggera (utječe na sve klase, pa i na NetBeans kod)
//            Logger rootLogger = Logger.getLogger("");
//
//            // 2. Kreiranje FileHandler-a
//            // "aplikacija.log" će biti u bin folderu servera (npr. tomcat/bin)
//            // true znači da dopisuje na kraj fajla (append)
//            FileHandler fh = new FileHandler(webUtil.vratiPathDB() + "aplikacija.log", true);
//            
//            // 3. Postavljanje formata (inače će pisati u čudnom XML formatu)
//            fh.setFormatter(new SimpleFormatter());
//
//            // 4. Dodavanje handlera
//            rootLogger.addHandler(fh);
//            
//        } catch (IOException e) {
//            sce.getServletContext().log("Ne mogu inicijalizovati log fajl", e);
//            webUtil.errPoruka("Ne mogu inicijalizovati log fajl " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//        // Ovdje po potrebi možeš zatvoriti resurse
//    }
//}
//
