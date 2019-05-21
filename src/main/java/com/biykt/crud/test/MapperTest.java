package com.biykt.crud.test;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.biykt.crud.bean.Department;
import com.biykt.crud.bean.Emplovee;
import com.biykt.crud.dao.DepartmentMapper;
import com.biykt.crud.dao.EmploveeMapper;

/**
 * 测试dao层工作
 * @author BIYKT
 *推荐Spring的项目可以使用Spring的单元测试，可以自动注入我们需要的组件
 *1.导入SpringTest模块
 *2.@ContextConfiguration指定Spring配置文件的位置
 *3.直接autowired要使用的组件即可
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class MapperTest {
	
	@Autowired
	DepartmentMapper departmentMapper;
	
	@Autowired
	EmploveeMapper emploveeMapper;
	
	//批量的sqlSession
	@Autowired
	SqlSession sqlSession;
	
	/**
	 * 测试DepartmentMapper
	 */
	@Test
	public void testCRUD(){
		/*原生的测试代码
		//1.创建SpringIOC容器
		ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
		//2.从容器中获取mapper
		DepartmentMapper bean = ioc.getBean(DepartmentMapper.class);
		*/
		System.out.println(departmentMapper);
		//1.插入几个部门
		departmentMapper.insertSelective(new Department(null,"开发部"));
		departmentMapper.insertSelective(new Department(null,"测试部"));
		//2.生成员工数据，测试员工插入
		//emploveeMapper.insertSelective(new Emplovee(null, "Jerry", "M", "Jerry@biykt.com", 1));
		//3.批量插入多个员工：使用可以执行批量操作的sqlSession
		EmploveeMapper mapper = sqlSession.getMapper(EmploveeMapper.class);
		for (int i = 0; i < 100; i++) {
			String uid = UUID.randomUUID().toString().substring(0,5)+i;
			mapper.insertSelective(new Emplovee(null, uid, "M", uid+"@biykt.com", 1));
		}
		System.out.println("批量完成");
	}
}
