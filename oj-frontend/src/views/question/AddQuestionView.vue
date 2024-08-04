<template>
  <div id="addQuestionView" class="apple-style">
    <h2 class="apple-style-title">创建题目</h2>
    <a-form :model="form" label-align="left" class="apple-style-form">
      <a-form-item field="title" label="标题" class="apple-style-form-item">
        <a-input
          v-model="form.title"
          placeholder="请输入标题"
          class="apple-style-input"
        />
      </a-form-item>
      <a-form-item field="tags" label="标签" class="apple-style-form-item">
        <a-input-tag
          v-model="form.tags"
          placeholder="请选择标签"
          allow-clear
          class="apple-style-input-tag"
        />
      </a-form-item>
      <a-form-item
        field="content"
        label="题目内容"
        class="apple-style-form-item"
      >
        <MdEditor
          :value="form.content"
          :handle-change="onContentChange"
          class="apple-style-md-editor"
        />
      </a-form-item>
      <a-form-item field="answer" label="答案" class="apple-style-form-item">
        <MdEditor
          :value="form.answer"
          :handle-change="onAnswerChange"
          class="apple-style-md-editor"
        />
      </a-form-item>
      <a-form-item
        label="判题配置"
        :content-flex="false"
        :merge-props="false"
        class="apple-style-form-item"
      >
        <a-space
          direction="vertical"
          style="min-width: 480px"
          class="apple-style-space"
        >
          <a-form-item
            field="judgeConfig.timeLimit"
            label="时间限制"
            class="apple-style-form-item"
          >
            <a-input-number
              v-model="form.judgeConfig.timeLimit"
              placeholder="请输入时间限制"
              mode="button"
              min="0"
              size="large"
              class="apple-style-input-number"
            />
          </a-form-item>
          <a-form-item
            field="judgeConfig.memoryLimit"
            label="内存限制"
            class="apple-style-form-item"
          >
            <a-input-number
              v-model="form.judgeConfig.memoryLimit"
              placeholder="请输入内存限制"
              mode="button"
              min="0"
              size="large"
              class="apple-style-input-number"
            />
          </a-form-item>
          <a-form-item
            field="judgeConfig.stackLimit"
            label="堆栈限制"
            class="apple-style-form-item"
          >
            <a-input-number
              v-model="form.judgeConfig.stackLimit"
              placeholder="请输入堆栈限制"
              mode="button"
              min="0"
              size="large"
              class="apple-style-input-number"
            />
          </a-form-item>
        </a-space>
      </a-form-item>
      <a-form-item
        label="测试用例配置"
        :content-flex="false"
        :merge-props="false"
        class="apple-style-form-item"
      >
        <a-form-item
          v-for="(judgeCaseItem, index) of form.judgeCase"
          :key="index"
          no-style
          class="apple-style-form-item"
        >
          <a-space
            direction="vertical"
            style="min-width: 640px"
            class="apple-style-space"
          >
            <a-form-item
              :field="`form.judgeCase[${index}].input`"
              :label="`输入用例-${index}`"
              :key="index"
              class="apple-style-form-item"
            >
              <a-input
                v-model="judgeCaseItem.input"
                placeholder="请输入测试输入用例"
                class="apple-style-input"
              />
            </a-form-item>
            <a-form-item
              :field="`form.judgeCase[${index}].output`"
              :label="`输出用例-${index}`"
              :key="index"
              class="apple-style-form-item"
            >
              <a-input
                v-model="judgeCaseItem.output"
                placeholder="请输入测试输出用例"
                class="apple-style-input"
              />
            </a-form-item>
            <a-button
              status="danger"
              @click="handleDelete(index)"
              class="apple-style-button"
            >
              删除
            </a-button>
          </a-space>
        </a-form-item>
        <div style="margin-top: 32px" class="apple-style-space">
          <a-button
            @click="handleAdd"
            type="outline"
            status="success"
            class="apple-style-button"
          >
            新增测试用例
          </a-button>
        </div>
      </a-form-item>
      <div style="margin-top: 16px" class="apple-style-space" />
      <a-form-item class="apple-style-form-item">
        <a-button
          type="primary"
          style="min-width: 200px"
          @click="doSubmit"
          class="apple-style-button"
        >
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute } from "vue-router";

const route = useRoute();
// 如果页面地址包含 update，视为更新页面
const updatePage = route.path.includes("update");

let form = ref({
  title: "",
  tags: [],
  answer: "",
  content: "",
  judgeConfig: {
    memoryLimit: 1000,
    stackLimit: 1000,
    timeLimit: 1000,
  },
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
});

/**
 * 根据题目 id 获取老的数据
 */
const loadData = async () => {
  const id = route.query.id;
  if (!id) {
    return;
  }
  const res = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  if (res.code === 0) {
    form.value = res.data as any;
    // json 转 js 对象
    if (!form.value.judgeCase) {
      form.value.judgeCase = [
        {
          input: "",
          output: "",
        },
      ];
    } else {
      form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
    }
    if (!form.value.judgeConfig) {
      form.value.judgeConfig = {
        memoryLimit: 1000,
        stackLimit: 1000,
        timeLimit: 1000,
      };
    } else {
      form.value.judgeConfig = JSON.parse(form.value.judgeConfig as any);
    }
    if (!form.value.tags) {
      form.value.tags = [];
    } else {
      form.value.tags = JSON.parse(form.value.tags as any);
    }
  } else {
    message.error("加载失败，" + res.message);
  }
};

onMounted(() => {
  loadData();
});

const doSubmit = async () => {
  console.log(form.value);
  // 区分更新还是创建
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("更新成功");
    } else {
      message.error("更新失败，" + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("创建成功");
    } else {
      message.error("创建失败，" + res.message);
    }
  }
};

/**
 * 新增判题用例
 */
const handleAdd = () => {
  form.value.judgeCase.push({
    input: "",
    output: "",
  });
};

/**
 * 删除判题用例
 */
const handleDelete = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};

const onContentChange = (value: string) => {
  form.value.content = value;
};

const onAnswerChange = (value: string) => {
  form.value.answer = value;
};
</script>

<style scoped>
.apple-style {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
  color: #333;
  background-color: #fff;
  padding: 20px;
}

.apple-style-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.apple-style-form {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
}

.apple-style-form-item {
  margin-bottom: 20px;
}

.apple-style-input {
  width: 100%;
}

.apple-style-input-tag {
  width: 100%;
}

.apple-style-md-editor {
  width: 100%;
}

.apple-style-space {
  display: flex;
  justify-content: space-between;
}

.apple-style-input-number {
  width: 100%;
}

.apple-style-button {
  min-width: 200px;
}
</style>
