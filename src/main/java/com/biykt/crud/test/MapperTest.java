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
 * ����dao�㹤��
 * @author BIYKT
 *�Ƽ�Spring����Ŀ����ʹ��Spring�ĵ�Ԫ���ԣ������Զ�ע��������Ҫ�����
 *1.����SpringTestģ��
 *2.@ContextConfigurationָ��Spring�����ļ���λ��
 *3.ֱ��autowiredҪʹ�õ��������
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class MapperTest {
	
	@Autowired
	DepartmentMapper departmentMapper;
	
	@Autowired
	EmploveeMapper emploveeMapper;
	
	//������sqlSession
	@Autowired
	SqlSession sqlSession;
	
	/**
	 * ����DepartmentMapper
	 */
	@Test
	public void testCRUD(){
		/*ԭ���Ĳ��Դ���
		//1.����SpringIOC����
		ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
		//2.�������л�ȡmapper
		DepartmentMapper bean = ioc.getBean(DepartmentMapper.class);
		*/
		System.out.println(departmentMapper);
		//1.���뼸������
		departmentMapper.insertSelective(new Department(null,"������"));
		departmentMapper.insertSelective(new Department(null,"���Բ�"));
		//2.����Ա�����ݣ�����Ա������
		//emploveeMapper.insertSelective(new Emplovee(null, "Jerry", "M", "Jerry@biykt.com", 1));
		//3.����������Ա����ʹ�ÿ���ִ������������sqlSession
		EmploveeMapper mapper = sqlSession.getMapper(EmploveeMapper.class);
		for (int i = 0; i < 100; i++) {
			String uid = UUID.randomUUID().toString().substring(0,5)+i;
			mapper.insertSelective(new Emplovee(null, uid, "M", uid+"@biykt.com", 1));
		}
		System.out.println("�������");
	}
}
