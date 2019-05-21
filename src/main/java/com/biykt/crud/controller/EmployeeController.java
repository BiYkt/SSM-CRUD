package com.biykt.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biykt.crud.bean.Emplovee;
import com.biykt.crud.bean.Msg;
import com.biykt.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * ����Ա��CRUD����
 * @author BIYKT
 *
 */
@Controller
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	/**
	 * ɾ���ķ���
	 * ����ɾ����id�ú�ָܷ�
	 * ����ɾ����ֱ�Ӵ�id
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg deleteEmp(@PathVariable("ids")String ids) {
		//����ɾ��
		if(ids.contains("-")){
			List<Integer> del_ids = new ArrayList<Integer>();
			String[] str_ids = ids.split("-");
			//��װid�ļ���
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		}else {
			//����ɾ��
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		
		return Msg.success();
	}
	
	/**
	 * ���ֱ�ӷ���ajax=put�����󣬻ᷢ���������������ݣ�
	 * ����employee�����װ���ϵ�����
	 * 
	 * 
	 * ԭ��
	 * Tomcat�ǽ��������е����ݷ�װһ��map
	 * request.getParameter("empName")�ͻ�����map��ȡֵ
	 * SpringMVC��װPOJO�����ʱ����POJO��ÿ�����Ե�ֵ����request.getParameter�����õ�
	 * 
	 * AJAX����ֱ�ӷ���PUT�����޷���request.getParameterȡ��ֵ
	 * Tomcatһ����PUT���󣬲����װ�������е�����Ϊmap��ֻ��POST����ŷ�װ������Ϊmap
	 * 
	 * �����������web.xml�����ù�����
	 * 
	 * Ա�����·���
	 * @param emplovee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Emplovee emplovee,HttpServletRequest request) {
		
		System.out.println("�������е�ֵ"+request.getParameter("gender"));
		System.out.println("��Ҫ���µ�Ա������"+emplovee);
		employeeService.updateEmp(emplovee);
		return Msg.success();
	}
	
	/**
	 * ����id��ѯԱ��
	 * PathVariableΪָ�������ֶ�Ϊ·���е�id��ֵ
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		
		Emplovee emplovee = employeeService.getEmp(id);
		return Msg.success().add("emp", emplovee);
	}
	
	/**
	 * ����û����Ƿ����
	 * matches�����Ǽ���������ʽ�ķ���
	 * @param empName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuser(@RequestParam("empName")String empName){
		//���ж��û����Ƿ��ǺϷ��ı��ʽ
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(regx)){
			return Msg.fail().add("va_msg", "�û���������6-16λ���ֺ���ĸ����ϻ���2-5λ����");		
		}
		
		//���ݿ��û����ظ�У��
		boolean b = employeeService.checkUser(empName);
		if(b){
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "�û���������");
		}
	}
	
	/**
	 * Ա������
	 * 1.֧��JSR303У����Ҫ����Hibernate-Validator��
	 * 
	 * @param emplovee
	 * @return
	 */
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Emplovee emplovee,BindingResult result) {
		if(result.hasErrors()){
			//У��ʧ�ܣ�Ӧ�÷���ʧ�ܣ���ģ̬������ʾУ��ʧ�ܵĴ�����Ϣ
			Map<String, Object> map = new HashMap<String, Object>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("������ֶ�����" + fieldError.getField());
				System.out.println("�������Ϣ��" + fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(emplovee);
			return Msg.success();
		}
		
	}
	/**
	 * ʹ��Json��ҳ������js����json�ַ���
	 * ��@ResponseBody������������Ҫ����jackson��
	 * @param pn
	 * @return
	 */
	
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue="1")Integer pn){
		//����PageHelper��ҳ���
		//�ڲ�ѯ֮ǰֻ��Ҫ����,����ҳ�룬�Լ���ҳÿҳ�Ĵ�С
		PageHelper.startPage(pn, 5);
		//startPage��������������ѯ����һ����ҳ��ѯ
		List<Emplovee> emps = employeeService.getAll();
		//ʹ��pageInfo��װ��ѯ��Ľ��,ֻ��Ҫ��pageInfo����ҳ�漴��
		//��װ����ϸ�ķ�ҳ��Ϣ�������в�ѯ����������,���Դ���������ʾ��ҳ��
		PageInfo page = new PageInfo(emps,5);
		return Msg.success().add("pageInfo",page);
	}
	
	/**
	 * ��ѯԱ�����ݣ���ҳ��ѯ��
	 * ��ʹ��Json��ֱ�ӷ���ҳ��
	 * @return
	 */
	//@RequestMapping("/emps")
	//@RequestParamע�⽫�������������������
	public String getEmps(@RequestParam(value="pn",defaultValue="1")Integer pn,
			Model model){
		//����PageHelper��ҳ���
		//�ڲ�ѯ֮ǰֻ��Ҫ����,����ҳ�룬�Լ���ҳÿҳ�Ĵ�С
		PageHelper.startPage(pn, 5);
		//startPage��������������ѯ����һ����ҳ��ѯ
		List<Emplovee> emps = employeeService.getAll();
		//ʹ��pageInfo��װ��ѯ��Ľ��,ֻ��Ҫ��pageInfo����ҳ�漴��
		//��װ����ϸ�ķ�ҳ��Ϣ�������в�ѯ����������,���Դ���������ʾ��ҳ��
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo",page);
		return "list";
	}
}
