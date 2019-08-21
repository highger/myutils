package com.example.demo.excel.entity;

import com.example.demo.excel.common.ExcelColumn;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mgy on 2019/8/21
 */
@Data
public class ExportPOJO {

    @ExcelColumn(value = "订单号")
    private String orderNo;

    /**
     * 期次
     */
    @ExcelColumn(value = "期次")
    private Integer period;

    /**
     * 日期
     */
    @ExcelColumn(value = "日期", format = "yyyy/MM/dd")
    private Date actualDate;

    /**
     * 金额
     */
    @ExcelColumn(value = "金额")
    private BigDecimal actualAmt;


}
