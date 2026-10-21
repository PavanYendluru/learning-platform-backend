import fs from 'fs';
import path from 'path';
import { execSync } from 'child_process';

const rawCp = fs.readFileSync('scripts/backend-cp.txt', 'utf8');
// Clean up all line wraps from powershell format-list
const cleanCp = rawCp
  .replace(/\r?\n\s*/g, '')
  .trim()
  .replace(/\\/g, '/');

const mailApiJar = 'C:/Users/yendluru pavan/.m2/repository/jakarta/mail/jakarta.mail-api/2.1.5/jakarta.mail-api-2.1.5.jar';
const angusMailJar = 'C:/Users/yendluru pavan/.m2/repository/org/eclipse/angus/angus-mail/2.0.5/angus-mail-2.0.5.jar';

const fullCp = `${cleanCp};${mailApiJar};${angusMailJar}`;

function findJavaFiles(dir) {
  let res = [];
  for (const f of fs.readdirSync(dir)) {
    const p = path.join(dir, f);
    if (fs.statSync(p).isDirectory()) {
      res = res.concat(findJavaFiles(p));
    } else if (f.endsWith('.java')) {
      res.push(p.replace(/\\/g, '/'));
    }
  }
  return res;
}

const javaFiles = findJavaFiles('C:/SPRINGBOOTWS/learning-platform-backend/src/main/java');
console.log(`Found ${javaFiles.length} Java source files to compile.`);

fs.writeFileSync('scripts/javac-args.txt', [
  '-parameters',
  '-encoding',
  'UTF-8',
  '-cp',
  `"${fullCp}"`,
  '-d',
  '"C:/SPRINGBOOTWS/learning-platform-backend/target/classes"',
  ...javaFiles.map(f => `"${f}"`)
].join('\n'), 'utf8');

console.log("Compiling with javac...");
try {
  const cmd = `C:\\JDK21\\bin\\javac.exe @"scripts/javac-args.txt"`;
  execSync(cmd, { stdio: 'inherit' });
  const resDir = 'C:/SPRINGBOOTWS/learning-platform-backend/src/main/resources';
  const targetClasses = 'C:/SPRINGBOOTWS/learning-platform-backend/target/classes';
  if (fs.existsSync(resDir)) {
    fs.cpSync(resDir, targetClasses, { recursive: true });
    console.log("Copied resources to target/classes.");
  }
  console.log("Compilation SUCCESSFUL!");
} catch (err) {
  console.error("Compilation FAILED:", err.message);
  process.exit(1);
}

