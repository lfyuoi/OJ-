package com.lfy.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.oj.common.ErrorCode;
import com.lfy.oj.constant.CommonConstant;
import com.lfy.oj.exception.BusinessException;
import com.lfy.oj.exception.ThrowUtils;
import com.lfy.oj.mapper.QuestionMapper;
import com.lfy.oj.model.dto.question.QuestionQueryRequest;
import com.lfy.oj.model.entity.Question;
import com.lfy.oj.model.entity.User;
import com.lfy.oj.model.vo.QuestionVO;
import com.lfy.oj.model.vo.UserVO;
import com.lfy.oj.service.QuestionService;
import com.lfy.oj.service.UserService;
import com.lfy.oj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 灵风
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2024-08-01 14:19:28
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    /**
     * 获取题目信息
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 将Question对象转换为QuestionVO对象
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 关联查询用户信息
        // 获取问题所属用户的ID
        Long userId = question.getUserId();
        // 初始化用户对象
        User user = null;
        // 如果用户ID不为空且大于0，则查询用户信息
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        // 将用户信息转换为UserVO对象
        UserVO userVO = userService.getUserVO(user);
        // 将UserVO对象设置到QuestionVO对象中
        questionVO.setUserVO(userVO);
        // 返回QuestionVO对象
        return questionVO;
    }

    /**
     * 获取题目分页信息
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        // 获取问题列表
        List<Question> questionList = questionPage.getRecords();
        // 初始化QuestionVO分页对象
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        // 如果问题列表为空，则直接返回QuestionVO分页对象
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 关联查询用户信息
        // 获取问题列表中所有用户的ID
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        // 根据用户ID查询用户列表，并按照用户ID进行分组
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 填充信息
        // 将问题列表转换为QuestionVO列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            // 将Question对象转换为QuestionVO对象
            QuestionVO questionVO = QuestionVO.objToVo(question);
            // 获取问题所属用户的ID
            Long userId = question.getUserId();
            // 初始化用户对象
            User user = null;
            // 如果用户ID存在于用户列表中，则获取用户对象
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            // 将用户信息转换为UserVO对象，并设置到QuestionVO对象中
            questionVO.setUserVO(userService.getUserVO(user));
            // 返回QuestionVO对象
            return questionVO;
        }).collect(Collectors.toList());
        // 将QuestionVO列表设置到QuestionVO分页对象中
        questionVOPage.setRecords(questionVOList);
        // 返回QuestionVO分页对象
        return questionVOPage;
    }

}