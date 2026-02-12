# Micronova 集成测试和调试指南

## 快速开始

### 编译并运行
```bash
./gradlew clean build
./gradlew runClient
```

---

## 功能测试

### ✅ 1. 自定义部位的 micronova_health 命令

#### 命令语法
```
/micronova_health <bodypart> <amount>
```

#### 支持的部位
- `chest` - 胸部
- `head` - 头部
- `left_arm` - 左臂
- `right_arm` - 右臂
- `left_leg` - 左腿
- `right_leg` - 右腿
- `all` - 所有部位

#### 测试案例

**测试1a：削减单个部位**
```
/micronova_health chest 20
```
**预期**: 仅胸部血量减少20点，其他部位不变

**测试1b：削减所有部位**
```
/micronova_health all 50
```
**预期**: 所有6个部位血量同时减少50点

**测试1c：不认识的部位**
```
/micronova_health invalid 10
```
**预期**: 在聊天栏显示可用部位列表

---

### ✅ 2. 血量系统和死亡机制

#### 血量显示（E键界面）
1. 按 **E 键** 打开库存
2. **预期结果**：
   - 左上角（缩小80%）显示6个部位的血量条
   - 每个部位显示为 "部位名: 血量值"
   - 血量条颜色从绿色（100HP）到红色（0HP）渐变

#### 死亡触发测试
**方法1：使用命令削减血量**
```
/micronova_health all 100
```
**预期**: 玩家立即死亡，屏幕变灰，显示"You Died"

**方法2：部分削减触发死亡**
```
/micronova_health chest 150
```
**预期**: 因为胸部最多100HP，所以胸部血量归0，如果其他部位都也为0，则死亡

#### 红心和饥饿度隐藏
1. 进入游戏，**不打开任何菜单**
2. **预期**：
   - 屏幕右下角**没有红心显示**（原生生命值）
   - 屏幕右下角**没有鸡腿显示**（饥饿度）
   - 只有自定义血量在E键菜单中

---

### ✅ 3. 按键绑定测试

#### 打开背包（I键）
1. 启动游戏，进入世界
2. 按 **I 键**
3. **预期结果**：
   - 打开自定义背包界面
   - 显示4个网格容器（从左到右）：
     - **pockets** (2×5)  - 容量: 10格
     - **tactical_vest** (8×8) - 容量: 64格
     - **backpack** (12×20) - 容量: 240格
   - 每个容器有标签和网格线
   - 背景为深灰色

#### 关闭背包
1. 按 **ESC 键** 或点击界面外部
2. **预期**: 返回游戏画面

---

### ✅ 4. E键菜单移除

#### 验证E键不再显示原生库存
1. 进入游戏，按 **E 键**
2. **预期**：
   - **旧的Minecraft库存界面不出现**
   - 若要打开自定义背包，使用 **I 键**

---

### ✅ 5. 基础容器始终可用

#### 无背包时的容器
1. 打开自定义背包 (I键)
2. **预期**：
   - 即使没有任何背包物品，仍显示4个容器
   - **pockets** (口袋) - 始终可用
   - **tactical_vest** (弹挂) - 始终可用
   - **backpack** (背包) - 始终可用（但可能为空）
   - 这些是基础容器，玩家天生拥有

#### 容量说明
| 容器 | 大小 | 容量 | 用途 |
|------|------|------|------|
| Pockets | 2×5 | 10格 | 快速存取小物品 |
| Tactical Vest | 8×8 | 64格 | 中等容量 |
| Backpack | 12×20 | 240格 | 主要存储 |

---

## 调试技巧

### 查看编译日志
```bash
./gradlew build 2>&1 | tee build.log
grep -i error build.log
```

### 运行单元测试
```bash
# 运行所有库存测试
./gradlew test --tests GridContainerTest
./gradlew test --tests InventoryManagerTest

# 运行所有测试
./gradlew test
```

### 启用详细输出
```bash
./gradlew runClient --info 2>&1 | tee game.log
```

### 检查死亡机制是否生效
1. 添加日志到 PlayerHealthManager：
```java
System.out.println("Player health: " + hc.snapshot());
System.out.println("Forcing kill: " + dead);
```

2. 运行命令并查看输出
```
/micronova_health all 100
```

### 验证按键绑定
1. 在 MicronovaClient.java 中添加日志：
```java
System.out.println("Inventory key pressed!");
System.out.println("Opening custom inventory screen");
```

2. 按 I 键并检查日志

---

## 常见问题

### ❌ 问题1：按I键没有反应
**可能原因**：
- 按键还未初始化
- 客户端Tick事件未触发

**调试步骤**：
1. 检查 MicronovaClient 中的 ClientTickEvents 是否正确注册
2. 验证 openInventoryKey 是否为 null
3. 检查 CustomInventoryScreen 是否正确编译

### ❌ 问题2：死亡不工作
**可能原因**：
- LivingEntityMixin 未正确拦截伤害
- PlayerHealthManager.isForceKill() 返回值不正确

**调试步骤**：
1. 在 LivingEntityMixin.onDamage() 中添加日志
2. 验证 cir.setReturnValue(true) 被执行
3. 检查 forceKill() 是否被调用

### ❌ 问题3：红心/饥饿度仍然显示
**可能原因**：
- InGameHudMixin 方法名不正确
- renderHealthBar 方法不存在

**调试步骤**：
1. 检查 InGameHudMixin 中的方法名
2. 尝试搜索其他可能的方法名（render、tick等）
3. 使用 Mixin 的 @Redirect 替代 @Inject

---

## 完整测试检查清单

```
命令系统：
☐ /micronova_health chest 20 (削减单个部位)
☐ /micronova_health all 50 (削减所有部位)
☐ /micronova_health left_leg 30 (削减其他部位)
☐ 无效部位显示错误信息

死亡系统：
☐ /micronova_health all 100 (触发死亡)
☐ 玩家死亡时屏幕变灰
☐ 显示 "You Died" 消息
☐ 红心不显示
☐ 饥饿度不显示

UI/键绑定：
☐ 按 E 键不显示原生库存
☐ 按 I 键打开自定义背包
☐ 背包显示4个容器
☐ 按 ESC 关闭背包

基础容器：
☐ 无背包时仍显示口袋和弹挂
☐ 容器容量正确（10, 64, 240格）
```

---

## 关键文件位置

```
命令系统：
src/main/java/top/vannesa/micronova/command/DebugHealthCommand.java

死亡机制：
src/main/java/top/vannesa/micronova/mixin/LivingEntityMixin.java
src/main/java/top/vannesa/micronova/health/PlayerHealthManager.java

UI组件：
src/client/java/top/vannesa/micronova/MicronovaClient.java
src/client/java/top/vannesa/micronova/ui/inventory/CustomInventoryScreen.java
src/client/java/top/vannesa/micronova/inventory/client/ClientInventoryCache.java

Mixin配置：
src/main/java/top/vannesa/micronova/mixin/
src/client/java/top/vannesa/micronova/mixin/client/

库存系统：
src/main/java/top/vannesa/micronova/inventory/
```

---

## 性能优化建议

1. **减少Mixin日志输出** - 移除调试 System.out.println
2. **缓存容器** - ClientInventoryCache 应在需要时才更新
3. **优化碰撞检测** - 考虑使用空间分割算法
4. **网络同步** - 仅在变化时同步背包状态（待实现）

