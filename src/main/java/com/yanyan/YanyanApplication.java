package com.yanyan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanyan.mapper")
public class YanyanApplication {

	public static void main(String[] args) {
		setProxy();//设置代理
		SpringApplication.run(YanyanApplication.class, args);
	}


	private static void setProxy() {
		// 设置代理，这里可以网上找一些免费代理，或者收费代理
		String proxy = "127.0.0.1";  // 100.100.101.235 8811  示例，里面填具体的代理ip
		int port = 7890;   //设置翻墙软件代理的端口，
		System.setProperty("proxyType", "4");
		System.setProperty("proxyPort", Integer.toString(port));
		System.setProperty("proxyHost", proxy);
		System.setProperty("proxySet", "true");
	}

}
