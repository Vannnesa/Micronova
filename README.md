# Micronova（中文）

Micronova 是一个基于 Minecraft 1.20.1 + Fabric 的模组工程，目标是打造一个类 Escape from Tarkov（塔科夫）风格的战斗与生存系统。重点为：部位化伤害、真实弹道与穿甲、模块化武器系统、护甲耐久与医疗机制。

## 目标
- 在 Minecraft 中还原塔科夫的核心战斗体验：弹道、穿甲、护甲、模块化武器与肢体伤残。
- 采用可配置的数据驱动方式（JSON），便于扩展武器与弹药数据。
- 保持良好的网络同步（服务器权威），支持多人游戏。

## 当前完成度（高层）
- 部位健康（7 个部位）、护甲减伤与流血机制
- 基础 9mm 手枪与客户端 HUD、射击流程实现
- 网络同步（HealthSyncS2CPacket）与基本单元测试

## 快速上手（构建与测试）
```bash
# 全量构建（含测试）
./gradlew build

# 仅运行测试
./gradlew test

# 运行指定测试类
./gradlew test --tests WeaponDamageTest

# 运行单个测试方法
./gradlew test --tests WeaponDamageTest.pistolAppliesExpectedDamageWithPenetration

# 启动开发客户端/服务器
./gradlew runClient
./gradlew runServer

# 清理并重建
./gradlew clean build
```

## 源码结构（高层）
- `src/main/java`：服务器/通用逻辑（HealthComponent、HitscanService、ModPackets 等）
- `src/client/java`：客户端渲染与输入处理（HUD、ClientHealthCache、ClientPackets）
- `src/shared/java`：跨端共用类（WeaponConfig 等）
- `src/test/java`：JUnit 单元测试（无需 Minecraft 客户端即可运行）

## 关键约定
- Java 版本：17（Gradle 配置中已定义）
- Fabric Loom 用于构建与运行（runClient/runServer）
- 共享源（src/shared）必须加入所有 sourceSet 的 classpath（build.gradle 中已处理）
- 所有新增功能应配套单元测试并保证 CI 通过

## 路线图（示例优先级）
1. WeaponConfig 外部化（JSON）
2. 护甲与耐久系统
3. 弹道与穿甲
4. 肢体伤残与医疗系统
5. 扩展武器/弹药数据、AI 与地图

## CI
项目在 GitHub Actions (.github/workflows/build.yml) 上运行构建与测试，CI 在每次 push 与 PR 时触发并上传构建产物。

---

如需开始实现某个子系统，请在本仓库中选择优先项或直接回复让我按推荐顺序开始（后续会在会话计划中记录每步变更）。
