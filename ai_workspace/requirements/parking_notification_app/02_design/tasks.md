# 任务列表

## 任务概览
- **总任务数**：14
- **预计工时**：~32小时
- **依赖关系**：T001→T002→T003→T004→T005→T006→T007,T008→T012,T009,T010→T011,T013→T014

## 任务列表

### Phase 1: 项目基建 & 数据层

#### Task 1: 项目脚手架搭建
- **ID**：T001
- **描述**：初始化Android项目结构，配置Gradle依赖，搭建Clean Architecture包结构
- **类型**：infrastructure
- **优先级**：High
- **预计工时**：1h
- **依赖**：无
- **状态**：Pending
- **产物**：
  - `build.gradle.kts`（project + app）
  - `settings.gradle.kts`
  - 包结构：`com.parking.notification/{data,domain,ui,service,di}`
  - Hilt DI基础配置

#### Task 2: Room数据库层实现
- **ID**：T002
- **描述**：创建所有Entity、DAO、Database类，配置SQLCipher加密
- **类型**：database
- **优先级**：High
- **预计工时**：2h
- **依赖**：T001
- **状态**：Pending
- **产物**：
  - `NotificationItemEntity.kt`
  - `TriggerRuleEntity.kt`
  - `NotificationItemRuleEntity.kt`
  - `NotificationHistoryEntity.kt`
  - `NotificationDao.kt`
  - `RuleDao.kt`
  - `ItemRuleDao.kt`
  - `HistoryDao.kt`
  - `AppDatabase.kt`（含SQLCipher加密）
  - `Converters.kt`（TypeConverter）

#### Task 3: Repository层实现
- **ID**：T003
- **描述**：实现所有Repository接口和实现类，封装DAO操作
- **类型**：data
- **优先级**：High
- **预计工时**：2h
- **依赖**：T002
- **状态**：Pending
- **产物**：
  - `NotificationRepository.kt`（接口+实现）
  - `RuleRepository.kt`（接口+实现）
  - `HistoryRepository.kt`（接口+实现）
  - Repository Module DI配置

### Phase 2: 领域层 & Service层

#### Task 4: 规则匹配引擎实现
- **ID**：T004
- **描述**：实现RuleMatcherEngine核心匹配算法，支持多规则AND/OR逻辑
- **类型**：domain
- **优先级**：High
- **预计工时**：2h
- **依赖**：T003
- **状态**：Pending
- **产物**：
  - `MatchResult.kt`（匹配结果数据类）
  - `RuleMatcherEngine.kt`（核心匹配算法）
  - `RuleMatcherEngineTest.kt`（单元测试：AC2/AC3/AC4/AC13）

#### Task 5: UseCase层实现
- **ID**：T005
- **描述**：实现所有UseCase业务逻辑
- **类型**：domain
- **优先级**：High
- **预计工时**：3h
- **依赖**：T003
- **状态**：Pending
- **产物**：
  - `CreateNotificationUseCase.kt`（含权限校验逻辑）
  - `UpdateNotificationUseCase.kt`
  - `DeleteNotificationUseCase.kt`
  - `ToggleNotificationUseCase.kt`
  - `CreateRuleUseCase.kt`
  - `UpdateRuleUseCase.kt`
  - `DeleteRuleUseCase.kt`
  - `TriggerAlertUseCase.kt`
  - `DismissAlertUseCase.kt`
  - `CheckPermissionUseCase.kt`
  - `CheckBackgroundRestrictionUseCase.kt`

#### Task 6: 后台SmsListenerService实现
- **ID**：T006
- **描述**：实现ForegroundService，注册SMS广播监听，调用规则匹配引擎
- **类型**：service
- **优先级**：High
- **预计工时**：3h
- **依赖**：T004
- **状态**：Pending
- **产物**：
  - `SmsListenerService.kt`（ForegroundService）
  - `SmsReceiver.kt`（BroadcastReceiver，AndroidManifest静态注册）
  - `SmsContentObserver.kt`（Android 14+备用方案）
  - `AndroidManifest.xml` 服务声明配置
  - 厂商ROM检测工具类 `RomUtils.kt`

#### Task 7: AlertManager提醒管理器实现
- **ID**：T007
- **描述**：实现震动、铃声、通知栏常驻通知的统一管理
- **类型**：service
- **优先级**：High
- **预计工时**：2h
- **依赖**：T006
- **状态**：Pending
- **产物**：
  - `AlertManager.kt`（震动+铃声+通知协调）
  - `NotificationChannels.kt`（通知渠道初始化）
  - `AlertNotificationBuilder.kt`（通知栏通知构建）
  - `AlertManagerTest.kt`（单元测试：AC5/AC7）

### Phase 3: UI层

#### Task 8: 底部导航+主页框架
- **ID**：T008
- **描述**：实现MainActivity骨架，底部导航栏，页面路由配置
- **类型**：ui
- **优先级**：High
- **预计工时**：2h
- **依赖**：T005
- **状态**：Pending
- **产物**：
  - `MainActivity.kt`
  - `AppNavigation.kt`（NavGraph路由定义）
  - `BottomNavBar.kt`
  - `MainScreen.kt`（Scaffold + 导航宿主）
  - `theme/`（主题配置）

#### Task 9: 通知项列表+编辑页实现
- **ID**：T009
- **描述**：实现通知项卡片列表页、创建/编辑页面UI及ViewModel
- **类型**：ui
- **优先级**：High
- **预计工时**：3h
- **依赖**：T008
- **状态**：Pending
- **产物**：
  - `NotificationListScreen.kt`
  - `NotificationListVM.kt`
  - `NotificationEditScreen.kt`
  - `NotificationEditVM.kt`
  - `NotificationCard.kt`（列表项组件）
  - `RingtonePickerDialog.kt`（铃声选择器封装）

#### Task 10: 触发规则管理页实现
- **ID**：T010
- **描述**：实现规则列表页、创建/编辑规则页面UI及ViewModel
- **类型**：ui
- **优先级**：High
- **预计工时**：2h
- **依赖**：T008
- **状态**：Pending
- **产物**：
  - `RuleListScreen.kt`
  - `RuleListVM.kt`
  - `RuleEditScreen.kt`
  - `RuleEditVM.kt`
  - `RuleItem.kt`（列表项组件）

#### Task 11: 全屏提醒关闭页实现
- **ID**：T011
- **描述**：实现全屏提醒关闭页Activity，大按钮交互，联动AlertManager
- **类型**：ui
- **优先级**：High
- **预计工时**：2h
- **依赖**：T007, T010
- **状态**：Pending
- **产物**：
  - `AlertFullScreenActivity.kt`
  - `AlertFullScreenVM.kt`
  - `AlertFullScreenContent.kt`（Compose内容）
  - `AndroidManifest.xml` Activity声明（showWhenLocked）

#### Task 12: 提醒历史页实现
- **ID**：T012
- **描述**：实现提醒历史列表页UI及ViewModel，支持单删和批量清空
- **类型**：ui
- **优先级**：Medium
- **预计工时**：2h
- **依赖**：T008
- **状态**：Pending
- **产物**：
  - `HistoryListScreen.kt`
  - `HistoryListVM.kt`
  - `HistoryItem.kt`（列表项组件）
  - `DeleteConfirmDialog.kt`

#### Task 13: 设置页 + 权限引导实现
- **ID**：T013
- **描述**：实现设置页UI，权限检测引导弹窗、厂商后台引导、服务控制
- **类型**：ui
- **优先级**：High
- **预计工时**：3h
- **依赖**：T008
- **状态**：Pending
- **产物**：
  - `SettingsScreen.kt`
  - `SettingsVM.kt`
  - `PermissionGuideDialog.kt`（权限引导弹窗）
  - `BackgroundGuideDialog.kt`（后台引导弹窗，含厂商适配内容）
  - `RomBackgroundGuideProvider.kt`（各ROM引导步骤数据）

#### Task 14: 后台保活 + 厂商适配引导
- **ID**：T014
- **描述**：实现WorkManager周期性保活检查，厂商ROM后台白名单引导配置
- **类型**：service
- **优先级**：Medium
- **预计工时**：2h
- **依赖**：T013
- **状态**：Pending
- **产物**：
  - `ServiceKeepAliveWorker.kt`（WorkManager定期检查）
  - `BatteryOptimizationHelper.kt`（电池优化白名单申请）
  - ROM引导数据配置（华为/小米/OPPO/vivo）

## 依赖关系图
```
T001 (项目基建)
  │
  ├──→ T002 (Room数据库)
  │       │
  │       └──→ T003 (Repository)
  │               │
  │               ├──→ T004 (匹配引擎)
  │               │       │
  │               │       └──→ T006 (SmsListenerService)
  │               │               │
  │               │               └──→ T007 (AlertManager)
  │               │                       │
  │               │                       └──→ T011 (全屏提醒页) ←─ T010
  │               │
  │               └──→ T005 (UseCases)
  │                       │
  │                       └──→ T008 (底部导航+主页)
  │                               │
  │                               ├──→ T009 (通知项列表/编辑)
  │                               ├──→ T010 (规则管理页) ──→ T011
  │                               ├──→ T012 (提醒历史页)
  │                               └──→ T013 (设置页)
  │                                       │
  │                                       └──→ T014 (后台保活)
```

## 执行顺序建议

### 串行路径（须按顺序）
```
T001 → T002 → T003 → T004 → T006 → T007
                                            ↘
T001 → T002 → T003 → T005 → T008 → T010 → T011
                                  T008 → T009
                                  T008 → T012
                                  T008 → T013 → T014
```

### 可并行任务
| 组 | 任务 | 说明 |
|----|------|------|
| A | T009, T010, T012, T013 | T008完成后，这4个UI任务完全独立 |
| B | T004, T005 | T003完成后，匹配引擎和UseCase可并行开发 |

### 推荐开发顺序
1. **T001** — 脚手架（1h）
2. **T002 + T003** — 数据层（4h，串行）
3. **T004 + T005** — 领域层（5h，并行）
4. **T006 + T007** — 服务层（5h，串行）
5. **T008** — 导航骨架（2h）
6. **T009 + T010 + T012 + T013** — UI页面（10h，并行）
7. **T011** — 全屏提醒页（2h，依赖T007+T010）
8. **T014** — 后台保活（2h，依赖T013）
