package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.ApiRestResponse;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.exception.ImoocMallExceptionEnum;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public ApiRestResponse<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return ApiRestResponse.error(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return ApiRestResponse.error(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return ApiRestResponse.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ApiRestResponse<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return ApiRestResponse.success("退出成功");
    }

    @PostMapping
    public ApiRestResponse<List<Employee>> save(HttpServletRequest request, @RequestBody Employee employee) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp != null) {
           return ApiRestResponse.error(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        Long id = employee.getId();
        List<Employee> employeeList = employeeService.listByIds(Collections.singleton(id));
        return ApiRestResponse.success(employeeList);
    }

    @GetMapping("/page")
    public ApiRestResponse<Page<Employee>> getEmployee(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Employee> pageInfo = new Page<Employee>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        if(name != null) {
            queryWrapper.like(Employee::getName,name);
        }
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return  ApiRestResponse.success(pageInfo);
    }

    @GetMapping("/{id}")
    public ApiRestResponse<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if(employee != null) {
            return ApiRestResponse.success(employee);
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.NOT_EXIST_EMPLOYEE);
    }

    /**
     *
     * @deprecated  根据id修改员工数据
     * @param employee Employee employee
     * @return  ApiRestResponse<Employee>
     */
    @PutMapping("/updateEmployee")
    public ApiRestResponse<Employee> updateEmployeeById(HttpServletRequest request, @RequestBody Employee employee) {
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        employeeService.updateById(employee);
        return ApiRestResponse.success();
    }
}
