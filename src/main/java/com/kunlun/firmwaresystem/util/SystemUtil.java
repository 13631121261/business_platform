package com.kunlun.firmwaresystem.util;

import com.kunlun.firmwaresystem.entity.Tag;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.customerMap;
import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;
import static java.lang.Thread.sleep;

public class SystemUtil {


    private RedisUtils redisUtils;
    private static SystemUtil util;

    public static SystemUtil getUtil() {
        if (util == null) {
            util = new SystemUtil();
            return util;
        } else {
            return util;
        }
    }

    private SystemUtil() {
        redisUtils = SpringUtil.getBean(RedisUtils.class);
    }






    public static void writeExcel(File file, String columns[],ArrayList<HashMap<String,String>> mapList) {
        //创建Excel文件薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建工作表sheeet
        HSSFSheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        Cell cell;
        for (int i = 0; i < columns.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(columns[i]);
        }
        int i = 1;
        for (Map<String, String> map : mapList) {
            row = sheet.createRow(i);
            int j = 0;
            for (String column : map.keySet()) {
                cell = row.createCell(j);
                cell.setCellValue(map.get(column));
                j++;
            }
            i++;
        }
        try {
            FileOutputStream stream = new FileOutputStream(file);
            workbook.write(stream);
            stream.close();
        }catch (Exception e){
            myPrintln("文件异常="+e.getMessage());
        }
        }


    public static ArrayList<HashMap<String, String>> readExcel(MultipartFile file, String columns[]) {
        //String logFilePath = Environment.getExternalStorageDirectory() + File.separator + "Visitor";
        Sheet sheet = null;
        Row row = null;
        Row rowHeader = null;
        ArrayList<HashMap<String, String>> list = null;
        String cellData = null;
        Workbook wb = null;
        if (file == null) {
            return null;
        }
        myPrintln("地址=" + file.getOriginalFilename());
        InputStream is = null;
        try {
            myPrintln("1111");
            is =file.getInputStream();
            myPrintln("12222"+is.available());
            if (file.getOriginalFilename().contains("xlsx")) {
                myPrintln("类型在此5");
                try {
                    myPrintln("类型在8"+is);
                    wb = new XSSFWorkbook(is);
                    myPrintln("类型在此66"+wb);
                }catch (IOException e){
                    myPrintln("大大的异常="+e.toString());
                }
                myPrintln("类型在此111");
            } else if (file.getOriginalFilename().contains("xls")) {
                myPrintln("类型在此"+is);
                wb = new HSSFWorkbook(is);
                myPrintln("类型在此6666");
                myPrintln(wb.toString());
            } else {
                wb = null;
            }
            if (wb != null) {
                myPrintln("65656");
                // 用来存放表中数据
                list = new ArrayList<HashMap<String, String>>();
                // 获取第一个sheet
                sheet = wb.getSheetAt(0);

                // 获取最大行数
                int rownum = sheet.getPhysicalNumberOfRows();
                myPrintln("输出行数="+rownum);
                // 获取第一行
                rowHeader = sheet.getRow(0);
                row = sheet.getRow(0);
                //sheet.createRow(5).createCell(0).setCellValue("今年");


                // 获取最大列数
                int colnum = row.getPhysicalNumberOfCells();
                for (int i = 1; i < rownum; i++) {
                    HashMap<String, String> map = new LinkedHashMap<String, String>();
                    row = sheet.getRow(i);
                    if (row != null) {
                        for (int j = 0; j < columns.length; j++) {
                            myPrintln("J=" + j);
                            if (columns[j].equals(getCellFormatValue(rowHeader.getCell(j)))) {
                                cellData = (String) getCellFormatValue(row
                                        .getCell(j));
                                myPrintln("读取=" + cellData + "J=" + j);
                                map.put(columns[j], cellData.replaceAll(" ", ""));
                                /*DecimalFormat df = new DecimalFormat("#");
                                myPrintln(    df.format(cellData));*/
                                // Logs.e("yy","cellData="+cellData);
                                //Logs.e("yy","map="+map);
                            }
                        }
                    } else {
                        break;
                    }
                    list.add(map);
                }
            }else{
                myPrintln("7878");
            }
        } catch (FileNotFoundException e) {

            myPrintln("异常"+e.getMessage());
            return null;
        } catch (IOException e) {
            myPrintln("异常22="+e.getMessage());


            return null;
        }
        return list;
    }

    /**
     * 获取单个单元格数据
     *
     * @param cell
     * @return
     * @author lizixiang ,2018-05-08
     */
    public static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            // 判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        // 数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

/*

public static  void  createExcelTwo(String path , String[] title, Map<String, Devicep> map_list) {
    HSSFWorkbook workbook =new  HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet();
    HSSFRow row=null;
    row = sheet.createRow(0);
    HSSFCell cell= null;
    int i=0;
    for(String t:title){
        cell=row.createCell(i);
        cell.setCellValue(t);
        i++;
    }
    i=1;
    Devicep deviceP;
    for(String sn:map_list.keySet()){
        deviceP=map_list.get(sn);
        row=sheet.createRow(i);
        i++;
        cell=row.createCell(0);
        cell.setCellValue(deviceP.getName());
        cell=row.createCell(1);
        cell.setCellValue(deviceP.getSn());
        cell=row.createCell(2);
        if(deviceP.getIsbind()==1){
            cell.setCellValue("已绑定信标");
        }else{
            cell.setCellValue("未绑定信标");
        }
        cell=row.createCell(3);
        cell.setCellValue(deviceP.getBind_mac());
        cell=row.createCell(4);
        cell.setCellValue(customerMap.get(deviceP.getCustomer_key()).getNickname());
        cell=row.createCell(5);
        cell.setCellValue(deviceP.getType());
        cell=row.createCell(6);
        if(deviceP.getSos()==1){
            cell.setCellValue("触发报警");
        }else{
            cell.setCellValue("正常");
        }
        cell=row.createCell(7);
        if(deviceP.getOnline()==1){
            cell.setCellValue("在线");
        }else {
            cell.setCellValue("离线");
        }
        cell=row.createCell(8);
        cell.setCellValue(deviceP.getLasttime());
        cell=row.createCell(9);
        cell.setCellValue(deviceP.getPoint_name());
        cell=row.createCell(10);
        cell.setCellValue(deviceP.getRssi());
        cell=row.createCell(11);
        cell.setCellValue(deviceP.getStation_mac());
        cell=row.createCell(12);
        cell.setCellValue(deviceP.getBt()+"V");
        cell=row.createCell(13);
        cell.setCellValue(deviceP.getCreatetime());
    }
    File file = new File(path);
    try{
        file.createNewFile();
        FileOutputStream stream = new FileOutputStream(file);
        workbook.write(stream);
        stream.close();
    }catch (IOException e){
        myPrintln("盘点记录文件保存异常"+e.getMessage());
    }

}*/

    public static  void  createExcelBeacon(String path , String[] title,    List<Tag> tagList) {
        HSSFWorkbook workbook =new  HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row=null;
        row = sheet.createRow(0);
        HSSFCell cell= null;
        int i=0;
        for(String t:title){
            cell=row.createCell(i);
            cell.setCellValue(t);
            i++;
        }
        i=1;
        Devicep deviceP;
        for(Tag tag : tagList){
        //  String[] titles = {"MAC", "在线状态", "绑定状态", "资产编码/身份证","资产/人员", "电压", "创建时间", "在线时间"};
            row=sheet.createRow(i);
            i++;
            cell=row.createCell(0);
            cell.setCellValue(tag.getMac());

            cell=row.createCell(1);
            cell.setCellValue(tag.getOnline()==1?"OnLine":"OffLine");

            cell=row.createCell(2);
            cell.setCellValue(tag.getIsbind()==1?"Bind":"unBind");

            cell=row.createCell(3);
            cell.setCellValue(tag.getBind_key());

            cell=row.createCell(4);
            cell.setCellValue(tag.getBind_key());

            cell=row.createCell(5);
            cell.setCellValue(tag.getBt());

            cell=row.createCell(6);
            cell.setCellValue(tag.getCreatetime());

            cell=row.createCell(7);
            cell.setCellValue(tag.getLastTime());
        }
        File file = new File(path);
        try{
            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            workbook.write(stream);
            stream.close();
        }catch (IOException e){
            myPrintln("盘点记录文件保存异常"+e.getMessage());
        }

    }
}
