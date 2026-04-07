Write-Host "Compiling Backend (Java/Spring Boot)..." -ForegroundColor Yellow

.\mvnw.cmd clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "Backend compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Backend compiled successfully! 🚀" -ForegroundColor Green
.\mvnw.cmd run
