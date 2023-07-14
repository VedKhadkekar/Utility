import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {

    private Map<String, Map<String,String>> moduleSubModuleMap = new HashMap<>();
    private Map<String, Map<String,String>> viewMap = new HashMap<>();

    public void readModuleSubModule(String fileName) throws IOException {
        File filePath = new File("test-classes/"+ fileName);
        if(!filePath.exists()){
            filePath = new File("target/test-classes"+ fileName);
        }

        InputStream input = new FileInputStream(filePath.toString());
        XSSFWorkbook wb = new XSSFWorkbook(input);
        XSSFSheet sheet = wb.getSheetAt(0);

        for(Row row : sheet){

            if(row.getCell(0).toString().isEmpty()){
                break;
            }
            else {

                XSSFCell testNameCell = (XSSFCell) row.getCell(0);
                XSSFCell moduleCell = (XSSFCell) row.getCell(1);
                XSSFCell subModuleCell = (XSSFCell) row.getCell(2);

                Map<String, String> testPropertiesInnerMap = new HashMap<>();
                testPropertiesInnerMap.put("module", moduleCell.toString());
                testPropertiesInnerMap.put("SubModule", subModuleCell.toString());

                moduleSubModuleMap.put(testNameCell.toString(), testPropertiesInnerMap);
            }

        }

        System.out.println("Modules & SubModules are read from Excel");
        wb.close();
    }

    public String getModuleSubModule(String testName, String property){

        String output = "";
        try{
            output = moduleSubModuleMap.get(testName).get(property);
        }
        catch (NullPointerException e){
            output= "-";
        }
        return  output;
    }
}
