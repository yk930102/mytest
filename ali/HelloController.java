package com.zt.security.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.zt.anno.Permission;

import java.util.Date;

@RestController
@RequestMapping("/hello.do")
public class HelloController {
	@Permission("a")
	@RequestMapping("/hello1.do")
	public Object hello1() {
		System.out.println("hello1");
		return "hello1";
	}
	
	@Permission("KnowledgeBaseAdd")
	@RequestMapping("/hello2.do")
	public Object hello2(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("------hello2");
		return "hello2";
	}
	
	@Permission("a")
	@RequestMapping("/hello3.do")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Object hello3(HttpServletRequest request) {
		System.out.println("hello3");
		return new Date();
	}

    @Permission(value = "eqadd")
    @PostMapping
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public void hello4() {
        System.out.println("hello3");
    }

    @Permission(value = "eqadd",orgKey = "orgId")
    @GetMapping
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public void hello5() {
        System.out.println("hello4");
    }
}
