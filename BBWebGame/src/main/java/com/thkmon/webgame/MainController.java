package com.thkmon.webgame;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {


	// 문법 : @RequestMapping(value = "{변수명:정규식}")
	@RequestMapping(value = "/")
	public String index() {
		return "index.jsp";
	}
	
	
	@RequestMapping(value = "/{path1:[a-zA-Z]*}/{path2:[a-zA-Z]*}")
	public String handleLowerCase(@PathVariable String path1, @PathVariable String path2) {
		return path1 + "/" + path2 + ".jsp";
	}
	
	
	/*
	@RequestMapping(value = "/{path:[a-z]*}")
	public String handleLowerCase(@PathVariable String path) {
		System.out.println("소문자");
		return "index.jsp";
	}
	
	
	@RequestMapping(value = "/{path:[A-Z]*}")
	public String handleUpperCase(@PathVariable String path) {
		System.out.println("대문자");
		return "index.jsp";
	}
	
	
	@RequestMapping(value = "/{path:[0-9]*}")
	public String handleNumber(@PathVariable String path) {
		System.out.println("숫자");
		return "index.jsp";
	}
	*/
}