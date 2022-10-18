package com.brecycle.service;

import com.brecycle.entity.dto.EntListDTO;
import com.brecycle.entity.dto.EntListParam;
import com.brecycle.entity.dto.PageResult;
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

    /**
     * 分页查询企业列表
     * @return
     */
    PageResult<EntListDTO> getEntList(EntListParam param);
}
