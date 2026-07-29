package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.model.dto.ReaderRequest;
import com.library.model.dto.ReaderVO;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 读者 Controller
 *
 * @author library
 */
@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    /**
     * 分页查询读者
     */
    @GetMapping
    public Result<PageResult<ReaderVO>> searchReaders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResult<ReaderVO> page = readerService.searchReaders(keyword, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 查询全部读者(不分页, 供下拉选择)
     */
    @GetMapping("/all")
    public Result<List<ReaderVO>> listAllReaders() {
        return Result.success(readerService.listAllReaders());
    }

    /**
     * 查询读者详情
     */
    @GetMapping("/{id}")
    public Result<ReaderVO> getReaderById(@PathVariable Long id) {
        return Result.success(readerService.getReaderById(id));
    }

    /**
     * 新增读者
     */
    @PostMapping
    public Result<Long> addReader(@Valid @RequestBody ReaderRequest request) {
        return Result.success(readerService.addReader(request));
    }

    /**
     * 更新读者
     */
    @PutMapping("/{id}")
    public Result<Void> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderRequest request) {
        readerService.updateReader(id, request);
        return Result.success();
    }

    /**
     * 删除读者
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return Result.success();
    }
}
