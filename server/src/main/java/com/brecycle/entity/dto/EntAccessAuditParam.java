package com.brecycle.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cmgun
 */
@Data
public class EntAccessAuditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String remark;
}
