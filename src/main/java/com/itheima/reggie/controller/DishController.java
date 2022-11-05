package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.ApiRestResponse;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    @PostMapping
    public ApiRestResponse<Boolean> insertDish(@RequestBody DishDto dishDto) {

        Boolean saveWithFlavor = dishService.saveWithFlavor(dishDto);

        return ApiRestResponse.success(saveWithFlavor);

    }

    /**
     * 菜品信息分页查询
     *
     * @param page     int page
     * @param pageSize int pageSize
     * @param name     String name
     * @return piRestResponse<Page>
     */
    @GetMapping("/page")
    public ApiRestResponse<Page<DishDto>> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return ApiRestResponse.success(dishDtoPage);
    }

    /**
     * 根据id查询菜单
     *
     * @param id Long id
     * @return ApiRestResponse<DishDto>
     */
    @GetMapping("/{id}")
    public ApiRestResponse<DishDto> getDishById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return ApiRestResponse.success(dishDto);
    }

    @PutMapping
    public ApiRestResponse<Boolean> updateDish(@RequestBody DishDto dishDto) {

        Boolean update = dishService.updateWithFlavor(dishDto);

        return ApiRestResponse.success(update);
    }
}
