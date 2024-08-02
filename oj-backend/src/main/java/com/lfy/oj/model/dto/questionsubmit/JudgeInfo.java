package com.lfy.oj.model.dto.questionsubmit;

import lombok.Data;

/**
 * 判题信息
 *
 * @author 程序员灵风
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 堆栈限制（KB）
     */
    private Long time;
}
