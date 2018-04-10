package me.cor_right.codeCounter.service;

import me.cor_right.codeCounter.util.PropertiesUtil;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/***
 * @author master 程序实际运行主体
 */
public class MainService implements FileFilter {

    private long linenumber;// 保存文件或文件夹的有效代码行数
    private int filenumber;// 保存文件数量
    private long filesize;// 保存文件或文件夹的字节数
    private String date;// 保存当前日期
    private boolean flag, isfile;// flag 判断文件是否存在，isfile判断文件的类型
    private String oriDirPath = "e:\\java_files\\workspace";// 保存检索路径
    private String tarExcalPath = "d:\\java\\saveRecord\\java编程量记录.xls";// 保存形成的ecxel文件保存的路径


    {
        this.oriDirPath = PropertiesUtil.getValue("source.default.path");
    }

    /**
     *
     */
    public void startSerch() {
        // 初始化
        this.flag = true;
        this.isfile = false;
        this.filesize = 0;
        this.filenumber = 0;
        this.linenumber = 0;
        this.date = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        File file = new File(oriDirPath);
        isfile = file.isFile();

        if (file.exists() == false && file.getAbsolutePath().equals("") == false) {// 文件不存在
            flag = false;
            return;
        }
        try {// 处理计算文件行数出缓冲流抛出的异常
            if (file.getAbsolutePath().equals("") == true) {
                searchFile(file);
            } else if (file.isFile()) {// 文件存在且是文件
                isfile = true;
                this.filenumber = 1;
                this.filesize = file.length();
                countLines(file);
                return;
            } else {// 文件存在且是文件夹
                searchFile(file);
            }
        } catch (IOException e) {
            e.getMessage();
            flag = false;
        }
    }

    /***
     * <b> 检索路径下文件是文件夹时运行该方法 用于计算文件夹下所有文件的各种信息 当路径为空时，默认检索所有盘符下所有文件
     *
     * @param file
     * @throws IOException
     */
    private void searchFile(File file) throws IOException {
        File[] files = null;
        if (this.oriDirPath.equals("") == false) {
            files = file.listFiles(this);
        } else {
            files = File.listRoots();
        }
        if (files != null)// 处理盘符下报控空指针的问题
            for (File curfile : files) {// 迭代遍历文件夹
                if (curfile.isDirectory()) {
                    searchFile(curfile);
                } else {
                    countLines(curfile);
                    this.filenumber++;
                    this.filesize += curfile.length();
                    System.out.println(this.filenumber + " : " + curfile.getAbsolutePath());
                }
            }
        return;
    }

    /***
     * 计算单个文件中有多少行代码，用正则匹配进行判断了 去掉空行，去掉全是；的行
     */
    private void countLines(File file) throws IOException {
        BufferedReader in = null;
        long fileline = 0;
        try {
            in = new BufferedReader(new FileReader(file));
            String data = null;
            while ((data = in.readLine()) != null) {
                if (data.matches("[\\s]+") == false && data.matches("[;]+") == false) {
                    fileline++;
                }
            }
            this.linenumber += fileline;
        } finally {
            in.close();
        }
    }

    /***
     * <b>保存到目标路径的文件中<br>
     * 如果文件不存在就创建一个（先创建文件夹，然后用api的write方法创建） <br>
     * 表格第一行是大标题《代码统计信息表》 <br>
     * 表格第二行是表头，从左到右分别为“日期”，”检索路径，，“java文件数”，“java文件总容量”，“总代码行数"（共5列） <br>
     * 如果表中存在日期相同且路径相同的数据，则对其进行更新，否则在末尾添加一条新的信息
     */
    public void saveToXLS() {
        flag = true;
        File file = new File(tarExcalPath);
        HSSFWorkbook book = null;
        // 初始化表格，得到的一定是解析好的写好表头和标题的book对象
        book = initailXLS(file);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            HSSFSheet sheet = book.getSheetAt(0);
            HSSFRow row = null;
            int rownum = 2;// 0，1行是标题和表头
            // 遍历文件，得到符合条件的行,推出循环时刚好是选中的那行或者最底下的空行
            while ((row = sheet.getRow(rownum)) != null) {
                String tempdate = row.getCell(0).getRichStringCellValue().toString();
                HSSFRichTextString temppath = row.getCell(1).getRichStringCellValue();
                HSSFRichTextString curpath = new HSSFRichTextString(this.oriDirPath);
                if (tempdate.equals(this.date) && temppath.equals(new HSSFRichTextString(this.oriDirPath))) {
                    break;
                }
                rownum++;
            }
            // 写数据
            row = sheet.createRow(rownum);
            HSSFCell cella = row.createCell(0);
            HSSFCell cellb = row.createCell(1);
            HSSFCell cellc = row.createCell(2);
            HSSFCell celld = row.createCell(3);
            HSSFCell celle = row.createCell(4);
            cella.setCellValue(new HSSFRichTextString(this.date));
            cellb.setCellValue(new HSSFRichTextString(this.oriDirPath));
            cellc.setCellValue(this.filenumber);
            celld.setCellValue(this.filesize);
            celle.setCellValue(this.linenumber);
            cella.setCellStyle(myCellStyle(book));
            cellb.setCellStyle(myCellStyle(book));
            cellc.setCellStyle(myCellStyle(book));
            celld.setCellStyle(myCellStyle(book));
            celle.setCellStyle(myCellStyle(book));

            book.write(out);
            out.close();
        } catch (IOException ee) {
            ee.getMessage();
            flag = false;
        }

    }

    /***
     * 判断文件是否存在，存在则解析已有文件，不存在则创建文件。同时写好表头和标题
     *
     * @return
     */
    private HSSFWorkbook initailXLS(File file) {
        HSSFWorkbook book = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        if (file.exists()) {
            try {
                in = new FileInputStream(file);
                POIFSFileSystem fs = new POIFSFileSystem(in);
                book = new HSSFWorkbook(fs);
                in.close();
            } catch (IOException ee) {
                ee.getMessage();
                flag = false;
            }
        } else {
            // 创建文件夹
            if (file.getParentFile().exists() == false) {
                file.getParentFile().mkdirs();
            }
            book = new HSSFWorkbook();
            HSSFSheet sheet = book.createSheet("代码量统计表");
            HSSFRow row = sheet.createRow(0);
            try {
                // 设定标题字体
                HSSFCellStyle stylea = book.createCellStyle();
                stylea.cloneStyleFrom(myCellStyle(book));
                HSSFFont fonta = book.createFont();
                fonta.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                fonta.setFontName("楷体");
                fonta.setFontHeightInPoints((short) 30);
                fonta.setCharSet(HSSFFont.DEFAULT_CHARSET);
                stylea.setFont(fonta);
                // 设置列宽
                sheet.setColumnWidth(0, 1800 * 5);
                sheet.setColumnWidth(1, 3000 * 5);
                sheet.setColumnWidth(2, 1200 * 5);
                sheet.setColumnWidth(3, 2400 * 5);
                sheet.setColumnWidth(4, 2400 * 5);
                // 制作标题
                HSSFCell cell = row.createCell(0);
                CellRangeAddress range = new CellRangeAddress(0, 0, 0, 4);
                cell.setCellStyle(stylea);
                sheet.addMergedRegion(range);
                out = new FileOutputStream(file);
                cell.setCellValue(new HSSFRichTextString("代码信息统计表"));
                // 设置表头
                HSSFCellStyle styleb = book.createCellStyle();
                styleb.cloneStyleFrom(myCellStyle(book));
                HSSFFont fontb = book.createFont();
                fontb.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                fontb.setFontName("楷体");
                fontb.setFontHeightInPoints((short) 20);
                fontb.setCharSet(HSSFFont.DEFAULT_CHARSET);
                styleb.setFont(fontb);
                row = sheet.createRow(1);
                HSSFCell cella = row.createCell(0);
                HSSFCell cellb = row.createCell(1);
                HSSFCell cellc = row.createCell(2);
                HSSFCell celld = row.createCell(3);
                HSSFCell celle = row.createCell(4);
                cella.setCellStyle(styleb);
                cellb.setCellStyle(styleb);
                cellc.setCellStyle(styleb);
                celld.setCellStyle(styleb);
                celle.setCellStyle(styleb);
                cella.setCellValue(new HSSFRichTextString("日期"));
                cellb.setCellValue(new HSSFRichTextString("检索路径"));
                cellc.setCellValue(new HSSFRichTextString("文件数"));
                celld.setCellValue(new HSSFRichTextString("文件总容量"));
                celle.setCellValue(new HSSFRichTextString("代码行数"));
                book.write(out);
                out.close();
            } catch (IOException ee) {
                ee.getMessage();
                flag = false;
            }
        }
        return book;
    }

    /***
     * 设置好的单元格风格
     *
     * @param wb
     * @return HSSFCellStyle
     */
    private HSSFCellStyle myCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        HSSFFont font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 14);
        font.setCharSet(HSSFFont.DEFAULT_CHARSET);
        style.setFont(font);
        return style;
    }

    /***
     * @return 返回文件是否运行正确的信息，再根据MainFrame方法中不同的位置打印不同的错误信息
     */
    public boolean getFlag() {
        return this.flag;
    }

    /***
     * 返回路径下文件类型
     *
     * @return 文件返回真，文件夹返回假
     */
    public boolean getFileType() {
        if (this.isfile)
            return true;
        else
            return false;
    }

    /**
     * @return 文件检索路径
     */
    public String getOriPath() {
        return this.oriDirPath;
    }

    /**
     * @return 文件检索路径
     */
    public String getTarPath() {
        return this.tarExcalPath;
    }

    /**
     * @return 文件数
     */
    public int getFileNumber() {
        return this.filenumber;
    }

    /**
     * @return 文件或文件夹中代码行数
     */
    public long getCodeLines() {
        return this.linenumber;
    }

    /**
     * @return 文件或文件夹容量
     */
    public long getFileSize() {
        return this.filesize;
    }

    /**
     * @return 当前日期
     */
    public String getDate() {
        return this.date;
    }

    /***
     * 修改检索路径
     */
    public void setOriPath(String newpath) {
        System.out.println(newpath);
        if (newpath.matches("[c-z]+:*")) {
            newpath = newpath.substring(0, 1);
            newpath = newpath.concat(":\\");
        }
        System.out.println(newpath);
        // 保存到
        this.oriDirPath = newpath;
        PropertiesUtil.setValue("source.default.path", newpath);
    }

    /***
     * 修改存储路径
     */
    public void setTarPath(String newpath) {
        this.tarExcalPath = newpath;
    }

    /***
     * <b> 定义过滤器，用于对文件进行从头到尾的检索，检索所有以“.java”结尾的文本文件（不区分大小写）
     *
     * @return 文件夹和符合条件的文件返回真true，其他返回false
     */
    public boolean accept(File thisfile) {
        if (thisfile.isDirectory())
            return true;
        if (thisfile.getName().toLowerCase().endsWith(".java"))
            return true;
        return false;
    }

}
