package com.library.reader.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import com.library.reader.dto.ReaderCreateRequest;
import com.library.reader.dto.ReaderUpdateRequest;
import com.library.reader.dto.ReaderVO;
import com.library.reader.service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 读者管理控制器（W10-W13）。
 *
 * @author DTCoder
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    /**
     * W10 新增读者。
     */
    @PostMapping
    public Result<Map<String, Long>> createReader(@Valid @RequestBody ReaderCreateRequest req) {
        Long id = readerService.createReader(req);
        return Result.success(Map.of("id", id));
    }

    /**
     * W11 删除读者。
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return Result.success();
    }

    /**
     * W12 修改读者。
     */
    @PutMapping("/{id}")
    public Result<Void> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderUpdateRequest req) {
        readerService.updateReader(id, req);
        return Result.success();
    }

    /**
     * W13 分页查询读者。
     */
    @GetMapping
    public Result<PageResult<ReaderVO>> pageReaders(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        IPage<ReaderVO> page = readerService.pageReaders(pageNum, pageSize);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }
}
