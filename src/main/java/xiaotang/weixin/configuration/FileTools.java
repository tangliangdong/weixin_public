//package xiaotang.weixin.configuration;
//
//import com.gonghui.pay.common.exceptions.ValidationException;
//import com.gonghui.pay.common.utils.string.StringUtil;
//import com.gonghui.pay.common.validate.Check;
//import net.sf.jxls.transformer.XLSTransformer;
//import org.apache.commons.io.FileUtils;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * 文件相关操作
// *
// * @author ldd on 2016/7/25.
// */
//@Component
//public class FileTools {
//    public static final String EXCEL_EXT_XLS = "xls";  //excel 2003版本
//    public static final String EXCEL_EXT_XLSX = "xlsx";  //excel 2007版本
//    private static Logger logger = LoggerFactory.getLogger(FileTools.class);
//    @Value("${upload.url}")
//    private String uploadUrl;
//    @Value("${export.tempPath}")
//    private String exportPath;
//    @Value("${excel.upload.max-size}")
//    private String maxFileSize;
//
//    private long parseSize(String size) {
//        Check.hasText(size, "文件大小不能为空");
//        size = size.toUpperCase();
//        return size.endsWith("KB") ? Long.valueOf(size.substring(0, size.length() - 2)).longValue() * 1024L : (size.endsWith("MB") ? Long.valueOf(size.substring(0, size.length() - 2)).longValue() * 1024L * 1024L : Long.valueOf(size).longValue());
//    }
//
//    /**
//     * 文件大小限制
//     *
//     * @param size 上传文件的大小
//     */
//    public void compareFileSize(long size) {
//        Check.notNull(size, "文件大小不能为空");
//        Check.notNull(maxFileSize, "文件大小限制未定义");
//        if (size > parseSize(maxFileSize)) {
//            throw new ValidationException("上传文件不能大于" + maxFileSize);
//        }
//    }
//
//    /**
//     * 上传文件到application.proprerties 中的upload.url路径
//     * 按创建日期建立文件夹 yyyy-MM-dd
//     * 返回文件名称: 文件夹路径+ yyyy-MM-dd HH_mm_ss_文件名
//     *
//     * @param file MultipartFile
//     * @return 返回文件在服务器上的路径
//     * @throws IOException IOException
//     */
//    public String uploadExcel(MultipartFile file) throws IOException {
//        logger.info("上传文件：" + file.getOriginalFilename() + "准备中...");
//        String data = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss").format(new Date());
//        File f = new File(uploadUrl);
//        /* 若不存在则创建目录 */
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        //生成文件目录
//        StringBuilder stringBuilder = new StringBuilder(uploadUrl + "/" + data.substring(0, 10) + "/");
//        String fileName = file.getOriginalFilename();
//        if (fileName.indexOf("/") > -1) {
//            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
//        } else if (fileName.indexOf("\\") > -1) {
//            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
//        }
//        stringBuilder.append(data).append("_").append(fileName);
//        FileUtils.writeByteArrayToFile(new File(stringBuilder.toString()), file.getBytes());
//        logger.info("上传完成");
//        return stringBuilder.toString();
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param fileName
//     */
//    public void clearUploadFile(String fileName) {
//        try {
//            File file = new File(fileName);
//            if (file.isFile()) {
//                file.delete();
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//
//    /***
//     * 根据模版导出excel
//     *
//     * @param response response
//     * @param data      模版中使用的对象
//     */
//    public void exportExcel(HttpServletResponse response, Map data, String fileName, String templateName) throws IOException {
//        Check.notNull(data, "导出数据不能为空");
//        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
//        if (StringUtil.isNotEmpty(fileName)) {
//            uuid = fileName;
//        }
//        response.setHeader("Content-Disposition", "attachment; filename=" + uuid + "." + EXCEL_EXT_XLSX);
//        OutputStream output = null;
//        Workbook web_book = null;
//        try {
//            XLSTransformer xlsTransformer = new XLSTransformer();
//            output = response.getOutputStream();
//            web_book = xlsTransformer.transformXLS(new FileInputStream(
//                    exportPath + "/" + templateName), data);
//            web_book.write(output);
//        } catch (Exception e) {
//            logger.error("excel文件导出异常", e);
//        } finally {
//            try {
//                if (output != null) {
//                    output.flush();
//                    output.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 读取excel
//     * 返回值对应的参数{List : sheet,List : row ,String [] : cell}
//     *
//     * @param fileName 文件名
//     * @return 返回单元格数据的集合
//     * @throws ValidationException IOException
//     */
//    public List<List<String[]>> readExcel(String fileName) throws ValidationException, IOException {
//        Check.notNull(fileName, "文件不能为空");
//        if (!fileName.endsWith(EXCEL_EXT_XLS) && !fileName.endsWith(EXCEL_EXT_XLSX)) {
//            throw new ValidationException("不支持的文档类型");
//        }
//        FileInputStream fs = null;
//        List<List<String[]>> list = new ArrayList<>();
//        try {
//            File f = new File(fileName);
//            fs = new FileInputStream(f);
//            Workbook wb = null;
//            //针对不同版本的excel创建不同的workbook实现对象
//            long start = System.currentTimeMillis();
//            if (fileName.endsWith(EXCEL_EXT_XLS)) {
//                wb = new HSSFWorkbook(fs);
//            } else if (fileName.endsWith(EXCEL_EXT_XLSX)) {
//                wb = new XSSFWorkbook(fs);
//            }
//            long cost = System.currentTimeMillis() - start;
//            System.out.println(cost);
//
//            if (wb == null)
//                throw new ValidationException("不支持的文档类型");
//            int sheetNum = wb.getNumberOfSheets();
//            if (sheetNum == 0) return null;
//            for (int i = 0; i < sheetNum; i++) {
//                Sheet sheet = wb.getSheetAt(i);
//                if (wb.isSheetHidden(i)) {
//                    throw new ValidationException("您上传的文件中有隐藏的表格,请先删除隐藏表格或空白表格后再上传");
//                }
//                List<String[]> rowList = new ArrayList<>();
//                int totalRows = sheet.getPhysicalNumberOfRows();
//                Row emp_row = sheet.getRow(0);
//                if (totalRows == 0 || emp_row == null) {
//                    continue;
//                }
//                for (int row = sheet.getFirstRowNum(); row < totalRows; row++) {  //有效行
//                    if (sheet.getRow(row) == null)
//                        continue;
//                    int cellNum = sheet.getRow(row).getLastCellNum() - sheet.getRow(row).getFirstCellNum();  //有效列
//                    //强行设置有效列为9列
//                    if (cellNum < 9) {
//                        if(cellNum == 0) {
//                            continue;
//                        }
//                        cellNum = 9;
//                    }
//
//                    //如果本行全部列空字符串，则跳过
//                    int emptyCount = 0;
//                    for (int k = 0; k < cellNum; k ++) {
//                        Cell cell = sheet.getRow(row).getCell(k);
//                        if (cell == null) {
//                             emptyCount ++ ;
//                        }
//                        if(cell != null && cell.getCellType() == Cell.CELL_TYPE_BLANK) {
//                            emptyCount ++;
//                        }
//                    }
//                    if(emptyCount == cellNum) {
//                        continue;
//                    }
//
//                    String[] cells = new String[cellNum];
//
//                    //int nullCell = 0;//空的个数
//                    for (int cel = 0; cel < cellNum; cel++) {
//                        Cell cell = sheet.getRow(row).getCell(cel);
//                        if (cell == null) {
//                            //nullCell++;
//                            cells[cel] = "";
//                        } else {
//                            //获取单元格的值
//                            switch (cell.getCellType()) {
//                                case Cell.CELL_TYPE_NUMERIC: // 数字
//                                    BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
//                                    String value = bd.toPlainString().trim();
//                                    if (value.indexOf(".") > 0) {
//                                        bd = new BigDecimal(value);
//                                        cells[cel] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
//                                    } else
//                                        cells[cel] = value.trim();
//                                    break;
//                                default:// 非数字
//                                    String code = cell.getStringCellValue().trim();
//                                    code = code.replace("'", "");
//                                    code = code.replaceAll(" ", "");
//                                    code = code.replaceAll("\\s*", "");
//                                    code = code.replace("\"", "”");
//                                    cells[cel] = code;
//                                    //if (StringUtils.isEmpty(cells[cel])) nullCell++;
//                                    break;
//                            }
//                        }
//                    }
//                    //if (nullCell >= 8 && row > 1) continue;  //从读取第三行数据开始,行数据都为空不读取
//                    rowList.add(cells);
//                }
//                list.add(rowList);
//            }
//        } finally {
//            try {
//                if (fs != null) {
//                    fs.close();
//                }
//            } catch (IOException e) {
//                logger.error(e.getMessage(), e);
//            }
//        }
//        return list;
//    }
//}
