package com.example.demo.validate;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Created by mgy on 2019/9/14
 */
@Slf4j
public class ValidateTest {

    public static void main(String[] args) {

        ValidateDTO validateDTO = new ValidateDTO();
        Set<ConstraintViolation<ValidateDTO>> validate = ValidateUtils.validate(validateDTO);

        if (!validate.isEmpty()) {
            String errInfo = ValidateUtils.buildErrorMsgWithTips(validate);
            log.error(errInfo);
        }

    }
}
