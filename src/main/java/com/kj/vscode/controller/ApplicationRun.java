package com.kj.vscode.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ServletComponentScan
@ImportResource("classpath:context-root.xml")
public class ApplicationRun {
	

		private static Logger log = LoggerFactory.getLogger(ApplicationRun.class);
	  
		public static void main(String[] args) {
			SpringApplication.run(ApplicationRun.class, args);
			System.out.println("============================启动成功========================");
		}

}
