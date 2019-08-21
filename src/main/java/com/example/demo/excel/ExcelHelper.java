package com.example.demo.excel;

import com.example.demo.excel.common.ExcelColumn;
import com.example.demo.excel.common.ExcelUtil;
import com.example.demo.excel.common.ReflectionUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mgy on 2019/8/21
 */
public class ExcelHelper {

    public static XSSFWorkbook create(List<?> beanList) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(beanList));
        Class<?> clazz = beanList.get(0).getClass();
        return create(beanList, Stream.of(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    private static void validate(Field field, ExcelColumn excelColumn) {
        String[] allowedSet = excelColumn.allowedSet();
        if (allowedSet.length != 0) {
            Class<?> type = field.getType();
            Preconditions.checkArgument(
                    type == String.class || Enum.class.isAssignableFrom(type),
                    "allowedSet只能用于String和枚举对象!");

            if (Enum.class.isAssignableFrom(type)) {
                Class<? extends Enum> enumType = (Class<? extends Enum>) type;
                for (String s : allowedSet) {
                    try {
                        Enum.valueOf(enumType, s);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("allowedSet参数非法!");
                    }
                }
            }
        }
    }

    public static XSSFWorkbook create(List<?> beanList, List<String> filedNames) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(beanList));

        Class<?> clazz = beanList.get(0).getClass();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet");
        // 获取bean中由ExcelColumn注解的列
        List<Field> columnFields = new ArrayList<>();
        for (String fieldName : filedNames) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn != null) {
                    validate(field, excelColumn);

                    String[] allowedSet = excelColumn.allowedSet();
                    if (allowedSet.length == 0 && Enum.class.isAssignableFrom(field.getType())) {
                        @SuppressWarnings("unchecked")
                        List<Enum> enumList = EnumUtils.getEnumList((Class<Enum>) field.getType());
                        allowedSet = enumList.stream().map(Enum::name).toArray(String[]::new);
                    }
                    if (allowedSet.length != 0) {
                        //限制列所允许的值
                        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
                        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(allowedSet);
                        int currentPos = columnFields.size();
                        CellRangeAddressList range = new CellRangeAddressList(1, beanList.size(), currentPos, currentPos);
                        DataValidation dataValidation = validationHelper.createValidation(constraint, range);
                        dataValidation.setSuppressDropDownArrow(true);
                        dataValidation.setShowErrorBox(true);
                        dataValidation.setShowPromptBox(true);
                        sheet.addValidationData(dataValidation);
                    }
                    columnFields.add(field);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            }
        }

        Preconditions.checkArgument(!columnFields.isEmpty());

        // 创建表头
        XSSFRow headerRow = sheet.createRow(0);
        int size = columnFields.size();
        for (int i = 0; i < size; i++) {
            XSSFCell headerCell = headerRow.createCell(i);
            ExcelColumn excelColumn = columnFields.get(i).getAnnotation(ExcelColumn.class);
            String headerStr = excelColumn.value();
            String desc = excelColumn.desc();
            if (StringUtils.isNotBlank(desc)) {
                headerStr = String.format("%s(%s)", headerStr, desc);
            }
            headerCell.setCellValue(headerStr);
        }

        // 写入数据
        int dataSize = beanList.size();
        for (int i = 0; i < dataSize; i++) {
            XSSFRow row = sheet.createRow(i + 1);
            Object bean = beanList.get(i);
            int columnCount = columnFields.size();
            for (int j = 0; j < columnCount; j++) {
                Field columnField = columnFields.get(j);
                Object value = ReflectionUtil.getFieldValue(bean, columnField);
                if (value == null) {
                    continue;
                }
                XSSFCell cell = row.createCell(j);
                ExcelColumn excelColumn = columnField.getAnnotation(ExcelColumn.class);

                // 检查值是否在允许范围
                String[] allowedSet = excelColumn.allowedSet();
                if (allowedSet.length != 0) {
                    Set<String> set = new HashSet<>(Arrays.asList(allowedSet));
                    String valueStr = value instanceof String ? (String) value : ((Enum<?>) value).name();
                    Preconditions.checkArgument(set.contains(valueStr), value + " is not allowed!");
                }

                // 转化日期格式
                if (value instanceof Date) {
                    String format = excelColumn.format();
                    CellStyle cellStyle = wb.createCellStyle();
                    CreationHelper createHelper = wb.getCreationHelper();
                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(format));
                    cell.setCellStyle(cellStyle);
                }
                ExcelUtil.setCellValue(cell, value);
            }
        }
        return wb;
    }

}
