package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.ApiRestResponse;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.exception.ImoocMallException;
import com.itheima.reggie.exception.ImoocMallExceptionEnum;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.service.SetMealService;
import com.itheima.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Resource
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto SetmealDto
     */
    @Override
    public Boolean saveWithDish(SetmealDto setmealDto) {

        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().peek((item) -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());


        return  setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * @return boolean
     */
    @Override
    public Boolean removeDishById(List<Long> ids) {

        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new ImoocMallException(ImoocMallExceptionEnum.SETMEAL_HAVE_BEEN_USED);
        }

        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据----setmeal_dish

        return setmealDishService.remove(lambdaQueryWrapper);
    }


}
