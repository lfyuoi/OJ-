package com.lfy.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lfy.oj.model.dto.question.JudgeConfig;
import com.lfy.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 *
 * @author 程序员灵风
 * @TableName question
 */
@TableName(value = "question")
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 创建题目人信息
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        // 判断传入的包装类是否为空
        if (questionVO == null) {
            return null;
        }
        // 创建一个新的对象
        Question question = new Question();
        // 将包装类的属性复制到新对象中
        BeanUtils.copyProperties(questionVO, question);
        // 获取包装类中的标签列表
        List<String> tagList = questionVO.getTags();
        // 判断标签列表是否为空
        if (tagList != null) {
            // 将标签列表转换为JSON字符串并设置到新对象中
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        // 获取包装类中的判断配置
        JudgeConfig judgeConfig = questionVO.getJudgeConfig();
        // 判断判断配置是否为空
        if (judgeConfig != null) {
            // 将判断配置转换为JSON字符串并设置到新对象中
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 返回新对象
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        // 判断传入的对象是否为空
        if (question == null) {
            return null;
        }
        // 创建一个新的包装类
        QuestionVO questionVO = new QuestionVO();
        // 将对象的属性复制到新包装类中
        BeanUtils.copyProperties(question, questionVO);
        // 将对象的标签字符串转换为列表并设置到新包装类中
        List<String> taglist = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(taglist);
        // 将对象的判断配置字符串转换为对象并设置到新包装类中
        String judgeConfigStr = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        // 返回新包装类
        return questionVO;
    }

    private static final long serialVersionUID = 1L;
}