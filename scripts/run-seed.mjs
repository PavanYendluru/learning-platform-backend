import fs from 'fs';
import path from 'path';
import { spawn } from 'child_process';

const sqlPath = path.join(process.cwd(), 'scripts', 'seed.sql');
const sqlContent = fs.readFileSync(sqlPath);

console.log(`Executing SQL file: ${sqlPath} (${sqlContent.length} bytes)`);

const mysql = spawn('mysql', ['-u', 'root', '-ptiger', 'learning_platform'], {
  stdio: ['pipe', 'inherit', 'inherit']
});

mysql.stdin.write(sqlContent);
mysql.stdin.end();

mysql.on('close', (code) => {
  console.log(`MySQL process exited with code ${code}`);
  if (code === 0) {
    console.log("Database seeded successfully!");
  } else {
    console.error("Database seeding encountered errors.");
  }
});
