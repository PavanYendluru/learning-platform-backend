import fs from 'fs';
import { spawn } from 'child_process';

const rawCp = fs.readFileSync('scripts/backend-cp.txt', 'utf8');
const cleanCp = rawCp
  .replace(/\r?\n\s*/g, '')
  .trim();

const mailApiJar = 'C:\\Users\\yendluru pavan\\.m2\\repository\\jakarta\\mail\\jakarta.mail-api\\2.1.5\\jakarta.mail-api-2.1.5.jar';
const angusMailJar = 'C:\\Users\\yendluru pavan\\.m2\\repository\\org\\eclipse\\angus\\angus-mail\\2.0.5\\angus-mail-2.0.5.jar';
const fullCp = `${cleanCp};${mailApiJar};${angusMailJar}`;

console.log("Starting Spring Boot backend...");
const child = spawn('C:\\JDK21\\bin\\java.exe', [
  '-cp', fullCp,
  'com.learningplatform.backend.LearningPlatformBackendApplication'
], {
  cwd: 'C:\\SPRINGBOOTWS\\learning-platform-backend',
  stdio: 'inherit',
  detached: false
});

child.on('error', (err) => {
  console.error("Failed to start backend:", err);
});

child.on('exit', (code, signal) => {
  console.log(`Backend exited with code ${code} / signal ${signal}`);
});
