# 架构设计文档

## 设计概述
- **需求名称**：挪车通知Android App（Parking Notification App）
- **设计版本**：v1.0
- **设计时间**：2026-06-29
- **设计依据**：REQ-PNA-001 需求文档

## 技术选型

| 层次 | 技术 | 说明 |
|------|------|------|
| **语言** | Kotlin 1.9+ | 官方推荐Android开发语言 |
| **UI框架** | Jetpack Compose + Material3 | 声明式UI，现代化Android UI开发 |
| **架构模式** | MVVM + Clean Architecture | 分层架构，关注点分离 |
| **依赖注入** | Hilt | Google官方DI框架 |
| **本地数据库** | Room | SQLite抽象层，官方推荐 |
| **异步** | Kotlin Coroutines + Flow | 响应式数据流 |
| **后台服务** | ForegroundService + WorkManager | 常驻后台+定期任务 |
| **导航** | Jetpack Navigation Compose | 页面路由 |
| **构建** | Gradle + Kotlin DSL | 标准Android构建 |
| **最小SDK** | API 26 (Android 8.0) | 覆盖95%以上设备 |
| **目标SDK** | API 34 (Android 14) | 最新稳定版 |

## 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────────┐  │
│  │ 通知项列表 │ │ 规则管理  │ │ 提醒历史  │ │ 全屏提醒关闭页  │  │
│  │  (主页面) │ │  页面    │ │  页面    │ │ (AlertActivity)│  │
│  └─────┬────┘ └────┬─────┘ └────┬─────┘ └───────┬────────┘  │
│        │            │            │               │           │
│  ┌─────┴────────────┴────────────┴───────────────┴────────┐  │
│  │                 ViewModel Layer                        │  │
│  │  NotificationVM  RuleVM  HistoryVM  AlertVM  SettingVM │  │
│  └─────────────────────────┬─────────────────────────────┘  │
├────────────────────────────┼────────────────────────────────┤
│                  Domain Layer (Use Cases)                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────────┐  │
│  │ 通知项管理 │ │ 规则管理  │ │ 规则匹配  │ │ 提醒管理       │  │
│  │  UseCase  │ │ UseCase  │ │  Engine  │ │  UseCase      │  │
│  └─────┬────┘ └────┬─────┘ └────┬─────┘ └───────┬────────┘  │
├────────────────────┼────────────┼────────────────┼──────────┤
│              Data Layer (Repository)                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  NotificationRepo  RuleRepo  HistoryRepo  AlertRepo  │   │
│  └──────────────────────┬───────────────────────────────┘   │
├─────────────────────────┼───────────────────────────────────┤
│              Data Source Layer (Room DB)                     │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  NotificationDao  RuleDao  HistoryDao  ItemRuleDao   │   │
│  └──────────────────────┬───────────────────────────────┘   │
│                         │                                    │
│  ┌──────────────────────┴───────────────────────────────┐   │
│  │           AppDatabase (Room + Encrypted)             │   │
│  └──────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│              Service Layer (Background)                      │
│  ┌──────────────────┐ ┌──────────────┐ ┌─────────────────┐  │
│  │ SmsListenerService│ │ RuleMatcher  │ │  AlertManager   │  │
│  │ (ForegroundService)│ │  Engine      │ │  (震动+铃声+通知)│  │
│  └──────────────────┘ └──────────────┘ └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 模块划分

### 1. UI Layer（表现层）
| 模块 | 组件 | 职责 |
|------|------|------|
| **主页面** | `NotificationListScreen` | 展示通知项卡片列表，FAB创建，底部导航 |
| **通知项编辑** | `NotificationEditScreen` | 创建/编辑通知项表单 |
| **规则管理** | `RuleListScreen` | 触发规则列表管理 |
| **规则编辑** | `RuleEditScreen` | 新增/编辑规则表单（手机号+内容关键词） |
| **提醒历史** | `HistoryListScreen` | 历史记录列表 |
| **全屏提醒** | `AlertFullScreenActivity` | 全屏关闭提醒页面（独立Activity） |
| **设置** | `SettingsScreen` | 服务控制、权限引导、版本信息 |

### 2. ViewModel Layer
| ViewModel | 关联UI | 主要职责 |
|-----------|--------|---------|
| `NotificationListVM` | 通知项列表 | 加载列表、切换启用/禁用 |
| `NotificationEditVM` | 通知项编辑 | 创建/编辑保存、规则关联、校验 |
| `RuleListVM` | 规则管理 | 规则CRUD |
| `RuleEditVM` | 规则编辑 | 规则创建/编辑表单逻辑 |
| `HistoryListVM` | 提醒历史 | 加载历史、删除/清空 |
| `AlertFullScreenVM` | 全屏提醒 | 关闭提醒、记录历史 |
| `SettingsVM` | 设置 | 服务状态、权限检测、厂商ROM识别 |

### 3. Domain Layer
| UseCase / Engine | 职责 |
|------------------|------|
| `CreateNotificationUseCase` | 创建通知项的业务逻辑+权限校验 |
| `UpdateNotificationUseCase` | 编辑通知项 |
| `DeleteNotificationUseCase` | 删除通知项+清理关联 |
| `ToggleNotificationUseCase` | 启用/禁用通知项 |
| `CreateRuleUseCase` | 创建触发规则 |
| `UpdateRuleUseCase` | 编辑触发规则 |
| `DeleteRuleUseCase` | 删除触发规则（校验是否被引用） |
| `MatchRuleEngine` | 短信匹配核心引擎：逐条匹配规则 |
| `TriggerAlertUseCase` | 触发提醒：震动+铃声+通知栏 |
| `DismissAlertUseCase` | 关闭提醒：停止震动+铃声+移除通知+记录历史 |
| `CheckPermissionUseCase` | 检测短信/通知/后台运行权限 |
| `CheckBackgroundRestrictionUseCase` | ROM检测+后台限制检测 |

### 4. Data Layer
| Repository | 数据来源 | 职责 |
|------------|---------|------|
| `NotificationRepository` | Room | 通知项CRUD |
| `RuleRepository` | Room | 触发规则CRUD |
| `HistoryRepository` | Room | 提醒历史CRUD |
| `AppSettingsRepository` | DataStore | 设置持久化 |

### 5. Service Layer（后台服务）
| 组件 | 类型 | 职责 |
|------|------|------|
| `SmsListenerService` | ForegroundService | 常驻后台，监听SMS广播 |
| `SmsContentObserver` | ContentObserver | Android 14+替代方案监听短信 |
| `RuleMatcherEngine` | 单例 | 短信匹配算法实现 |
| `AlertManager` | 单例 | 统一管理震动+铃声+通知栏 |

## 数据流设计

### 短信接收→提醒触发主流程
```
SMS BroadcastReceiver
    │
    ▼
SmsListenerService.onSmsReceived()
    │
    ▼
RuleMatcherEngine.match(sender, content)
    │ 遍历所有启用的NotificationItems
    │ 逐条检查关联的TriggerRules
    │ 手机号contains + 内容contains
    │
    ├── 无匹配 → 忽略
    │
    └── 匹配成功 →
         │
         ▼
    AlertManager.triggerAlert(itemId, sender, content, time)
         │
         ├── 震动: Vibrator.vibrate(VibrationEffect) 持续
         ├── 铃声: RingtoneManager.getRingtone().play() 循环
         └── 通知栏: NotificationManager.notify() ongoing通知
              │
              ▼
         AlertFullScreenActivity 启动 (new task, 全屏)
              │
              ▼
         用户点击"关闭提醒"按钮
              │
              ▼
    AlertManager.dismissAlert()
         │
         ├── 震动停止
         ├── 铃声停止
         ├── NotificationManager.cancel() 移除通知
         └── HistoryRepository.insert() 记录历史
```

### 用户创建通知项流程
```
NotificationEditScreen
    │
    ▼
NotificationEditVM.save()
    │
    ├── 检测短信权限? → 未授权 → 弹出权限引导弹窗
    ├── 检测通知权限? → 未授权 → 弹出权限引导弹窗
    │
    ▼
CreateNotificationUseCase.invoke()
    │
    ├── NotificationRepository.insert(item)
    ├── RuleRepository.insert(rules) 如新增规则
    └── ItemRuleRepository.insert(relations)
         │
         ▼
    导航返回列表页，显示新通知项
```

## 数据库设计

### 表结构

#### notification_items（通知项表）
```sql
CREATE TABLE notification_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    enable_vibration INTEGER NOT NULL DEFAULT 1,
    ringtone_uri TEXT,
    ringtone_name TEXT,
    enabled INTEGER NOT NULL DEFAULT 1,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```
- `enable_vibration`: 0=关闭, 1=开启长震动
- `ringtone_uri`: 系统铃声URI，null=系统默认
- `ringtone_name`: 铃声显示名称
- `enabled`: 0=禁用, 1=启用
- `created_at/updated_at`: Unix时间戳（毫秒）

#### trigger_rules（触发规则表）
```sql
CREATE TABLE trigger_rules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    phone_keyword TEXT NOT NULL,
    content_keyword TEXT NOT NULL,
    match_mode INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```
- `phone_keyword`: 短信发送号码模糊匹配关键词
- `content_keyword`: 短信内容模糊匹配关键词
- `match_mode`: 0=模糊匹配（当前默认）

#### notification_item_rules（通知项-规则关联表）
```sql
CREATE TABLE notification_item_rules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id INTEGER NOT NULL,
    rule_id INTEGER NOT NULL,
    FOREIGN KEY (item_id) REFERENCES notification_items(id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES trigger_rules(id) ON DELETE CASCADE,
    UNIQUE(item_id, rule_id)
);
```

#### notification_history（提醒历史表）
```sql
CREATE TABLE notification_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id INTEGER,
    sender_number TEXT NOT NULL,
    message_content TEXT NOT NULL,
    matched_rule_id INTEGER,
    triggered_at INTEGER NOT NULL,
    dismissed_at INTEGER,
    dismissed_by INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (item_id) REFERENCES notification_items(id) ON DELETE SET NULL,
    FOREIGN KEY (matched_rule_id) REFERENCES trigger_rules(id) ON DELETE SET NULL
);
```
- `dismissed_at`: 关闭时间，null=尚未关闭
- `dismissed_by`: 0=按钮关闭

### 索引策略
```sql
CREATE INDEX idx_item_rules_item_id ON notification_item_rules(item_id);
CREATE INDEX idx_item_rules_rule_id ON notification_item_rules(rule_id);
CREATE INDEX idx_history_triggered_at ON notification_history(triggered_at DESC);
CREATE INDEX idx_history_item_id ON notification_history(item_id);
```

### 数据库加密
- 使用 Room 集成 SQLCipher（`net.zetetic:android-database-sqlcipher`）
- 加密密钥存储在 Android Keystore 中
- 数据库文件路径：`context.getDatabasePath("parking_notification.db")`

## 安全设计

### 短信数据保护
| 措施 | 实现 |
|------|------|
| **本地存储加密** | SQLCipher + Android Keystore密钥管理 |
| **无网络传输** | app不声明任何网络权限 |
| **权限最小化** | 仅在用户创建通知项并启用后请求短信权限 |
| **数据清除** | 设置页提供"清除所有数据"选项 |

### 后台服务安全
- ForegroundService 使用低优先级通知（`IMPORTANCE_LOW`）作为保活手段
- 短信广播接收器使用 `manifest` 静态注册（Android 8.0+兼容）

## 权限设计

### 权限请求时机
```mermaid
graph TD
    A[用户点击保存通知项] --> B{已授予短信权限?}
    B -->|否| C[弹出短信权限引导弹窗]
    C --> D[用户点击"去设置"]
    D --> E[跳转系统设置页]
    E --> F[用户返回app]
    F --> B
    B -->|是| G{已授予通知权限?}
    G -->|否| H[弹出通知权限引导弹窗]
    H --> I[跳转系统设置页]
    I --> F
    G -->|是| J[保存通知项]

    K[app启动] --> L{后台运行已开启?}
    L -->|否| M[弹出后台引导弹窗]
    M --> N[展示厂商定制引导步骤]
    N --> O[用户点击"去设置"]
    O --> P[跳转对应设置页]
```

## 导航设计

### 页面路由（Navigation Compose）
```
NavGraph:
  ├── notification_list (默认首页)
  │     ├── → notification_edit/{itemId}? (创建/编辑)
  │     └── → rule_list (规则管理)
  │           └── → rule_edit/{ruleId}? (创建/编辑规则)
  ├── history_list (提醒历史)
  └── settings (设置页)
```

### 全屏提醒（独立Activity）
- `AlertFullScreenActivity` 在 AndroidManifest 中声明为：
  - `android:launchMode="singleInstance"`
  - `android:showWhenLocked="true"`
  - `android:turnScreenOn="true"`
  - `android:taskAffinity=""`
- 通过 `PendingIntent.getActivity()` 从通知栏点击或SMS广播启动

## 后台架构设计

### SmsListenerService 生命周期
```
App启动/用户开启服务
    │
    ▼
Context.startForegroundService()
    │
    ▼
SmsListenerService.onCreate()
    │
    ├── 创建 NotificationChannel("parking_service", "挪车服务")
    ├── 显示 ForegroundService 保活通知 (IMPORTANCE_LOW)
    ├── 注册 SmsReceiver (BroadcastReceiver)
    └── 注册 SmsContentObserver (Android 14+备用)
    │
    ▼
SmsListenerService.onStartCommand() → START_STICKY
    │
    ▼
[等待SMS广播] ◄─────── 系统发送 SMS_RECEIVED 广播
    │
    ▼
SmsReceiver.onReceive()
    │
    ├── 解析 sender + messageBody
    └── 发送给 RuleMatcherEngine 匹配
```

### 低功耗策略
| 策略 | 实现 |
|------|------|
| **Doze模式兼容** | 申请 `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` 白名单 |
| **厂商ROM适配** | 华为/小米/OPPO/vivo 各自的后台白名单引导 |
| **WorkManager定期检查** | 每15分钟周期性检查服务是否存活 |
| **AlarmManager保活** | 每30分钟弱心跳检测，防止系统杀死 |

## 界面组件树

```
App
├── MainActivity (SingleActivity)
│   ├── BottomNavigationBar
│   │   ├── Tab: 通知项 (NotificationListScreen)
│   │   ├── Tab: 触发规则 (RuleListScreen)
│   │   ├── Tab: 提醒历史 (HistoryListScreen)
│   │   └── Tab: 设置 (SettingsScreen)
│   │
│   ├── NotificationEditScreen (sheet or fullscreen)
│   │   ├── 名称输入
│   │   ├── 震动开关 + 铃声选择按钮 + 预览按钮
│   │   └── 已选规则列表 + 添加规则按钮
│   │
│   ├── RuleEditScreen (dialog or fullscreen)
│   │   ├── 手机号关键词输入
│   │   └── 内容关键词输入
│   │
│   └── SettingsScreen
│       ├── 服务运行状态指示 + 开关
│       ├── 权限状态检查入口
│       ├── 厂商后台引导入口
│       └── 版本信息
│
└── AlertFullScreenActivity (独立Activity)
    ├── 高亮背景 (红/橙色)
    ├── 短信详情: 发送号码、内容、时间
    └── 大按钮: "关闭提醒" (60%+宽度)
```

## 项目能力使用
（绿色字段项目，暂无已有能力可复用 — 全新建模）

## 性能设计

| 关注点 | 方案 |
|--------|------|
| **短信匹配延迟** | 匹配算法O(n)优化；Room查询使用索引；避免在主线程阻塞 |
| **内存占用** | 规则列表启动时一次性加载到内存缓存；避免重复查询DB |
| **震动/铃声控制** | 使用单例AlertManager统一管理资源；关闭时及时release |
| **列表性能** | Room + Paging 3 库实现历史记录分页加载 |
