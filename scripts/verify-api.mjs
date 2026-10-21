const API_BASE = "http://localhost:8080/api";

async function verifyApi() {
  const loginRes = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: "admin@learningplatform.com",
      password: "Password@123"
    })
  });
  const loginJson = await loginRes.json();
  const token = loginJson.data.accessToken;
  console.log("Logged in:", loginJson.data.email, "Role:", loginJson.data.role);

  // 1. Domains
  const domRes = await fetch(`${API_BASE}/domains`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  const domJson = await domRes.json();
  console.log(`\n--- DOMAINS (${domJson.data?.length}) ---`);
  domJson.data?.forEach(d => console.log(`[ID ${d.id}] ${d.name} (${d.slug})`));

  // 2. Resources
  const resRes = await fetch(`${API_BASE}/resources?size=50`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  const resJson = await resRes.json();
  console.log(`\n--- RESOURCES (${resJson.data?.totalElements} total) ---`);
  resJson.data?.content?.slice(0, 5).forEach(r => console.log(`- [${r.resourceType}] ${r.title} (${r.externalUrl})`));

  // 3. Admin dashboard stats
  const statsRes = await fetch(`${API_BASE}/admin/dashboard/stats`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  console.log("\n--- ADMIN DASHBOARD STATS ---");
  console.log(await statsRes.json());
}

verifyApi();
