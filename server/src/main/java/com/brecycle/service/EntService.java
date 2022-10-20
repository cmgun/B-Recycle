package com.brecycle.service;

import com.brecycle.entity.dto.*;
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

    /**
     * 审批通过
     * @param param 企业参数
     * @param currentUserName 当前用户
     */
    void accessPass(EntAccessPassParam param, String currentUserName) throws Exception;

    /**
     * 获取准入结果
     * @param userName
     * @return
     */
    AccessInfoDTO getAccessInfo(String userName) throws Exception ;
}
