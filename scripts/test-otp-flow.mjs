import { execSync } from 'child_process';
import crypto from 'crypto';

const API_BASE = "http://localhost:8080/api";

async function run() {
  const testEmail = "testlearner2@example.com";
  execSync(`mysql -u root -ptiger -e "USE learning_platform; DELETE FROM email_otps WHERE email = '${testEmail}'; DELETE FROM users WHERE email = '${testEmail}';"`);

  console.log("=== 1. SEND REGISTRATION OTP ===");
  const sendRes = await fetch(`${API_BASE}/auth/register/send-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: testEmail, name: "Second Learner" })
  });
  console.log("Send OTP status:", sendRes.status, await sendRes.json());

  // Find OTP
  const dbHash = execSync(`mysql -u root -ptiger -N -e "USE learning_platform; SELECT otp_hash FROM email_otps WHERE email = '${testEmail}' ORDER BY id DESC LIMIT 1;"`).toString().trim();
  let foundOtp = null;
  for (let c = 1000; c <= 9999; c++) {
    if (crypto.createHash('sha256').update(String(c)).digest('hex') === dbHash) {
      foundOtp = String(c);
      break;
    }
  }
  console.log("Registration OTP:", foundOtp);

  // Verify OTP
  const verifyRes = await fetch(`${API_BASE}/auth/register/verify-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: testEmail, otp: foundOtp, purpose: "REGISTRATION" })
  });
  const verifyData = await verifyRes.json();
  const token = verifyData.data.verificationToken;
  console.log("Verified! Token:", token);

  // Complete Registration
  const completeRes = await fetch(`${API_BASE}/auth/register/complete`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: testEmail,
      name: "Second Learner",
      password: "OriginalPassword@123",
      verificationToken: token
    })
  });
  console.log("Registration Complete:", completeRes.status, await completeRes.json());

  console.log("\nWaiting 4 seconds before password reset (respecting 3s cooldown)...");
  await new Promise(r => setTimeout(r, 4000));

  console.log("\n=== 2. FORGOT PASSWORD FLOW ===");
  const fpSendRes = await fetch(`${API_BASE}/auth/forgot-password/send-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: testEmail })
  });
  console.log("FP Send OTP status:", fpSendRes.status, await fpSendRes.json());

  const fpHash = execSync(`mysql -u root -ptiger -N -e "USE learning_platform; SELECT otp_hash FROM email_otps WHERE email = '${testEmail}' AND purpose = 'PASSWORD_RESET' ORDER BY id DESC LIMIT 1;"`).toString().trim();
  let fpOtp = null;
  for (let c = 1000; c <= 9999; c++) {
    if (crypto.createHash('sha256').update(String(c)).digest('hex') === fpHash) {
      fpOtp = String(c);
      break;
    }
  }
  console.log("Password Reset OTP:", fpOtp);

  // Verify reset OTP
  const fpVerifyRes = await fetch(`${API_BASE}/auth/forgot-password/verify-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: testEmail, otp: fpOtp, purpose: "PASSWORD_RESET" })
  });
  const fpVerifyData = await fpVerifyRes.json();
  const resetToken = fpVerifyData.data.verificationToken;
  console.log("FP Verified! Reset Token:", resetToken);

  // Reset password
  const resetRes = await fetch(`${API_BASE}/auth/forgot-password/reset`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: testEmail,
      resetToken: resetToken,
      newPassword: "BrandNewPassword@456"
    })
  });
  console.log("Reset password status:", resetRes.status, await resetRes.json());

  // Test old password fails
  console.log("\n=== 3. VERIFY LOGIN ===");
  const oldLogin = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: testEmail, password: "OriginalPassword@123" })
  });
  console.log("Old password login (expected 400):", oldLogin.status);

  const newLogin = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: testEmail, password: "BrandNewPassword@456" })
  });
  console.log("New password login (expected 200):", newLogin.status, await newLogin.json());
}

run();
