@echo off
REM Setup environment variables for DigiCert KeyLocker code signing
REM Replace the placeholder values with your actual DigiCert KeyLocker credentials

SET DIGICERT_API_KEY=your-api-key-here
SET DIGICERT_KEYLOCKER_USERNAME=your-keylocker-username
SET DIGICERT_KEYLOCKER_PASSWORD=your-keylocker-password
SET DIGICERT_CERTIFICATE_ID=your-certificate-id

REM Path to SignTool.exe - adjust based on your Windows SDK installation
SET SIGNTOOL_PATH=C:\Program Files (x86)\Windows Kits\10\bin\10.0.19041.0\x64\signtool.exe

echo DigiCert KeyLocker environment variables set successfully.
echo.
echo For testing the signed executable without publishing to GitHub:
echo npm run build:electron-signed-test
echo.
echo For building and publishing to GitHub:
echo npm run build:electron-signed 