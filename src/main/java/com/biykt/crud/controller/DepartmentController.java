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
 * ����Ͳ����йص�����
 * @author BIYKT
 *
 */
@Controller
public class DepartmentController {
	@Autowired
	private DepartmentServive departmentService;
	
	/**
	 * �������в�����Ϣ
	 */
	@RequestMapping("/depts")
	@ResponseBody
	public Msg getDepts(){
		//������в�����Ϣ
		List<Department> list = departmentService.getDepts();
		return Msg.success().add("depts", list);
	}
}
