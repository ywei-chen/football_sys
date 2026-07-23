# 球隊事務處理系統 — 需求規劃書(Java 生態版 v2)

> 給老婆公司內部用的系統，範圍由原本單純的「繳費對帳」擴大為完整的球隊事務處理系統，涵蓋球隊資料管理、通知發送、對帳三大模組，並加上登入驗證。
>
> 對應 IntelliJ IDEA 專案 `paymentMapping`（已完成 Spring Boot 環境建置）。本文件為 v1（繳費對帳系統）的擴充改版。

## 專案目標

- 使用者：非技術人員（老婆）及公司內部人員，需要簡單好操作的網頁介面
- 系統性質：內部小型事務處理系統，低流量、單機或家用 NAS 部署即可
- 三大功能模組 + 一個登入驗證模組（詳見下方「模組規格」）

## 模組總覽

| 模組 | 功能摘要 |
|---|---|
| 登入驗證模組 | 單一帳號登入驗證，無需自助註冊 |
| 表單模組 | 所有球隊基本資料表的新增、修改、匯出 |
| 通知模組 | Email / 三竹簡訊文案樣板管理，選擇發送對象球隊，排程發送 |
| 對帳模組 | 比對 Email 收到的繳費通知與球隊繳費狀態，列出已繳費/未繳費名單 |

## 技術選型（Java 生態版）

| 項目 | 選擇 | 原因 |
|---|---|---|
| 後端語言 | Java 17（LTS）+ Spring Boot 4.1.0 | 業界最主流的企業級 Java 框架，已完成環境建置 |
| 後端角色 | 純 REST API（不再輸出 HTML） | 因前端改用 Vue SPA，後端只負責資料與商業邏輯，透過 JSON 溝通 |
| 前端框架 | **Vue 3**（Composition API）+ Vue Router + Pinia，Vite 建置 | 學習曲線比 React 平緩、語法接近傳統 HTML/JS，適合一人開發維護的內部工具 |
| 登入驗證 | Spring Security + JWT | REST API + SPA 架構下最常見的驗證方式；單一帳號，不做自助註冊，帳號由後台直接建立 |
| 資料庫 | PostgreSQL（正式環境）／H2（本機開發，已完成設定） | `pg_trgm` 擴充套件可做模糊字串比對，符合對帳模組需求 |
| ORM | Spring Data JPA + Hibernate（已完成設定） | Spring 生態標準資料存取層 |
| Email 發送 | Spring Boot Starter Mail（`JavaMailSender`） | Spring 內建標準郵件發送機制 |
| 簡訊發送 | 三竹簡訊（Mitake）HTTP API | 台灣常見簡訊閘道，提供 HTTP GET/POST 介面，Java 端用 `RestTemplate` 或 `WebClient` 呼叫即可，不需要額外 SDK |
| 排程 | Spring `@Scheduled`（固定週期）＋ 視需求評估 Quartz（若需要「使用者自訂排程時間」存進資料庫再觸發） | 對應通知模組的排程發送功能 |
| Excel 匯出 | Apache POI | Java 生態標準 Excel 讀寫函式庫，供表單模組「匯出」功能使用 |
| 建置工具 | Maven（後端）＋ npm/Vite（前端） | 後端維持原設定；前端用 Vite 做開發伺服器與正式建置 |
| IDE | IntelliJ IDEA Community（已完成安裝設定） | 對 Spring Boot / Maven 支援完整；Vue 部分可另外用 VS Code 或 IntelliJ 內建的 JS/Vue 支援 |
| 部署 | **單一 Docker image**（multi-stage build） | Stage 1：Node.js 環境 build Vue 專案成靜態檔案；Stage 2：Maven 環境把靜態檔案複製進 `src/main/resources/static` 一起打包成 jar；Stage 3：輕量 JRE runtime 執行最終 jar。整個系統只需一個容器運作 |

## 系統架構（資料流）

```
┌─────────────────────────────────────────────────────────┐
│                     Vue 3 SPA（前端）                      │
│   登入頁 / 球隊表單頁 / 通知樣板與排程頁 / 對帳結果頁         │
└───────────────────────┬─────────────────────────────────┘
                         │ REST API（JSON，JWT 驗證）
┌───────────────────────▼─────────────────────────────────┐
│                Spring Boot（後端，單一 jar）                │
│                                                           │
│  Spring Security + JWT ── 登入驗證模組                     │
│  Team Controller/Service ── 表單模組（CRUD + Excel 匯出）   │
│  Notification Controller/Service ── 通知模組               │
│      ├─ Email（JavaMailSender）                           │
│      ├─ 三竹簡訊（HTTP API）                                │
│      └─ Spring @Scheduled / Quartz（排程發送）              │
│  Reconciliation Service ── 對帳模組                        │
│      └─ Gmail API 讀信 → 解析 → pg_trgm 模糊比對            │
└───────────────────────┬─────────────────────────────────┘
                         │ Spring Data JPA
┌───────────────────────▼─────────────────────────────────┐
│         PostgreSQL（正式）／H2（開發）                       │
│  Team / User / NotificationTemplate / NotificationLog /   │
│  PaymentEmailRaw / ReconciliationResult                   │
└───────────────────────────────────────────────────────────┘
```

## 模組詳細規格

### 1. 登入驗證模組

- 單一帳號，密碼由後台直接建立（不開放自助註冊）
- 登入成功後由後端簽發 JWT，前端存於記憶體或 `httpOnly` cookie，之後每次 API 請求帶上驗證
- 暫不需要角色權限區分（單一帳號即單一角色）

### 2. 表單模組

- 「所有球隊基本資料表」的新增、修改、匯出（Excel）
- 待確認欄位：球隊名稱、聯絡人、聯絡方式、應繳金額、其他必要欄位（見下方待確認事項）

### 3. 通知模組

- Email、三竹簡訊各自的文案 **text block 公版樣板**，可能包含變數（如球隊名稱、應繳金額、截止日期）
- 發送對象：可勾選特定球隊（非全發）
- 排程功能：可設定未來某時間點自動發送，或固定週期發送

### 4. 對帳模組

- 資料來源：Gmail 收到的繳費/入帳通知信
- 核心功能：自動解析信件內容，跟球隊基本資料比對，依信心程度自動勾選或轉人工確認
- 比對信心分級邏輯（與原規劃相同）：

| 信心層級 | 比對條件 | 處理方式 |
|---|---|---|
| 高信心 | 備註/信件含唯一識別碼（球隊編號）+ 金額完全相符 | 自動勾選 |
| 中信心 | 模糊比對到名稱（`pg_trgm` 相似度高）+ 金額相符 | 自動勾選，標記「建議覆核」 |
| 低信心 | 只有金額相符，或比對到多筆候選 | 進入「待人工確認」清單，附候選名單 |
| 無法比對 | 內容無意義、查無資料 | 列入例外清單 |

## 資料庫 Entity 初步規劃

- `Team`：球隊基本資料（表單模組核心資料表）
- `User`：登入帳號（單一帳號）
- `NotificationTemplate`：Email / 簡訊文案樣板
- `NotificationLog`：發送紀錄與排程設定
- `PaymentEmailRaw`：Gmail 讀取的原始信件資料
- `ReconciliationResult`：比對結果（信心分數、比對依據、對應球隊）

## 待確認 / 待決定事項

- [ ] 球隊基本資料表需要哪些欄位（名稱、聯絡人、聯絡方式、應繳金額、繳費週期等）
- [ ] Email / 簡訊樣板需要哪些變數欄位（球隊名稱、金額、截止日期…）
- [ ] 排程發送的精細度：固定週期就好，還是需要「每次手動指定某個未來時間點」（會影響選 `@Scheduled` 還是 Quartz）
- [ ] 三竹簡訊帳號/API 金鑰申請與費用確認
- [ ] Gmail OAuth token 的持久化方式（Docker volume 掛載 / 加密存 DB）
- [ ] `pg_trgm` 相似度閾值需要用實際樣本資料測試調整
- [ ] JWT 過期時間、要不要做「記住我」機制
- [ ] 老婆的往來銀行/繳費單位，Email 通知格式是否穩定

## 下一步建議

1. ~~用 IntelliJ IDEA 建立 Spring Boot 專案~~ ✅ 已完成（專案 `paymentMapping`）
2. 把後端現有設定調整為純 REST API 角色（移除 Thymeleaf 方向，確認 CORS 設定讓本機 Vue dev server 可呼叫）
3. 加入 Spring Security + JWT 依賴，實作單一帳號登入
4. 用 Vite 建立 Vue 3 專案（Vue Router + Pinia），設定好本機開發時呼叫後端 API 的 proxy
5. 設計並建立 `Team` Entity 與表單模組 CRUD API + Excel 匯出（Apache POI）
6. 設計 `NotificationTemplate` / `NotificationLog` Entity，實作 Email（`JavaMailSender`）與三竹簡訊 API 串接，接著做排程發送
7. 串接 Gmail API，實作對帳模組（讀信 → 解析 → `pg_trgm` 比對）
8. 前端依序做出：登入頁、球隊表單頁、通知樣板與排程頁、對帳結果頁
9. 撰寫 Dockerfile（Node build → Maven build → JRE runtime 三階段）與 `docker-compose.yml`（含 PostgreSQL + App 服務）

## 環境備註

- 時區需設定 `TZ=Asia/Taipei`
- Gmail API 費用：目前完全免費，超額計費規劃在 2026 年稍晚才會開始
- 三竹簡訊為付費服務（依發送量計費），需另外申請帳號
- IntelliJ IDEA 環境已確認可正常啟動 Spring Boot 應用
