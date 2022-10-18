package com.brecycle.controller;

import com.brecycle.common.Response;
import com.brecycle.config.shiro.JWTConfig;
import com.brecycle.config.shiro.JwtTokenUtil;
import com.brecycle.entity.dto.EntListDTO;
import com.brecycle.entity.dto.EntListParam;
import com.brecycle.entity.dto.PageResult;
import com.brecycle.service.EntService;
import com.google.common.collect.Lists;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 机构准入
 *
 * @author cmgun
 */
@Api(tags = "机构准入")
@RestController
@RequestMapping("ent")
public class EntController {

    @Autowired
    EntService entService;

    @ApiOperation("准入申请")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "AUTHORIZE_TOKEN", value = "AUTHORIZE_TOKEN", dataType = "String", required = true)
    })
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Response apply(@ApiParam(value = "上传的⽂件") @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        String token = request.getHeader(JWTConfig.tokenHeader);
        String userName = JwtTokenUtil.getUsername(token);
        entService.apply(Lists.newArrayList(file), userName);
        return Response.success("操作成功");
    }

    @ApiOperation("查询企业列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "AUTHORIZE_TOKEN", value = "AUTHORIZE_TOKEN", dataType = "String", required = true)
    })
    @PostMapping("/list")
    Response list(@RequestBody @ApiParam(value = "参数", required = true) EntListParam param) {
        PageResult<EntListDTO> result = entService.getEntList(param);
        return Response.success("查询成功", result);
    }


}
