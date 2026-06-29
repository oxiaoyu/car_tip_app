# SDD 工作空间

## 目录结构

```
ai_workspace/
│
├── code-specification/          # 代码库规范
│   └── 定义代码库级别的规范要求
│
├── extends/                     # 自定义规则扩展文件
│   ├── code_review_checklist.md # 代码审查规则
│   ├── design_analysis.md       # 设计分析规则
│   ├── impl_requirement.md      # 实现需求规则
│   └── test_requirement.md      # 测试需求规则
│
├── project-how-to/              # 能力规范
│   ├── abilities-index.md       # 能力索引
│   └── abilities/               # 能力规范内容
│
├── requirements/                # 需求任务文档
│
├── working_requirements_status.md  # 工作需求状态跟踪
│
└── README.md                    # 本文件
```

---

## SDD 三阶段协作流程

```
用户需求
   │
   ▼
┌────────────────────────┐
│ 需求规范分析 Skill     │  →  requirement.md (需求规范)
│ SDD-requirement-analysis│
└───────────┬────────────┘
            │ 用户确认
            ▼
┌────────────────────────┐
│ 设计规范分析 Skill     │  →  design.md (设计规范)
│ SDD-design-analysis    │  →  tasks.md (任务规范)
└───────────┬────────────┘
            │ 用户确认
            ▼
┌────────────────────────┐
│ 实现测试审查 Skill     │  →  代码实现 + 测试 + 审查报告
│ SDD-implementation-   │
│    test-review        │
└───────────┬────────────┘
            │ 用户确认
            ▼
         ✅ 完成
```

## 注意事项

- 每个阶段完成后需要用户确认才能进入下一阶段
- 所有文档都应存放在 `ai_workspace` 目录下
- 使用 `working_requirements_status.md` 跟踪工作进度
