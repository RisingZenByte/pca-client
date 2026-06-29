# PCA Client

[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](LICENSE)
[![Releases](https://img.shields.io/github/v/release/RisingZenByte/pca-client?label=release)](https://github.com/RisingZenByte/pca-client/releases)

轻量 **PCA 同步协议客户端**，配合服务端 [carpet-pls-addition](https://github.com/RisingZenByte/carpet-pls-addition) 使用，为 **Tweakeroo 容器预览** 提供多人游戏数据同步。

> 替代 MasaGadget 在部分新版本上缺失的 PCA 客户端功能；**不要**与 MasaGadget 或客户端版 `carpet-pls-addition` 同时安装。

---

## 支持版本

本项目会随 Minecraft 更新持续维护。请从 [Releases](https://github.com/RisingZenByte/pca-client/releases) 下载与客户端 **相同 MC 版本** 的 jar，并与服务端 [carpet-pls-addition](https://github.com/RisingZenByte/carpet-pls-addition) 的对应版本配套使用。

| Minecraft | Mod 版本 | 状态 |
|-----------|----------|------|
| **26.2** | 1.0.0 | 当前支持 |

Release 标签格式：`v{mod版本}-mc{mc版本}`（例如 `v1.0.0-mc26.2`）。

---

## 安装

### 客户端（本 mod）

| 依赖 | 必须 |
|------|------|
| Minecraft（与 jar 匹配） | ✓ |
| Fabric Loader ≥ 0.19.3 | ✓ |
| Fabric API | ✓ |
| [MaLiLib](https://modrinth.com/mod/malilib)（同 MC 版本） | ✓ |
| [Tweakeroo](https://modrinth.com/mod/tweakeroo)（同 MC 版本） | ✓ |
| **pca-client**（同 MC 版本） | ✓ |

### 服务端（不是本 mod）

安装同 MC 版本的 [carpet-pls-addition](https://github.com/RisingZenByte/carpet-pls-addition) 并执行：

```
/pls pcaSyncProtocol true
```

### carpet-pls-addition 要放客户端吗？

**不需要，且会导致崩溃。** 服务端 mod 与 pca-client 会重复注册 `pca:*` 网络包。客户端只需 **pca-client**。

---

## 使用

1. 连接已开启 PCA 协议的服务器
2. 日志出现：`PCA sync protocol enabled by server`
3. 在 Tweakeroo 中开启 **Inventory Preview** 并按住快捷键
4. 指向箱子、漏斗、村民等即可预览

---

## 编译

本地编译前，将对应 MC 版本的 MaLiLib、Tweakeroo jar 放入 `libs/`（见 `libs/README.md`）。GitHub Actions 会自动下载当前版本的依赖。

```powershell
git clone https://github.com/RisingZenByte/pca-client.git
cd pca-client
.\gradlew.bat build
```

产物：`build/libs/pca-client-*.jar`

新版本跟进时，更新 `gradle.properties` 中的 `minecraft_version` 及 `libs/` 中的 compile-only 依赖即可。

---

## 协议

与 [pca-protocol](https://github.com/Fallen-Breath/pca-protocol) / MasaGadget 使用相同的 `pca:*` 通道命名。

| 方向 | 包 ID |
|------|--------|
| S2C | `enable_pca_sync_protocol`, `disable_pca_sync_protocol`, `update_block_entity`, `update_entity` |
| C2S | `sync_block_entity`, `sync_entity`, `cancel_sync_block_entity`, `cancel_sync_entity` |

---

## 致谢与版权

客户端 PCA 逻辑的设计与实现**参考**了 [plusls/MasaGadget](https://github.com/plusls/MasaGadget)（LGPL-3.0）；协议规范来自 [Fallen-Breath/pca-protocol](https://github.com/Fallen-Breath/pca-protocol)。

**完整第三方声明见 [NOTICES.md](NOTICES.md)。**

- **Copyright (C) 2026 [RisingZenByte](https://github.com/RisingZenByte)**
- **License:** [LGPL-3.0-or-later](LICENSE)

---

## 相关链接

- 服务端 mod：[carpet-pls-addition](https://github.com/RisingZenByte/carpet-pls-addition)
- 问题反馈：[Issues](https://github.com/RisingZenByte/pca-client/issues)
