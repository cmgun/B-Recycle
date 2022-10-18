package com.brecycle.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 企业准入
 */
public interface EntService {

    /**
     * 准入申请
     * @param files
     */
    void apply(List<MultipartFile> files, String userName) throws Exception;
}
