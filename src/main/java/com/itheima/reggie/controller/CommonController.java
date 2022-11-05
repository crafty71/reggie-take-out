package com.itheima.reggie.controller;

import com.itheima.reggie.common.ApiRestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/common")
@Slf4j
@RestController
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public ApiRestResponse<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffer = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffer;

        File dir = new File(basePath);

        if(!dir.exists()) {
            dir.mkdir();
        }
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ApiRestResponse.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;

            byte[] bytes = new byte[1024];

            while((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0 , len);
                outputStream.flush();
            }

            outputStream.close();

            fileInputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
