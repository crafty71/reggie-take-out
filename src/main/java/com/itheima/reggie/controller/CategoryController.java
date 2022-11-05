package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.ApiRestResponse;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.exception.ImoocMallExceptionEnum;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @Resource
    DishService dishService;

    @Resource
    SetMealService setMealService;

    @GetMapping("/test")
    public ApiRestResponse<List<Category>> getCategoryList() {
        return ApiRestResponse.success();
    }

    /**
     * @param category Category category
     * @return ApiRestResponse<Boolean>
     * @deprecated 新增分类
     */
    @PostMapping
    public ApiRestResponse<Boolean> insertCategory(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        return ApiRestResponse.success(save);
    }

    @GetMapping("/page")
    public ApiRestResponse<Page<Category>> getCategory(int page, int pageSize) {

        Page<Category> categoryPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        Page<Category> categoryList = categoryService.page(categoryPage, queryWrapper);

        return ApiRestResponse.success(categoryList);

    }

    /**
     * @param id Long Id
     * @return ApiRestResponse<Boolean>
     * @deprecated 根据id 删除分类
     */
    @DeleteMapping
    public ApiRestResponse<Boolean> deleteCategory(Long id) {
        boolean remove = categoryService.removeById(id);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count();
        if (dishCount > 0) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NOT_ALLOW_DELETE_WITH_DISH);
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setMealCount = setMealService.count();
        if (setMealCount > 0) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NOT_ALLOW_DELETE_WITH_SETMEAL);
        }
        return ApiRestResponse.success(remove);
    }

    /**
     * @param category Category category
     * @return ApiRestResponse<Boolean>
     * @deprecated 修改分类数据
     */
    @PostMapping("/update")
    public ApiRestResponse<Boolean> updateCategory(@RequestBody Category category) {
        boolean update = categoryService.updateById(category);
        return ApiRestResponse.success(update);
    }

    /**
     *  根据条件type查询
     * @param category Category category
     * @return ApiRestResponse<List<Category>>
     */
    @GetMapping("/list")
    public ApiRestResponse<List<Category>> getCategoryList(Category category) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);

        return ApiRestResponse.success(categoryList);
    }
}
