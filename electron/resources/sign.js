// This script handles code signing with DigiCert KeyLocker
const { execSync } = require("child_process");
const path = require("path");
const fs = require("fs");

/**
 * Custom signing function for electron-builder
 * @param {Object} options - Signing options provided by electron-builder
 * @param {string} options.path - Path to the file to sign
 */
exports.default = async function (options) {
  console.log("Signing file:", options.path);

  // Get DigiCert KeyLocker credentials from environment variables
  const apiKey = process.env.DIGICERT_API_KEY;
  const keyLockerUsername = process.env.DIGICERT_KEYLOCKER_USERNAME;
  const keyLockerPassword = process.env.DIGICERT_KEYLOCKER_PASSWORD;
  const certificateId = process.env.DIGICERT_CERTIFICATE_ID;

  if (!apiKey || !keyLockerUsername || !keyLockerPassword || !certificateId) {
    console.error(
      "DigiCert KeyLocker credentials not found in environment variables"
    );
    throw new Error("Missing DigiCert KeyLocker credentials");
  }

  try {
    // Path to the SignTool.exe - you may need to adjust this path based on your Windows SDK installation
    const signToolPath =
      process.env.SIGNTOOL_PATH ||
      "C:\\Program Files (x86)\\Windows Kits\\10\\bin\\10.0.19041.0\\x64\\signtool.exe";

    // Verify SignTool exists
    if (!fs.existsSync(signToolPath)) {
      throw new Error(`SignTool not found at path: ${signToolPath}`);
    }

    // Command to sign the file using DigiCert KeyLocker
    // This is a simplified example - you'll need to adjust based on DigiCert's specific API requirements
    const command = `"${signToolPath}" sign /fd sha256 /tr http://timestamp.digicert.com /td sha256 /n "UNICEF" /kc "${certificateId}" /kl "${keyLockerUsername}:${keyLockerPassword}" "${options.path}"`;

    // Execute the signing command
    execSync(command, { stdio: "inherit" });

    console.log("Successfully signed:", options.path);
  } catch (error) {
    console.error("Error signing file:", error);
    throw error;
  }
};
