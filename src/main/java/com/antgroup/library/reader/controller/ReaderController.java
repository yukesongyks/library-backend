package com.antgroup.library.reader.controller;

import com.antgroup.library.common.response.PageResult;
import com.antgroup.library.common.response.Result;
import com.antgroup.library.reader.dto.ReaderCreateRequest;
import com.antgroup.library.reader.dto.ReaderQueryRequest;
import com.antgroup.library.reader.dto.ReaderUpdateRequest;
import com.antgroup.library.reader.dto.ReaderVO;
import com.antgroup.library.reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 读者管理接口（W06-W10）。
 */
@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping
    public Result<PageResult<ReaderVO>> queryReaders(ReaderQueryRequest request) {
        return Result.success(readerService.queryReaders(request));
    }

    @GetMapping("/{id}")
    public Result<ReaderVO> getReaderById(@PathVariable Long id) {
        return Result.success(readerService.getReaderById(id));
    }

    @PostMapping
    public Result<Long> createReader(@Valid @RequestBody ReaderCreateRequest request) {
        return Result.success(readerService.createReader(request));
    }

    @PutMapping("/{id}")
    public Result<Void> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderUpdateRequest request) {
        readerService.updateReader(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return Result.success();
    }
}
