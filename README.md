# library-backend
图书管理系统后端 —— 成本统计报表服务

## 运行
- 启动: `mvn spring-boot:run`（默认端口 8080；H2 内存库自动建表并载入种子数据）
- 测试: `mvn test`

## 接口（前缀 /api，响应体 {code,message,data}）
- GET /api/cost/summary                   成本总览（年）
- GET /api/cost/analysis                  多维度统计分析（department/project/business_line/employee/month/quarter/year）
- GET /api/cost/export                    报表导出（format=xlsx|csv）

## 统计口径说明
- 预算口径：`project.budget_amount` 为年度预算；月度/季度/年度维度下按「当期有成本发生的去重项目预算合计（每项目每键只计一次）」统计，即月/季度行的预算为该项目的年度预算额。
- 预算占比 = 实际消耗 ÷ 项目预算 × 100（预算为 0 记 0.00%）；预计超支金额 = 实际消耗 − 项目预算（超支为正）。