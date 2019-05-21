package com.biykt.crud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biykt.crud.bean.Department;
import com.biykt.crud.dao.DepartmentMapper;

@Service
public class DepartmentServive {
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	public List<Department> getDepts() {
		List<Department> list = departmentMapper.selectByExample(null);
		// TODO Auto-generated method stub
		return list;
	}

}
