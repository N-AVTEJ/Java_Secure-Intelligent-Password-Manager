$ErrorActionPreference = "Stop"

Write-Host "Applying database schema from sql/setup.sql..." -ForegroundColor Cyan
Get-Content ".\sql\setup.sql" | mysql -u root -p""

Write-Host "Verifying password_db/passwords table..." -ForegroundColor Cyan
mysql -u root -p"" -e "USE password_db; SHOW TABLES; SELECT COUNT(*) AS total_rows FROM passwords;"

Write-Host "Database setup completed." -ForegroundColor Green
