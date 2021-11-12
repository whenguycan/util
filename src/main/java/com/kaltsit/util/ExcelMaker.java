package com.kaltsit.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author wangcy
 * @date 2021/11/11 8:32
 */
public class ExcelMaker {

    Class<? extends Workbook> type;

    String title;
    List<String> columns = new ArrayList<>();
    Map<Integer, List<String>> validations = new HashMap<>();
    List<List<String>> items = new ArrayList<>();

    private ExcelMaker(Class<? extends Workbook> type) {
        this.type = type;
    }

    public static <T extends Workbook> ExcelMaker getInstance(Class<T> type) {
        return new ExcelMaker(type);
    }

    public ExcelMaker setTitle(String title) {
        this.title = title;
        return this;
    }

    public ExcelMaker setColumns(String... columns) {
        if(columns != null && columns.length != 0) {
            this.columns.addAll(Arrays.asList(columns));
        }
        return this;
    }

    public ExcelMaker addValidation(String colName, List<String> validation) {
        if(validation == null || validation.isEmpty()) {
            throw new RuntimeException("validation is empty");
        }
        if(StringUtils.isEmpty(colName)) {
            throw new RuntimeException("colName is empty");
        }
        String colNameUpper = colName.toUpperCase();
        Pattern pattern = Pattern.compile("[A-Z]{1,2}");
        if(!pattern.matcher(colNameUpper).matches()) {
            throw new RuntimeException("colName incorrect, and must be from A to ZZ");
        }
        validations.put(getIndex(colNameUpper), validation);
        return this;
    }

    private int getIndex(String colNameUpper) {
        if(colNameUpper.length() == 1) {
            return colNameUpper.charAt(0) - 65;
        }
        if(colNameUpper.length() == 2) {
            return (colNameUpper.charAt(0) - 64) * 26 + colNameUpper.charAt(1) - 65;
        }
        return -1;
    }

    public ExcelMaker addItem(List<String> item) {
        if(item != null) {
            items.add(item);
        }
        return this;
    }

    public ExcelMaker addItem(String... fields) {
        if(fields != null && fields.length != 0) {
            items.add(Arrays.asList(fields));
        }
        return this;
    }

    public ExcelMaker addItems(List<List<String>> items) {
        if(items != null) {
            items.addAll(items);
        }
        return this;
    }

    public Workbook makeWorkbook() {
        check();
        try {
            int size = columns.size();
            Workbook workbook = type.newInstance();
            Sheet sheet = workbook.createSheet();
            //title
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, size - 1));
            Cell row0cell0 = sheet.createRow(0).createCell(0);
            row0cell0.setCellValue(title);
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            row0cell0.setCellStyle(style);
            //headers
            Row row1 = sheet.createRow(1);
            for(int i=0,len=size; i<len; i++) {
                sheet.setColumnWidth(i, 20 * 256);
                row1.createCell(i).setCellValue(columns.get(i));
            }
            //validations
            if(!validations.isEmpty()) {
                for(Map.Entry<Integer, List<String>> entry : validations.entrySet()) {
                    int idx = entry.getKey();
                    if(idx < size) {
                        String[] values = entry.getValue().toArray(new String[0]);
                        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
                        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(values);
                        CellRangeAddressList range = new CellRangeAddressList(2, 65535, idx, idx);
                        DataValidation validation = validationHelper.createValidation(constraint, range);
                        sheet.addValidationData(validation);
                    }
                }
            }
            //items
            if(!items.isEmpty()) {
                for(int i=0,iLen=items.size(); i<iLen; i++) {
                    int rowIndex = i + 2;
                    List<String> item = items.get(i);
                    if(item != null && !item.isEmpty()) {
                        Row row = sheet.createRow(rowIndex);
                        for(int j=0,jLen=item.size(); j<jLen; j++) {
                            if(j < size) {
                                Cell cell = row.createCell(j);
                                String value = item.get(j);
                                if(StringUtils.isNotEmpty(value)) {
                                    cell.setCellValue(item.get(j));
                                }
                            }
                        }
                    }
                }
            }
            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void check() {
        if(StringUtils.isEmpty(title)) {
            throw new RuntimeException("title is empty");
        }
        if(CollectionUtils.isEmpty(columns)) {
            throw new RuntimeException("columns is empty");
        }
    }
}