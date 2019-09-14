package com.example.demo.validate;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
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
    @Min(0)
    private Integer age;

    /**
     * 电话
     */
    @NotNull
    @Length(max = 11)
    private String phone;
}
