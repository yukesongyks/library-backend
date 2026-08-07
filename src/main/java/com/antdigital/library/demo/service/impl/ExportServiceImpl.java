package com.antdigital.library.demo.service.impl;

import com.antdigital.library.common.exception.ErrorCodeEnum;
import com.antdigital.library.common.exception.ServiceException;
import com.antdigital.library.demo.model.vo.BubbleSortVO;
import com.antdigital.library.demo.model.vo.HashVO;
import com.antdigital.library.demo.model.vo.HelloWorldVO;
import com.antdigital.library.demo.service.DemoService;
import com.antdigital.library.demo.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 导出服务实现。
 *
 * <p>支持导出三种页面展示结果为 CSV 文件。</p>
 *
 * @author library-backend
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    private static final String CONTENT_TYPE = "text/csv;charset=UTF-8";

    private final DemoService demoService;

    public ExportServiceImpl(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public void export(String type, String input, HttpServletResponse response) {
        if (type == null || type.isBlank()) {
            throw new ServiceException(ErrorCodeEnum.PARAM_EMPTY, "导出类型不能为空");
        }

        String fileName = "export_" + type + "_" + System.currentTimeMillis() + ".csv";
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition",
                "attachment;filename=" + java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try (OutputStream os = response.getOutputStream()) {
            String csvContent = buildCsvContent(type, input);
            // 写入 UTF-8 BOM，确保 Excel 正确识别编码
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            os.write(csvContent.getBytes(StandardCharsets.UTF_8));
            os.flush();
            logger.info("导出完成, type: {}, fileName: {}", type, fileName);
        } catch (IOException e) {
            logger.error("导出失败, type: {}, errorMessage: {}", type, e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.SYSTEM_ERROR, "导出失败", "导出文件失败，请重试");
        }
    }

    /**
     * 根据类型构建 CSV 内容。
     *
     * @param type  导出类型
     * @param input 输入参数
     * @return CSV 字符串
     */
    private String buildCsvContent(String type, String input) {
        return switch (type) {
            case "helloworld" -> buildHelloWorldCsv();
            case "hash" -> buildHashCsv(input);
            case "bubble-sort" -> buildBubbleSortCsv(input);
            default -> {
                logger.warn("不支持的导出类型: {}", type);
                throw new ServiceException(ErrorCodeEnum.UNSUPPORTED_TYPE,
                        "不支持的导出类型: " + type, "请选择有效的导出类型");
            }
        };
    }

    /**
     * 构建 HelloWorld 导出 CSV。
     */
    private String buildHelloWorldCsv() {
        HelloWorldVO vo = demoService.helloWorld();
        return "message,timestamp\n" + vo.getMessage() + "," + vo.getTimestamp() + "\n";
    }

    /**
     * 构建哈希结果导出 CSV。
     */
    private String buildHashCsv(String input) {
        HashVO vo = demoService.hash(input);
        return "input,algorithm,hash_value\n"
                + vo.getInput() + "," + vo.getAlgorithm() + "," + vo.getHashValue() + "\n";
    }

    /**
     * 构建冒泡排序结果导出 CSV。
     */
    private String buildBubbleSortCsv(String input) {
        BubbleSortVO vo = demoService.bubbleSort(input);
        StringBuilder sb = new StringBuilder();
        sb.append("index,input,output\n");
        List<Integer> inList = vo.getInput();
        List<Integer> outList = vo.getOutput();
        int maxLen = Math.max(inList.size(), outList.size());
        for (int i = 0; i < maxLen; i++) {
            String inVal = i < inList.size() ? String.valueOf(inList.get(i)) : "";
            String outVal = i < outList.size() ? String.valueOf(outList.get(i)) : "";
            sb.append(i).append(",").append(inVal).append(",").append(outVal).append("\n");
        }
        return sb.toString();
    }
}
