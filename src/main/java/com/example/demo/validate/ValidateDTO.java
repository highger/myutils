package com.example.demo.validate;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by mgy on 2019/9/14
 */
@Data
public class ValidateDTO {

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 年龄
     */
    @NotNull
    private Integer age;

    /**
     * 电话
     */
    @NotNull
    private String phone;
}
