package com.lfy.oj.judge;

import com.lfy.oj.judge.strategy.DefaultJudgeStrategy;
import com.lfy.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.lfy.oj.judge.strategy.JudgeContext;
import com.lfy.oj.judge.strategy.JudgeStrategy;
import com.lfy.oj.model.dto.questionsubmit.JudgeInfo;
import com.lfy.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
