# 差异分析报告

## 分析概述
- **分析时间**：2026-06-29
- **分析范围**：全项目（绿色字段项目，无现有代码）
- **分析方法**：基于需求文档的静态分析

## 项目状态
- **项目类型**：新建Android App（Greenfield）
- **现有代码**：无
- **需要创建**：完整Android项目

## 文件变更清单

### 新增文件（完整项目）

#### 构建配置
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `build.gradle.kts` | 构建 | 项目级Gradle配置 |
| `app/build.gradle.kts` | 构建 | App模块Gradle配置（含Room/Hilt/Compose依赖） |
| `settings.gradle.kts` | 构建 | 项目设置 |
| `gradle.properties` | 构建 | Gradle属性 |
| `local.properties` | 构建 | 本地SDK路径 |

#### Android清单
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `app/src/main/AndroidManifest.xml` | 配置 | 权限声明、Service/Activity/Receiver注册 |

#### 数据层
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `data/entity/NotificationItemEntity.kt` | Entity | 通知项实体 |
| `data/entity/TriggerRuleEntity.kt` | Entity | 触发规则实体 |
| `data/entity/NotificationItemRuleEntity.kt` | Entity | 关联表实体 |
| `data/entity/NotificationHistoryEntity.kt` | Entity | 提醒历史实体 |
| `data/dao/NotificationDao.kt` | DAO | 通知项数据访问 |
| `data/dao/RuleDao.kt` | DAO | 规则数据访问 |
| `data/dao/ItemRuleDao.kt` | DAO | 关联表数据访问 |
| `data/dao/HistoryDao.kt` | DAO | 历史数据访问 |
| `data/database/AppDatabase.kt` | Database | Room数据库+SQLCipher |
| `data/repository/NotificationRepository.kt` | Repository | 通知项仓库 |
| `data/repository/RuleRepository.kt` | Repository | 规则仓库 |
| `data/repository/HistoryRepository.kt` | Repository | 历史仓库 |
| `data/repository/AppSettingsRepository.kt` | Repository | 设置仓库 |

#### 领域层
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `domain/usecase/notification/CreateNotificationUseCase.kt` | UseCase | 创建通知项 |
| `domain/usecase/notification/UpdateNotificationUseCase.kt` | UseCase | 编辑通知项 |
| `domain/usecase/notification/DeleteNotificationUseCase.kt` | UseCase | 删除通知项 |
| `domain/usecase/notification/ToggleNotificationUseCase.kt` | UseCase | 启用/禁用 |
| `domain/usecase/rule/CreateRuleUseCase.kt` | UseCase | 创建规则 |
| `domain/usecase/rule/UpdateRuleUseCase.kt` | UseCase | 编辑规则 |
| `domain/usecase/rule/DeleteRuleUseCase.kt` | UseCase | 删除规则 |
| `domain/usecase/alert/TriggerAlertUseCase.kt` | UseCase | 触发提醒 |
| `domain/usecase/alert/DismissAlertUseCase.kt` | UseCase | 关闭提醒 |
| `domain/usecase/permission/CheckPermissionUseCase.kt` | UseCase | 权限检测 |
| `domain/usecase/permission/CheckBackgroundRestrictionUseCase.kt` | UseCase | 后台限制检测 |
| `domain/engine/RuleMatcherEngine.kt` | Engine | 规则匹配核心引擎 |
| `domain/model/MatchResult.kt` | Model | 匹配结果数据模型 |

#### 服务层
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `service/SmsListenerService.kt` | Service | ForegroundService后台监听 |
| `service/SmsReceiver.kt` | Receiver | SMS广播接收器 |
| `service/SmsContentObserver.kt` | Observer | Android 14+短信监听替代方案 |
| `service/alert/AlertManager.kt` | Manager | 提醒管理（震动+铃声+通知） |
| `service/alert/NotificationChannels.kt` | Config | 通知渠道初始化 |
| `service/alert/AlertNotificationBuilder.kt` | Builder | 通知构建 |
| `service/keepalive/ServiceKeepAliveWorker.kt` | Worker | WorkManager保活 |
| `service/keepalive/BatteryOptimizationHelper.kt` | Helper | 电池优化白名单 |
| `service/rom/RomUtils.kt` | Util | 厂商ROM检测 |
| `service/rom/RomBackgroundGuideProvider.kt` | Provider | 各ROM后台引导配置 |

#### UI层
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `ui/MainActivity.kt` | Activity | 主Activity（SingleActivity） |
| `ui/AlertFullScreenActivity.kt` | Activity | 全屏提醒Activity |
| `ui/navigation/AppNavigation.kt` | Navigation | 页面路由 |
| `ui/navigation/BottomNavBar.kt` | Component | 底部导航栏 |
| `ui/screen/notification/NotificationListScreen.kt` | Screen | 通知项列表 |
| `ui/screen/notification/NotificationListVM.kt` | ViewModel | 通知项列表VM |
| `ui/screen/notification/NotificationEditScreen.kt` | Screen | 通知项编辑 |
| `ui/screen/notification/NotificationEditVM.kt` | ViewModel | 通知项编辑VM |
| `ui/screen/notification/NotificationCard.kt` | Component | 通知项卡片 |
| `ui/screen/rule/RuleListScreen.kt` | Screen | 规则列表 |
| `ui/screen/rule/RuleListVM.kt` | ViewModel | 规则列表VM |
| `ui/screen/rule/RuleEditScreen.kt` | Screen | 规则编辑 |
| `ui/screen/rule/RuleEditVM.kt` | ViewModel | 规则编辑VM |
| `ui/screen/history/HistoryListScreen.kt` | Screen | 提醒历史列表 |
| `ui/screen/history/HistoryListVM.kt` | ViewModel | 提醒历史VM |
| `ui/screen/alert/AlertFullScreenVM.kt` | ViewModel | 全屏提醒VM |
| `ui/screen/alert/AlertFullScreenContent.kt` | Component | 全屏提醒内容 |
| `ui/screen/settings/SettingsScreen.kt` | Screen | 设置页 |
| `ui/screen/settings/SettingsVM.kt` | ViewModel | 设置VM |
| `ui/dialog/PermissionGuideDialog.kt` | Dialog | 权限引导弹窗 |
| `ui/dialog/BackgroundGuideDialog.kt` | Dialog | 后台引导弹窗 |
| `ui/dialog/DeleteConfirmDialog.kt` | Dialog | 删除确认弹窗 |
| `ui/dialog/RingtonePickerDialog.kt` | Dialog | 铃声选择器 |
| `ui/theme/Theme.kt` | Theme | Material3主题 |
| `ui/theme/Color.kt` | Theme | 颜色定义 |
| `ui/theme/Type.kt` | Theme | 字体定义 |

#### DI层
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `di/AppModule.kt` | DI | Hilt全局Module |
| `di/DatabaseModule.kt` | DI | 数据库Module |
| `di/RepositoryModule.kt` | DI | Repository Module |
| `di/ServiceModule.kt` | DI | Service Module |

#### 应用入口
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `ParkingNotificationApp.kt` | Application | Hilt Application入口 |

### 修改文件
无（绿色字段项目）

### 删除文件
无

## 影响范围评估

### 模块影响
- **直接影响模块**：所有模块（新建）
- **间接影响模块**：无
- **无影响模块**：无（无现有代码）

### 接口影响
- **新增接口**：所有Repository接口、UseCase接口
- **修改接口**：无
- **废弃接口**：无

### 数据库影响
- **新增表**：
  - `notification_items` — 通知项主表
  - `trigger_rules` — 触发规则表
  - `notification_item_rules` — 通知项-规则多对多关联表
  - `notification_history` — 提醒历史表
- **修改表**：无
- **数据迁移**：无

## 风险评估

### 高风险项
| 风险项 | 影响 | 应对措施 |
|-------|------|---------|
| Android 14+ READ_SMS权限受限 | 无法直接读取短信 | 使用SmsContentObserver + 用户手动授权；备选NotificationListenerService |
| 国产ROM后台服务被杀死 | 用户收不到提醒 | 厂商定制引导流程 + ForegroundService + WorkManager保活 |
| SQLCipher与Room集成兼容性 | DB初始化失败 | 使用稳定版依赖 + API 26+加密测试 |

### 中风险项
| 风险项 | 影响 | 应对措施 |
|-------|------|---------|
| 多个通知项同时匹配 | 多重提醒干扰 | 同一短信仅触发一次提醒，多条匹配合并 |
| 通知权限Android 13+新限制 | 弹窗不弹出 | 使用shouldShowRequestPermissionRationale引导 |
| 系统铃声URI持久化跨设备失效 | 铃声播放失败 | 保存铃声URI + fallback到系统默认铃声 |

### 低风险项
| 风险项 | 影响 | 应对措施 |
|-------|------|---------|
| Compose在低端设备渲染性能 | 列表卡顿 | 启用compose优化 + 使用LazyColumn + 图片资源优化 |
| 横竖屏切换状态丢失 | UI状态重置 | ViewModel保存状态 + SavedStateHandle |

## 兼容性分析
- **向后兼容**：不适用（绿色字段项目）
- **数据迁移**：不需要
- **接口兼容**：不适用
- **Android版本兼容**：API 26+（Android 8.0+），覆盖率约95%

## 建议
1. **先做核心链路**：建议先完成 通知项创建 → 短信监听 → 规则匹配 → 提醒触发 主链路，再补充历史记录等辅助功能
2. **尽早处理权限**：权限引导是用户体验关键，建议在第一个通知项创建功能完成时就完整实现
3. **适配测试**：需在华为、小米、OPPO、vivo真实设备上测试后台存活率
4. **隐私合规**：Google Play上架需提供隐私政策，明确声明短信数据用途
5. **短信监听方案**：建议同时实现 `SmsReceiver`（API < 29）和 `SmsContentObserver`（API 29+）双方案

## 代码规范应用
- [x] 遵循 Kotlin 官方编码规范
- [x] 遵循 MVVM + Clean Architecture 分层规范
- [x] 遵循 Room 数据库设计规范
- [x] 使用 Hilt 依赖注入规范
- [ ] 使用项目已有能力（绿色字段项目，无可复用能力）
