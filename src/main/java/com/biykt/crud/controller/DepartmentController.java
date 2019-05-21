package com.biykt.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biykt.crud.bean.Department;
import com.biykt.crud.bean.Msg;
import com.biykt.crud.service.DepartmentServive;

/**
 * 处理和部门有关的请求
 * @author BIYKT
 *
 */
@Controller
public class DepartmentController {
	@Autowired
	private DepartmentServive departmentService;
	
	/**
	 * 返回所有部门信息
	 */
	@RequestMapping("/depts")
	@ResponseBody
	public Msg getDepts(){
		//查出所有部门信息
		List<Department> list = departmentService.getDepts();
		return Msg.success().add("depts", list);
	}
}
