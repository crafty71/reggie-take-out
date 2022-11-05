package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.ApiRestResponse;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealService;
import com.itheima.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/meal")
@Slf4j
public class SetMealController {

    @Resource
    private SetMealService setMealService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishService setmealDishService;

    @PostMapping
    public ApiRestResponse<Boolean> insertDish(@RequestBody SetmealDto setmealDto) {

        Boolean saveWithDish = setMealService.saveWithDish(setmealDto);
        return ApiRestResponse.success(saveWithDish);
    }

    /**
     * 套餐分页查询
     * @param page int page
     * @param pageSize int pageSize
     * @param name String name
     * @return ApiRestResponse<Page<SetmealDto>>
     */
    @GetMapping("/page")
    public ApiRestResponse<Page<SetmealDto>> getDishByPage(int page,int pageSize,String name){
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setMealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return ApiRestResponse.success(dtoPage);
    }

    @DeleteMapping
    public ApiRestResponse<Boolean> deleteMealWithIds(List<Long> ids) {
        Boolean dishById = setMealService.removeDishById(ids);

        return ApiRestResponse.success(dishById);

    }
}
