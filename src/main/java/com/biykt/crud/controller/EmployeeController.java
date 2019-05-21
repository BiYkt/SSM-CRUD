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
 * 处理员工CRUD请求
 * @author BIYKT
 *
 */
@Controller
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	/**
	 * 删除的方法
	 * 批量删除：id用横杠分割
	 * 单个删除：直接带id
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg deleteEmp(@PathVariable("ids")String ids) {
		//批量删除
		if(ids.contains("-")){
			List<Integer> del_ids = new ArrayList<Integer>();
			String[] str_ids = ids.split("-");
			//组装id的集合
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		}else {
			//单个删除
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		
		return Msg.success();
	}
	
	/**
	 * 如果直接发送ajax=put的请求，会发生请求体中有数据，
	 * 但是employee对象封装不上的问题
	 * 
	 * 
	 * 原因：
	 * Tomcat是将请求体中的数据封装一个map
	 * request.getParameter("empName")就会从这个map中取值
	 * SpringMVC封装POJO对象的时候会把POJO中每个属性的值调用request.getParameter方法拿到
	 * 
	 * AJAX不能直接发送PUT请求，无法用request.getParameter取得值
	 * Tomcat一看是PUT请求，不会封装请求体中的数据为map，只有POST请求才封装请求体为map
	 * 
	 * 解决方法：在web.xml中配置过滤器
	 * 
	 * 员工更新方法
	 * @param emplovee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Emplovee emplovee,HttpServletRequest request) {
		
		System.out.println("请求体中的值"+request.getParameter("gender"));
		System.out.println("将要更新的员工数据"+emplovee);
		employeeService.updateEmp(emplovee);
		return Msg.success();
	}
	
	/**
	 * 根据id查询员工
	 * PathVariable为指定传入字段为路径中的id的值
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
	 * 检查用户名是否可用
	 * matches方法是检验正则表达式的方法
	 * @param empName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuser(@RequestParam("empName")String empName){
		//先判断用户名是否是合法的表达式
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(regx)){
			return Msg.fail().add("va_msg", "用户名必须是6-16位数字和字母的组合或者2-5位中文");		
		}
		
		//数据库用户名重复校验
		boolean b = employeeService.checkUser(empName);
		if(b){
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "用户名不可用");
		}
	}
	
	/**
	 * 员工保存
	 * 1.支持JSR303校验需要导入Hibernate-Validator包
	 * 
	 * @param emplovee
	 * @return
	 */
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Emplovee emplovee,BindingResult result) {
		if(result.hasErrors()){
			//校验失败，应该返回失败，在模态框中显示校验失败的错误信息
			Map<String, Object> map = new HashMap<String, Object>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("错误的字段名：" + fieldError.getField());
				System.out.println("错误的信息：" + fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(emplovee);
			return Msg.success();
		}
		
	}
	/**
	 * 使用Json，页面中用js解析json字符串
	 * “@ResponseBody”正常工作需要导入jackson包
	 * @param pn
	 * @return
	 */
	
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue="1")Integer pn){
		//引入PageHelper分页插件
		//在查询之前只需要调用,传入页码，以及分页每页的大小
		PageHelper.startPage(pn, 5);
		//startPage后面紧跟的这个查询就是一个分页查询
		List<Emplovee> emps = employeeService.getAll();
		//使用pageInfo包装查询后的结果,只需要将pageInfo交给页面即可
		//封装了详细的分页信息，包括有查询出来的数据,可以传入连续显示的页数
		PageInfo page = new PageInfo(emps,5);
		return Msg.success().add("pageInfo",page);
	}
	
	/**
	 * 查询员工数据（分页查询）
	 * 不使用Json，直接返回页面
	 * @return
	 */
	//@RequestMapping("/emps")
	//@RequestParam注解将请求参数绑定至方法参数
	public String getEmps(@RequestParam(value="pn",defaultValue="1")Integer pn,
			Model model){
		//引入PageHelper分页插件
		//在查询之前只需要调用,传入页码，以及分页每页的大小
		PageHelper.startPage(pn, 5);
		//startPage后面紧跟的这个查询就是一个分页查询
		List<Emplovee> emps = employeeService.getAll();
		//使用pageInfo包装查询后的结果,只需要将pageInfo交给页面即可
		//封装了详细的分页信息，包括有查询出来的数据,可以传入连续显示的页数
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo",page);
		return "list";
	}
}
