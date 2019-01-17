package com.ssmcrud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssmcrud.bean.Employee;
import com.ssmcrud.bean.Msg;
import com.ssmcrud.service.EmployeeService;

@Controller
public class EmployeeController {
	@Autowired
	EmployeeService employeeService;

	// @RequestMapping("/emps")
	// public String getEmps(@RequestParam(value = "pn", defaultValue = "1")
	// Integer pn, Model model) {
	// // 引入pagehelper
	// // PageHelper.startPage(pageNum, pageSize)
	// PageHelper.startPage(pn, 5);
	//
	// List<Employee> emps = employeeService.getAll();
	// // 5代表连续显示的页数
	// PageInfo page = new PageInfo(emps, 5);
	// model.addAttribute("pageInfo", page);
	// return "list";
	// }

	// @RequestMapping(value = "/emps", method = RequestMethod.GET)
	// @ResponseBody
	// public PageInfo getEmpsWithJson(@RequestParam(value = "pn", defaultValue
	// = "1") Integer pn) {
	// PageHelper.startPage(pn, 5);
	// List<Employee> emps = employeeService.getAll();
	// PageInfo pageInfo = new PageInfo(emps, 5);
	// return pageInfo;
	//
	// }

	@RequestMapping("/index")
	public String getEmps() {
		return "index";
	}

	@RequestMapping(value = "/emps", method = RequestMethod.GET)
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
		PageHelper.startPage(pn, 5);
		List<Employee> emps = employeeService.getAll();
		PageInfo pageInfo = new PageInfo(emps, 5);
		return Msg.success().add("pageInfo", pageInfo);

	}

	@RequestMapping(value = "/emp", method = RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee, BindingResult result) {
		if (result.hasErrors()) {
			Map<String, Object> map = new HashMap<>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		} else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
	}

	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkUser(@RequestParam("empName") String empName) {

		String regex = "(^[A-Za-z0-9]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5}$)";
		if (!empName.matches(regex)) {
			return Msg.fail().add("va_msg", "名字必须是2-5个中文或者6-16位英文数字组合");
		}

		boolean b = employeeService.checkUser(empName);
		if (b) {
			return Msg.success();
		} else {
			return Msg.fail().add("va_msg", "用户名被占用");
		}
	}

	/**
	 * 因为rest风格，并不是型参传的id， 而是路径里面的数字，所以要@PathVariable("id")
	 */
	@RequestMapping(value = "/emp/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id") Integer id) {
		Employee employee = employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}

	/**
	 * 注意：不能将{empId}简写为{id}，否则更新不上
	 * 
	 * @param Employee
	 * @return Msg
	 */
	@RequestMapping(value = "/emp/{empId}", method = RequestMethod.PUT)
	@ResponseBody
	public Msg saveEmp(Employee employee) {
		System.out.println("Employee:" + employee);
		employeeService.updateEmp(employee);
		return Msg.success();
	}

	@RequestMapping(value = "/emp/{ids}", method = RequestMethod.DELETE)
	@ResponseBody
	public Msg deleteEmp(@PathVariable("ids") String ids) {
		if (ids.contains("-")) {
			List<Integer> del_ids = new ArrayList<>();
			String[] str_ids = ids.split("-");
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		} else {
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
	}

	// @RequestMapping(value = "/emp/{id}", method = RequestMethod.DELETE)
	// @ResponseBody
	// public Msg deleteEmpById(@PathVariable("id") Integer id) {
	// employeeService.deleteEmp(id);
	// return Msg.success();
	// }

}
