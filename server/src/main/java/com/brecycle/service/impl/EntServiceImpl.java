package com.brecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.brecycle.controller.hanlder.BusinessException;
import com.brecycle.entity.EntInfo;
import com.brecycle.entity.MongoFile;
import com.brecycle.entity.User;
import com.brecycle.entity.UserFile;
import com.brecycle.enums.AccessStatus;
import com.brecycle.mapper.EntInfoMapper;
import com.brecycle.mapper.UserFileMapper;
import com.brecycle.mapper.UserMapper;
import com.brecycle.service.EntService;
import com.brecycle.service.MongoFileRepository;
import com.brecycle.service.UserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cmgun
 */
@Slf4j
@Service
public class EntServiceImpl implements EntService {

    @Autowired
    MongoFileRepository mongoFileRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserFileMapper userFileMapper;
    @Autowired
    EntInfoMapper entInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(List<MultipartFile> files, String userName) throws Exception {
        // 查询当前用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
        if (user == null) {
            throw new BusinessException("用户信息不存在");
        }
        // 文件上传
        List<String> fileIds = Lists.newArrayList();
        for (MultipartFile file : files) {
            MongoFile mongoFile = uploadFile(file, userName);
            fileIds.add(mongoFile.getId());
        }
        // 保存文件关联
        List<UserFile> userFiles = fileIds.stream().map(e -> {
            UserFile userFile = new UserFile();
            userFile.setUserId(user.getId());
            userFile.setFileId(e);
            return userFile;
        }).collect(Collectors.toList());
        userFiles.forEach(e -> userFileMapper.insert(e));
        // 更新准入状态
        EntInfo entInfo = new EntInfo();
        entInfo.setAccessStatus(AccessStatus.AUDIT.getValue());
        entInfoMapper.update(entInfo, new LambdaUpdateWrapper<EntInfo>().eq(EntInfo::getUserId, user.getId()));
    }

    public MongoFile uploadFile(MultipartFile file, String userId) throws Exception {
        if (file.getSize() > 16777216) {
            return this.saveGridFsFile(file, userId);
        } else {
            return this.saveBinaryFile(file, userId);
        }
    }

    /**
     * 保存GridFs文件（大文件）
     * @param file
     * @return
     * @throws Exception
     */
    private MongoFile saveGridFsFile(MultipartFile file, String userName) throws Exception {
        String suffix = getFileSuffix(file);
        String fileName = userName + "_" + file.getOriginalFilename();
        gridFsTemplate.store(file.getInputStream(), fileName, file.getContentType());
        String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());

        MongoFile mongoFile = mongoTemplate.save(
                MongoFile.builder()
                        .fileName(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .uploadDate(new Date())
                        .suffix(suffix)
                        .md5(md5)
                        .gridFsId(fileName)
                        .build()
        );

        return mongoFile;
    }

    /**
     * 保存Binary文件（小文件）
     * @param file
     * @return
     * @throws Exception
     */
    public MongoFile saveBinaryFile(MultipartFile file, String userName) throws Exception {
        String suffix = getFileSuffix(file);
        String fileName = userName + "_" + file.getOriginalFilename();
        String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());

        MongoFile mongoFile = mongoFileRepository.save(
                MongoFile.builder()
                        .fileName(fileName)
                        .fileSize(file.getSize())
                        .content(new Binary(file.getBytes()))
                        .contentType(file.getContentType())
                        .uploadDate(new Date())
                        .suffix(suffix)
                        .md5(md5)
                        .build()
        );

        return mongoFile;
    }

    /**
     * 获取文件后缀
     * @param file
     * @return
     */
    private String getFileSuffix(MultipartFile file) {
        String suffix = "";
        if (Objects.requireNonNull(file.getOriginalFilename()).contains(".")) {
            suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        }
        return suffix;
    }
}
