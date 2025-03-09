# Code Signing Instructions for Windows EXE

This document explains how to code sign the Windows executable using DigiCert KeyLocker.

## Prerequisites

1. DigiCert KeyLocker account with a valid code signing certificate
2. Windows SDK installed (for SignTool.exe)
3. Node.js and npm installed

## Setup

1. Edit the `setup-signing-env.bat` file in the `electron/resources` directory:

   - Replace `your-api-key-here` with your DigiCert API key
   - Replace `your-keylocker-username` with your DigiCert KeyLocker username
   - Replace `your-keylocker-password` with your DigiCert KeyLocker password
   - Replace `your-certificate-id` with your DigiCert certificate ID
   - Verify the path to SignTool.exe is correct for your system

2. Run the batch file to set up the environment variables:

   ```
   cd electron/resources
   setup-signing-env.bat
   ```

3. Build options:

   a. To build and sign the application WITHOUT publishing to GitHub (for testing):

   ```
   npm run build:electron-signed-test
   ```

   b. To build, sign, and publish the application to GitHub:

   ```
   npm run build:electron-signed
   ```

## Testing the Signed Executable

After building with the `build:electron-signed-test` command:

1. The signed executable will be available in the `electron/dist` directory
2. Verify the signature using the Windows File Properties dialog:

   - Right-click on the .exe file
   - Select Properties
   - Go to the "Digital Signatures" tab
   - You should see your certificate listed

3. You can also verify the signature using SignTool:
   ```
   signtool verify /pa /v your-executable.exe
   ```

## Troubleshooting

If you encounter issues with code signing:

1. Verify that your DigiCert KeyLocker credentials are correct
2. Check that SignTool.exe is available at the specified path
3. Ensure your certificate is valid and has not expired
4. Check the console output for any error messages

## CI/CD Integration

For CI/CD pipelines, set the following environment variables in your build environment:

- `DIGICERT_API_KEY`
- `DIGICERT_KEYLOCKER_USERNAME`
- `DIGICERT_KEYLOCKER_PASSWORD`
- `DIGICERT_CERTIFICATE_ID`
- `SIGNTOOL_PATH` (if different from the default)

## Notes

- The code signing process uses the SHA-256 algorithm
- The signed executable is timestamped using DigiCert's timestamp server
- The certificate subject name is set to "UNICEF" (change this in electron-builder.config.json if needed)
