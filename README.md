全民監督酒駕系統
===

專案簡介：
---

本專案以一般民眾為主要使用對象，開發一套可判斷道路上機車是否存在疑似酒駕行為的系統。使用者可透過 App 中的錄影功能即時擷取道路影像，系統結合影像辨識與機器學習模型，分析駕駛行為並判斷其是否具備酒後駕駛風險。若系統判定有潛在酒駕風險，將自動上傳檢舉資料以利警方後續處理，為道路安全盡一份心力。

核心功能：
---

>民眾端功能：

- 即時錄影功能，記錄路況與疑似酒駕行為
- AI 自動分析影片，判定疑似酒駕等級
- 自動擷取 GPS 位置與時間資訊
- 一鍵生成檢舉案件並送交警方
- 查看個人檢舉記錄與案件狀態

>警方端功能：
- 集中管理所有檢舉案件
- 快速搜尋與篩選案件
- 查看案件詳細資訊與影片證據
- 更新案件處理狀態

技術架構：
---

>開發環境：
- 開發語言: Kotlin
- UI 框架: Jetpack Compose
- 最小 SDK: Android 7.0 (API 24)
- 目標 SDK: Android 15 (API 35)

>主要技術棧：
- 前端技術：
  - Jetpack Compose: 現代化的宣告式 UI 開發
  - Material Design 3: 遵循最新設計規範
  - Navigation Component: 頁面導航管理
  - CameraX: 相機功能與影片錄製

- 後端服務：
  - Firebase Authentication: 使用者身份驗證與授權
  - Cloud Firestore: NoSQL 雲端資料庫
  - Firebase Storage: 影片檔案儲存

- 位置服務：
  - Google Maps SDK: 地圖顯示與互動
  - Google Location Services: GPS 定位與地理編碼
  - Geocoder: 將座標轉換為地址

- AI/ML：
  - 機器學習模型: 酒駕行為辨識（待整合）
  - 即時影像分析: 自動判定危險等級
