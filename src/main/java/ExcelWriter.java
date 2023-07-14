import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;


import java.io.*;
import java.util.*;

public class ExcelWriter {

    private String fileName = "output.xlsx";
    private  static Map<String,Integer> rowCounter = new HashMap<>();
    private String DELIMITER = "##";

    public ExcelWriter(){

    }

    public ExcelWriter(String fileName){
        this.fileName = fileName;
    }

    public void setRowCounter(String sheetName, int counter){rowCounter.put(sheetName,counter);}
    private void incrementRowCounter(String sheetName){rowCounter.put(sheetName,rowCounter.get(sheetName)+1);}

    private void addRowToSheet(Workbook workbook, Sheet sheet, String data, String type){

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");

        if(type.equals("header")){
            font.setBold(true);
            style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        if(data.contains(DELIMITER + "Failed" + DELIMITER)){
            style.setFillBackgroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        else if(data.contains(DELIMITER + "Passed" + DELIMITER)){
            style.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        style.setFont(font);

        List<String> dataList = ListUtils.getListFromString(data,DELIMITER);

        Row row = sheet.createRow(rowCounter.get(sheet.getSheetName()));
        int columnCounter = 0;
        for(Iterator<String> iterator = dataList.iterator(); iterator.hasNext();){
            String value = iterator.next().trim();

            Cell cell = row.createCell(columnCounter++);
            cell.setCellValue(value);
            //cell.setCellType(style);
        }

        incrementRowCounter(sheet.getSheetName());

    }

    private  void addRowToAllSheets(String sheetName, String data, String type) throws IOException {
        if(sheetName.contains(DELIMITER)){
            List<String> sheetList = ListUtils.getListFromString(sheetName,DELIMITER);
            for(Iterator<String> iterator = sheetList.iterator();iterator.hasNext();){
                addRow(iterator.next(),data,type);
            }
        }
        else{
            addRow(sheetName,data,type);
        }
    }

    private  void addRowToAllSheets(String sheetName, List<String> data, String type) throws IOException {
        if(sheetName.contains(DELIMITER)){
            List<String> sheetList = ListUtils.getListFromString(sheetName,DELIMITER);
            for(Iterator<String> iterator = sheetList.iterator();iterator.hasNext();){
                addRow(iterator.next(),data,type);
            }
        }
        else{
            addRow(sheetName,data,type);
        }
    }

    public void addDataRows(String sheetName, List<String> data) throws IOException {
        addRowToAllSheets(sheetName,data,"data");
    }

    private void addRow(String sheetName, String data, String type) throws IOException {
        FileInputStream in = new FileInputStream(new File(fileName));
        Workbook workbook = WorkbookFactory.create(in);
        Sheet sheet =workbook.getSheet(sheetName);
        addRowToSheet(workbook,sheet,data,type);
        for(int i=0;i<10;i++){
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(new File(fileName));
        workbook.write(out);
        out.close();
        in.close();
    }

    private void addRow(String sheetName, List<String> data, String type) throws IOException {
        FileInputStream in = new FileInputStream(new File(fileName));
        Workbook workbook = WorkbookFactory.create(in);
        Sheet sheet =workbook.getSheet(sheetName);
        for(Iterator<String> iterator = data.iterator();iterator.hasNext();) {
            String rowString = iterator.next();
            addRowToSheet(workbook, sheet, rowString, type);
        }
        for(int i=0;i<10;i++){
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(new File(fileName));
        workbook.write(out);
        out.close();
        in.close();
    }

    public static void exportJobsToExcel(List<JobBean> jobList,String summary) throws IOException {
        String timeStamp = DateUtils.getCurrentDate("yyyy-MM-dd'_'HH-mm-SS'_'Z");
        File templateFile = new File("test-classes/Execution_Report_Template.xlsx");
        if(!templateFile.exists()){
            templateFile = new File("target/test-classes/Execution_Report_Template.xlsx");
        }
        File reportFile = new File("Execution_Report_"+ timeStamp + ".xlsx");
        FileUtils.copyFile(templateFile,reportFile);

        ExcelWriter excelWriter = new ExcelWriter(reportFile.getName());
        excelWriter.setRowCounter("All",1);

        List<String> jobDataList = new ArrayList<>();

        for(Iterator<JobBean> iterator= jobList.iterator();iterator.hasNext();){

        }
    }







}