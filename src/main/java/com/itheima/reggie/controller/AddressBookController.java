package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.ApiRestResponse;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.exception.ImoocMallExceptionEnum;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @param addressBook AddressBook addressBook
     * @return ApiRestResponse<AddressBook>
     */
    @PostMapping
    public ApiRestResponse<AddressBook> insertBook(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return ApiRestResponse.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook AddressBook addressBook
     * @return ApiRestResponse<AddressBook>
     */
    @PutMapping
    public ApiRestResponse<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return ApiRestResponse.success(addressBook);
    }

    /**
     * 获取用户默认地址
     *
     * @param id Long id
     * @return ApiRestResponse<AddressBook>
     */
    @GetMapping("/{id}")
    public ApiRestResponse<AddressBook> getAddressById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return ApiRestResponse.success(addressBook);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.ADDRESS_NOT_EXIST);
        }
    }

    /**
     * 获取用户默认地址
     *
     * @return ApiRestResponse<AddressBook>
     */
    @GetMapping("/default")
    public ApiRestResponse<AddressBook> getDefaultAddress() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());

        queryWrapper.eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook != null) {
            return ApiRestResponse.success(addressBook);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.ADDRESS_NOT_EXIST);
        }

    }

    /**
     *  获取用户全部地址
     * @param addressBook AddressBook addressBook
     * @return ApiRestResponse<List<AddressBook>>
     */
    @GetMapping("/list")
    public ApiRestResponse<List<AddressBook>> getAddressList(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());

        queryWrapper.orderByAsc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);

        return ApiRestResponse.success(list);
    }

}
