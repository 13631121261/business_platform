package com.kunlun.firmwaresystem.util;


import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kunlun.firmwaresystem.entity.History;
import com.kunlun.firmwaresystem.entity.StationStayAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ITextTableGenerator {

    public static void main(String[] args) throws IOException {
        String dest = "employee_table.pdf";
        File file = new File(dest);
        file.getAbsoluteFile().createNewFile();
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        // 创建表格 (4列)
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        // 添加表头
        addTableHeader(table);
        // 添加数据行
        addRows(table);
        document.add(table);
        document.close();
    }
    private static void addTableHeader(Table table) {
        String[] headers = {"ID", "姓名", "部门", "薪资"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .setBackgroundColor(new DeviceGray(0.75f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(new Paragraph(header));
            table.addCell(cell);
        }
    }
    private static void addRows(Table table) {
        String[][] data = {
                {"101", "张三", "研发部", "¥15,000"},
                {"102", "李四", "市场部", "¥12,000"},
                {"103", "王五", "财务部", "¥13,500"}
        };
        for (String[] row : data) {
            for (String cellData : row) {
                table.addCell(new Cell()
                        .setTextAlignment(TextAlignment.CENTER)
                        .add(new Paragraph(cellData)));
            }
        }
    }

    public static File createPdf(List<StationStayAnalyzer.StationStay> list,long srart_time,long end_time) {
        try {
            System.out.println("111");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String dest = "export_history";
            File file = new File(dest);
            if (!file.exists()) {
                System.out.println("222");
                file.mkdirs();
            }
            System.out.println("333");
            File outfile = new File(file,file.getName() + System.currentTimeMillis() + ".pdf");
            WriterProperties a=   new WriterProperties();
            a.useSmartMode();
            PdfWriter writer = new PdfWriter(String.valueOf(outfile.getAbsoluteFile()),  a);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
            System.out.println("444");
            // 添加标题
            Paragraph title = new Paragraph("Historical location of Personnel or Vehicles")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            System.out.println("555");
            // 添加副标题
            Paragraph subtitle = new Paragraph(df.format(srart_time) + " to " + df.format(end_time))
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitle);
            // 创建表格 (4列)
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1,1, 1, 1, 1, 1}));
            table.setWidth(UnitValue.createPercentValue(100));
            System.out.println("666");
            // 添加表头
            String[] headers = {"Name", "SN/ID No","Company Name", "Station Name", "Start Time", "Stop Time", "Duration"};
            for (String header : headers) {
                System.out.println("777"+header);
                Cell cell = new Cell()
                        .setFontSize(10)
                        .setBackgroundColor(new DeviceGray(0.75f))
                        .setTextAlignment(TextAlignment.CENTER)
                        .add(new Paragraph(header));
                table.addCell(cell);
            }
            System.out.println("开始循环");
            int i=0;
            for (StationStayAnalyzer.StationStay stationStay : list) {
                i++;
                if(stationStay.getStationMac()==null||stationStay.getName()==null||stationStay.getSn()==null||stationStay.getEndTime()==0||stationStay.getStartTime()==0||stationStay.getDuration_sec()==-1 ){

                    System.out.println("7--7"+stationStay);
                        continue;
                }

                if (i % 1000 == 0) {
                    document.add(table);
                    document.flush();
                    table.flush();// 强制刷新缓冲区
                    table = new Table(UnitValue.createPercentArray(new float[]{1, 1,1, 1, 1, 1, 1}));
                    table.setWidth(UnitValue.createPercentValue(100));
                   // System.out.println("Processed: " + i + " rows");
                    i=0;
                }
              try {
                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .add(new Paragraph(stationStay.getName())));
                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .add(new Paragraph(stationStay.getSn())));
                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .setFont(font)
                          .add(new Paragraph(stationStay.getCompany_name())));
                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .setFont(font)
                          .add(new Paragraph(stationStay.getStation_name())));

                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .add(new Paragraph(df.format(stationStay.getStartTime()))));
                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .add(new Paragraph(df.format(stationStay.getEndTime()))));
                  int hours = stationStay.getDuration_sec() / 3600;
                  int minutes = (stationStay.getDuration_sec() % 3600) / 60;
                  int seconds = stationStay.getDuration_sec() % 60;
                  table.addCell(new Cell()
                          .setFontSize(10)
                          .setTextAlignment(TextAlignment.CENTER)
                          .add(new Paragraph(String.format("%02d:%02d:%02d", hours, minutes, seconds))));
              }catch (Exception e) {
                  System.out.println(stationStay.getStartTime());
                  System.out.println(stationStay.getEndTime());
                  System.out.println(stationStay.getDuration_sec());
                  System.out.println("为何="+e.toString());
              }
            }
            System.out.println("kkkk开始保存");
            try {
                document.add(table);
                document.close();
            }catch (Exception e) {
                System.out.println("为何"+e.toString());
            }
            System.out.println("文件保存为="+outfile.getAbsolutePath());
            return outfile;
        }catch (FileNotFoundException e) {
            System.out.println("异常到这里"+e.getMessage());
        } catch (IOException e) {
            System.out.println("222异常到这里"+e.getMessage());

        }
        return null;
    }
}