package com.hyperfit.util;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> excel导出工具 </p>
 */
public class ExcelUtil {

    /**
     * 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，可自定义工作表大小）
     *
     * @param list      数据源
     * @param fieldMap  类的英文属性和Excel中的中文列名的对应关系,如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式,如：
     *                  list中存放的都是student ，student中又有college属性，而我们需要学院名称，则可以这样写,fieldMap
     *                  .put("college.collegeName","学院名称")
     * @param sheetName 工作表的名称
     * @param sheetSize 每个工作表中记录的最大个数
     * @param out       导出流
     * @throws Exception 异常
     */
    public static <T> void listToExcel(List<T> list, LinkedHashMap<String, String> fieldMap, String sheetName,
                                       int sheetSize, OutputStream out) throws Exception {

        if (list == null || list.size() == 0) {
            throw new Exception("数据源中没有任何数据");
        }

        if (sheetSize > 65535 || sheetSize < 1) {
            sheetSize = 65535;
        }

        // 创建工作簿并发送到OutputStream指定的地方
        WritableWorkbook wwb;
        try {
            wwb = Workbook.createWorkbook(out);

            // 因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
            // 所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
            // 1.计算一共有多少个工作表
            Integer sheetSizeInteger = sheetSize;
            double sheetNum = Math.ceil(list.size() / sheetSizeInteger.doubleValue());

            // 2.创建相应的工作表，并向其中填充数据
            for (int i = 0; i < sheetNum; i++) {
                // 如果只有一个工作表的情况
                if (1 == sheetNum) {
                    WritableSheet sheet = wwb.createSheet(sheetName, i);
                    fillSheet(sheet, list, fieldMap, 0, list.size() - 1);
                    // 有多个工作表的情况
                } else {
                    WritableSheet sheet = wwb.createSheet(sheetName + (i + 1), i);
                    // 获取开始索引和结束索引
                    int firstIndex = i * sheetSize;
                    int lastIndex = (i + 1) * sheetSize - 1 > list.size() - 1 ? list.size() - 1 : (i + 1) * sheetSize - 1;
                    // 填充工作表
                    fillSheet(sheet, list, fieldMap, firstIndex, lastIndex);
                }
            }
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 导出Excel（导出到浏览器，可以自定义工作表的大小）
     *
     * @param list      数据源
     * @param fieldMap  类的英文属性和Excel中的中文列名的对应关系
     * @param sheetName 工作表的名称
     * @param sheetSize 每个工作表中记录的最大个数
     * @param response  使用response可以导出到浏览器
     * @param fileName  Excel的文件名
     * @throws Exception 异常
     */
    public static <T> void listToExcel(List<T> list, LinkedHashMap<String, String> fieldMap, String sheetName,
                                       int sheetSize, HttpServletResponse response, String fileName) throws Exception {

        // 如果文件名没提供，则使用时间戳
        if (fileName == null || fileName.trim().equals("")) {
            // 设置默认文件名为当前时间：年月日时分秒
            fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();
        }

        // 设置response头信息
        response.reset();
        response.setContentType("application/vnd.ms-excel"); // 改成输出excel文件
        response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

        // 创建工作簿并发送到浏览器
        try {
            OutputStream out = response.getOutputStream();
            listToExcel(list, fieldMap, sheetName, sheetSize, out);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 根据字段名获取字段值
     *
     * @param fieldName 字段名
     * @param o         对象
     * @return 字段值
     * @throws Exception 异常
     */
    public static Object getFieldValueByName(String fieldName, Object o) throws Exception {

        Object value = null;
        Field field = getFieldByName(fieldName, o.getClass());

        if (field != null) {
            field.setAccessible(true);
            value = field.get(o);
        } else {
            // throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 "
            // + fieldName);
        }

        return value;
    }

    /**
     * 根据字段名获取字段
     *
     * @param fieldName 字段名
     * @param clazz     包含该字段的类
     * @return 字段
     */
    public static Field getFieldByName(String fieldName, Class<?> clazz) {
        // 拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();

        // 如果本类中存在该字段，则返回
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        // 否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }

        // 如果本类和父类都没有，则返回空
        return null;
    }

    /**
     * 根据带路径或不带路径的属性名获取属性值,即接受简单属性名，如userName等，又接受带路径的属性名，如student.department. name等
     *
     * @param fieldNameSequence 带路径的属性名或简单属性名
     * @param o                 对象
     * @return 属性值
     * @throws Exception 异常
     */
    public static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {

        Object value = null;

        // 将fieldNameSequence进行拆分
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length == 1) {
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
            // 根据属性名获取属性对象
            Object fieldObj = getFieldValueByName(attributes[0], o);
            String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;

    }

    /**
     * 根据字段名给对象的字段赋值
     *
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param o          对象
     * @throws Exception 异常
     */
    public static void setFieldValueByName(String fieldName, Object fieldValue, Object o) throws Exception {

        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            field.setAccessible(true);
            // 获取字段类型
            Class<?> fieldType = field.getType();

            // 根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue != null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character.valueOf(fieldValue.toString().charAt(0)));
                }
            } else if (Date.class == fieldType) {
                field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
            } else {
                field.set(o, fieldValue);
            }
        } else {
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        }
    }

    /**
     * 设置工作表自动列宽和首行加粗
     *
     * @param ws        要设置格式的工作表
     * @param extraWith 额外的宽度
     */
    public static void setColumnAutoSize(WritableSheet ws, int extraWith) {
        // 获取本列的最宽单元格的宽度
        for (int i = 0; i < ws.getColumns(); i++) {
            int colWith = 0;
            for (int j = 0; j < ws.getRows(); j++) {
                String content = ws.getCell(i, j).getContents().toString();
                int cellWith = content.length() + getChineseNum(content);
                if (colWith < cellWith) {
                    colWith = cellWith;
                }
            }
            // 设置单元格的宽度为最宽宽度+额外宽度
            ws.setColumnView(i, colWith + extraWith);
        }

    }

    public static int getChineseNum(String context) { /// 统计context中是汉字的个数
        int lenOfChinese = 0;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字的Unicode编码范围
        Matcher m = p.matcher(context);
        while (m.find()) {
            lenOfChinese++;
        }
        return lenOfChinese;
    }

    /**
     * 向工作表中填充数据
     *
     * @param sheet      工作表名称
     * @param list       数据源
     * @param fieldMap   中英文字段对应关系的Map
     * @param firstIndex 开始索引
     * @param lastIndex  结束索引
     * @throws Exception 异常
     */
    public static <T> void fillSheet(WritableSheet sheet, List<T> list, LinkedHashMap<String, String> fieldMap,
                                     int firstIndex, int lastIndex) throws Exception {

        // 定义存放英文字段名和中文字段名的数组
        String[] enFields = new String[fieldMap.size()];
        String[] cnFields = new String[fieldMap.size()];

        // 填充数组
        int count = 0;
        for (Entry<String, String> entry : fieldMap.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }
        // 填充表头
        WritableFont wfont = getFont();
        WritableCellFormat wcfFC = new WritableCellFormat(wfont);// 表头格式
        WritableCellFormat contentF = new WritableCellFormat();// 正文格式
        wcfFC.setWrap(true);// 自动换行
        wcfFC.setAlignment(Alignment.CENTRE);
        wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);// 设置对齐方式
        contentF.setWrap(true);
        contentF.setAlignment(Alignment.CENTRE);
        contentF.setVerticalAlignment(VerticalAlignment.CENTRE);// 设置对齐方式

        for (int i = 0; i < cnFields.length; i++) {
            Label label = new Label(i, 0, cnFields[i]);
            label.setCellFormat(wcfFC);
            sheet.addCell(label);
        }

        // 填充内容
        int rowNo = 1;
        for (int index = firstIndex; index <= lastIndex; index++) {
            T item = list.get(index);
            for (int i = 0; i < enFields.length; i++) {
                Object objValue = getFieldValueByNameSequence(enFields[i], item);
                String fieldValue = objValue == null ? "" : objValue.toString();
                Label label = new Label(i, rowNo, fieldValue);
                label.setCellFormat(contentF);
                sheet.addCell(label);
            }
            rowNo++;
        }

        // 设置自动列宽
        setColumnAutoSize(sheet, 5);
    }

    // 设置字体
    private static WritableFont getFont() {
        return new WritableFont(WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE + 1, WritableFont.BOLD);
    }

}
