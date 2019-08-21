package com.example.demo.excel;

import com.example.demo.excel.entity.ExportPOJO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by mgy on 2019/8/21
 */
@Slf4j
public class ExcelTest {

    private void exportList(HttpServletResponse httpResponse, List<ExportPOJO> exportPOJOList) throws IOException {
        try {
            try (XSSFWorkbook wb = ExcelHelper.create(exportPOJOList)) {
                httpResponse.setContentType("application/xlsx");
                OutputStream out = new BufferedOutputStream(httpResponse.getOutputStream());
                wb.write(out);
            }
        } catch (Exception e) {
            log.error("导出列表失败!", e);
            ServletOutputStream out = httpResponse.getOutputStream();
            IOUtils.write("导出列表失败!", out);
            IOUtils.closeQuietly(out);
        }
    }
}
