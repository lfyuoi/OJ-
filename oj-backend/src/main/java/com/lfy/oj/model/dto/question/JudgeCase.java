package com.lfy.oj.model.dto.question;

import lombok.Data;

/**
 * 判题用例
 *
 * @author 程序员灵风
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private  String input;

    /**
     * 输出用例
     */
    private  String output;

}
