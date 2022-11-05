package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto SetmealDto setmealDto
     */
    public Boolean saveWithDish(SetmealDto setmealDto);

    public Boolean removeDishById(List<Long> ids);
}
