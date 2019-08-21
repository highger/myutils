package com.example.demo.excel.common;

import com.example.demo.DateConverter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * Created by mgy on 2019/8/21
 */
public class ExcelUtil {

    public static void setCellValue(Cell cell, Object value) {
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue(DateConverter.toDate((LocalDate) value));
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(DateConverter.toDate((LocalDateTime) value));
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value != null) {
            cell.setCellValue(value.toString());
        }
    }

    @SuppressWarnings("unused")
    public static int columnNameToNumber(String name) {
        int number = 0;
        for (int i = 0; i < name.length(); i++) {
            number = number * 26 + (name.charAt(i) - ('A' - 1));
        }
        return number;
    }

    private static String columnNumberToName(int number) {
        StringBuilder sb = new StringBuilder();
        while (number-- > 0) {
            sb.append((char) ('A' + (number % 26)));
            number /= 26;
        }
        return sb.reverse().toString();
    }


    public static <T> List<T> read(InputStream in,
                                   Class<T> clazz,
                                   boolean strictMode,
                                   boolean shouldTrimTitle,
                                   Validator<T> validator) throws IOException, ParseException {
        checkClass(clazz);
        Map<String, Pair<Field, ExcelColumn>> beanTitleToFieldMap = getTitleFieldMap(clazz);
        if (beanTitleToFieldMap.isEmpty()) {
            throw new IllegalArgumentException(clazz.getName() + "没有ExcelColumn!");
        }

        XSSFWorkbook workbook = new XSSFWorkbook(in);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new ParseException("表格不能为空!");
        }
        List<String> titleList = getTitleList(sheet, shouldTrimTitle);
        List<T> results = new ArrayList<>(titleList.size());
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                throw new ParseException("无效的行! 行不能为空! rowIndex=" + (i + 1));
            }
            T obj = newInstance(clazz);
            boolean emptyObj = true;
            for (int j = 0; j < titleList.size(); j++) {
                String title = titleList.get(j);
                Pair<Field, ExcelColumn> fieldAndColumn = beanTitleToFieldMap.get(title);
                if (fieldAndColumn == null) {
                    continue;
                }
                Cell cell = row.getCell(j);
                if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
                    continue;
                }
                Field field = fieldAndColumn.getLeft();
                Object value = null;
                try {
                    value = convertValue(cell, field.getType(), fieldAndColumn.getRight());
                } catch (Exception e) {
                    if (strictMode) {
                        String msg = String.format("参数非法! reason=%s, rowIndex=%d, columnIndex=%s",
                                e.getMessage(), i + 1, columnNumberToName(j + 1));
                        throw new ParseException(msg, e);
                    }
                }
                try {
                    BeanUtils.setProperty(obj, field.getName(), value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                emptyObj = false;
            }
            Optional<String> result = validator.validate(obj);
            if (result.isPresent()) {
                throw new ParseException(String.format("参数非法! 请检查订单参数的有效性! rowIndex=%d, %s", i + 1, result.get()));
            }
            if (!emptyObj) {
                results.add(obj);
            }
        }
        return results;
    }

    /**
     * Java泛型
     */
    @SuppressWarnings("unchecked")
    private static Object convertValue(Cell cell, Class<?> targetType, ExcelColumn column) {
        CellType cellType = cell.getCellTypeEnum();

        //不支持Number转String,excel总喜欢将数字字符串转化为科学表示法,并且内部表示法是double,转为String会不一样
        if (cellType == CellType.NUMERIC) {
            if (targetType == Integer.class || targetType == int.class) {
                return getNumericCellValue(cell).intValue();
            } else if (targetType == Short.class || targetType == short.class) {
                return getNumericCellValue(cell).shortValue();
            } else if (targetType == Long.class || targetType == long.class) {
                return getNumericCellValue(cell).longValue();
            } else if (targetType == Float.class || targetType == float.class) {
                return getNumericCellValue(cell).floatValue();
            } else if (targetType == Double.class || targetType == double.class) {
                return getNumericCellValue(cell);
            } else if (BigDecimal.class.isAssignableFrom(targetType)) {
                // 可能抛异常
                return BigDecimal.valueOf(getNumericCellValue(cell));
            } else if (Date.class.isAssignableFrom(targetType)) {
                if (!DateUtil.isCellDateFormatted(cell)) {
                    throw new RuntimeException("无效的日期格式!");
                }
                // 可能抛异常
                return cell.getDateCellValue();
            } else if (LocalDate.class.isAssignableFrom(targetType)) {
                if (!DateUtil.isCellDateFormatted(cell)) {
                    throw new RuntimeException("无效的日期格式!");
                }
                // 可能抛异常
                return DateConverter.toLocalDate(cell.getDateCellValue());
            } else if (LocalDateTime.class.isAssignableFrom(targetType)) {
                if (!DateUtil.isCellDateFormatted(cell)) {
                    throw new RuntimeException("无效的日期格式!");
                }
                // 可能抛异常
                return DateConverter.toLocalDateTime(cell.getDateCellValue());
            } else if (targetType == String.class) {
                throw new RuntimeException("不允许数字类型转化为字符串!");
            }
        } else if (cellType == CellType.BOOLEAN) {
            if (targetType == Boolean.class) {
                return cell.getBooleanCellValue();
            }
            //String 转 number 是否允许?暂不允许
        } else if (cellType == CellType.STRING) {
            String stringCellValue = cell.getStringCellValue().trim();

            String[] allowedSet = column.allowedSet();
            if (allowedSet.length != 0) {
                Set<String> set = new HashSet<>(Arrays.asList(allowedSet));
                Preconditions.checkArgument(set.contains(stringCellValue.trim()));
            }

            if (targetType == String.class) {
                return stringCellValue;
            } else if (Date.class.isAssignableFrom(targetType)) {
                String format = column.format();
                DateFormat dateFormat = new SimpleDateFormat(format);
                try {
                    return dateFormat.parse(stringCellValue);
                } catch (java.text.ParseException e) {
                    throw new RuntimeException("无法转化为日期!", e);
                }
            } else if (LocalDate.class.isAssignableFrom(targetType)) {
                String format = column.format();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.get(format);
                try {
                    return LocalDate.parse(stringCellValue, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("无法转化为日期", e);
                }
            } else if (LocalDateTime.class.isAssignableFrom(targetType)) {
                String format = column.format();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.get(format);
                try {
                    return LocalDateTime.parse(stringCellValue, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("无法转化为日期", e);
                }
            } else if (Enum.class.isAssignableFrom(targetType)) {
                Class<? extends Enum> enumTargetType = (Class<? extends Enum>) targetType;
                // 可能抛异常
                return Enum.valueOf(enumTargetType, stringCellValue);
            }
        } else if (cellType == CellType.BLANK) {
            return null;
        }
        throw new RuntimeException("不支持的类型转化! excel类型=" + cellType.name() + ", 目标类型=" + targetType);
    }

    private static Double getNumericCellValue(Cell cell) {
        return cell.getNumericCellValue();
    }


    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(clazz.getName() + "不可实例化!", e);
        }
    }

    private static <T> void checkClass(Class<T> clazz) {
        newInstance(clazz);
    }

    private static Map<String, Pair<Field, ExcelColumn>> getTitleFieldMap(Class<?> clazz) {
        Map<String, Pair<Field, ExcelColumn>> titleFiledMap = new HashMap<>();
        Stream<Field> fieldStream = Arrays.stream(clazz.getDeclaredFields());
        Class<?> currentClass = clazz;
        //嵌套类
        while (currentClass.isAnnotationPresent(ExcelBean.class)
                && (currentClass = clazz.getSuperclass()) != Object.class) {
            fieldStream = Stream.concat(fieldStream, Stream.of(currentClass.getDeclaredFields()));
        }
        fieldStream
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .forEach(field -> {
                    ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                    titleFiledMap.put(excelColumn.value(), Pair.of(field, excelColumn));
                });
        return titleFiledMap;
    }

    /**
     * 数字会被解析成String,excel中的数字可能是数字string
     */
    public static List<Map<String, Object>> readExcelWithTitle(InputStream in) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new ParseException("表格不能为空!");
        }
        List<String> titleList = getTitleList(sheet, false);
        List<Map<String, Object>> data = new ArrayList<>(sheet.getPhysicalNumberOfRows());
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                throw new ParseException("无效的行!行不能为空!rowIndex=" + (i + 1));
            }
            Map<String, Object> rowMap = new HashMap<>(titleList.size());
            data.add(rowMap);
            for (int j = 0; j < titleList.size(); j++) {
                String title = titleList.get(j);
                Object value = null;
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = cell.getDateCellValue();
                        } else {
                            cell.setCellType(CellType.STRING);
                            value = cell.getStringCellValue();
                        }
                        break;
                    case BOOLEAN:
                        value = cell.getBooleanCellValue();
                        break;
                    default:
                }
                rowMap.put(title, value);
            }
        }
        return data;
    }

    private static String trimTitle(String title) {
        int i = title.indexOf('(');
        return i == -1 ? title : title.substring(0, i);
    }

    private static List<String> getTitleList(Sheet sheet, boolean trim) throws ParseException {
        Row titleRow = sheet.getRow(0);
        int columnCount = titleRow.getPhysicalNumberOfCells();
        LinkedHashSet<String> titleLinkedSet = new LinkedHashSet<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            Cell titleCell = titleRow.getCell(i);
            if (titleCell == null) {
                throw new ParseException("无效的标题!标题不能为空!columnIndex=" + columnNumberToName(i + 1));
            }
            if (titleCell.getCellTypeEnum() != CellType.STRING) {
                throw new ParseException("无效的标题!标题必需为文本!columnIndex=" + columnNumberToName(i + 1));
            }
            String title = titleCell.getStringCellValue().trim();
            if (trim) {
                title = trimTitle(title);
            }
            if (StringUtils.isBlank(title)) {
                throw new ParseException("无效的标题!标题不能为空字符串!columnIndex=" + columnNumberToName(i + 1));
            }
            if (!titleLinkedSet.add(title)) {
                throw new ParseException("标题不能重复,title=" + title);
            }
        }
        if (titleLinkedSet.isEmpty()) {
            throw new ParseException("标题行不能为空!");
        }
        return Lists.newArrayList(titleLinkedSet);
    }

    /**
     * 读取oss数据
     */
    public static List<String[]> readExcel(InputStream in, boolean isExcel2003) {
        List<String[]> dataList = new ArrayList<>();
        Workbook wb = null;
        try {
            wb = isExcel2003 ? new HSSFWorkbook(in) : new XSSFWorkbook(in);
        } catch (IOException ex) {
            Logger.getLogger(ExcelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        Sheet sheet = wb.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows();
        int totalCells = 0;
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            String[] rowList = new String[totalCells];
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                String cellValue = "";
                if (cell == null) {
                    rowList[c] = (cellValue);
                    continue;
                }
                cellValue = convertCellStr(cell, cellValue);
                rowList[c] = (cellValue);
            }
            dataList.add(rowList);
        }
        return dataList;
    }


    public static <T> List<T> readStrictly(InputStream in, Class<T> clazz) throws IOException, ParseException {
        return read(in, clazz, true, true, Validator.nop());
    }

    public static <T> List<T> readStrictly(InputStream in, Class<T> clazz, Validator<T> validator) throws IOException, ParseException {
        return read(in, clazz, true, true, validator);
    }

    /**
     * 数据转化
     *
     * @param cell
     * @param cellStr
     * @return
     */
    private static String convertCellStr(Cell cell, String cellStr) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                // 读取String
                cellStr = cell.getStringCellValue();
                break;
            case BOOLEAN:
                // 得到Boolean对象的方法
                cellStr = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                // 先看是否是日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 读取日期格式
                    cellStr = formatTime(cell.getDateCellValue().toString());
                } else {
                    // 读取数字
                    cellStr = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                // 读取公式
                cellStr = cell.getCellFormula();
                break;
            default:
                break;
        }
        return cellStr;
    }

    private static String formatTime(String s) {
        SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = sf.parse(s);
        } catch (java.text.ParseException ex) {
            Logger.getLogger(ExcelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }


    public static boolean isExcel2007(String fileName) {
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }


}
