import { execSync } from 'child_process';
import crypto from 'crypto';

const API_BASE = "http://localhost:8080/api";

function logStep(name) {
  console.log(`\n==================================================`);
  console.log(`>>> ${name}`);
  console.log(`==================================================`);
}

function assert(condition, message) {
  if (!condition) {
    console.error(`FAILED ASSERTION: ${message}`);
    throw new Error(message);
  }
  console.log(`  ✓ ${message}`);
}

async function runAllScenarios() {
  const results = {};

  // -------------------------------------------------------------
  // SCENARIO E & F: Security & Invalid Auth Tests
  // -------------------------------------------------------------
  logStep("SCENARIO E: Invalid Login Handling");
  try {
    const invalidLoginRes = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: "admin@learningplatform.com", password: "WrongPassword!999" })
    });
    assert(invalidLoginRes.status === 400 || invalidLoginRes.status === 401, `Invalid password returns 400/401 (got ${invalidLoginRes.status})`);
    const invJson = await invalidLoginRes.json();
    assert(invJson.success === false, "Response success is false");
    results.scenarioE = "PASSED";
  } catch (err) {
    results.scenarioE = `FAILED: ${err.message}`;
  }

  logStep("SCENARIO F: Unauthenticated Access Denied");
  try {
    const unauthRes = await fetch(`${API_BASE}/admin/users`);
    assert(unauthRes.status === 401 || unauthRes.status === 403, `Unauthenticated request returns 401/403 (got ${unauthRes.status})`);
    results.scenarioF = "PASSED";
  } catch (err) {
    results.scenarioF = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO A: Full Learner Lifecycle
  // -------------------------------------------------------------
  logStep("SCENARIO A: User Registration, Login, Dashboard, Browse, Bookmark, Logout");
  const testUserEmail = `audit_user_${Date.now()}@example.com`;
  let userToken = null;
  let testBookmarkId = null;

  try {
    // 1. Send OTP
    const sendOtpRes = await fetch(`${API_BASE}/auth/register/send-otp`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: testUserEmail, name: "Audit Learner" })
    });
    assert(sendOtpRes.status === 200, "Registration OTP requested successfully");

    // 2. Query DB for OTP hash & recover code
    const dbHash = execSync(`mysql -u root -ptiger -N -e "USE learning_platform; SELECT otp_hash FROM email_otps WHERE email = '${testUserEmail}' ORDER BY id DESC LIMIT 1;"`).toString().trim();
    let otpCode = null;
    for (let c = 1000; c <= 9999; c++) {
      if (crypto.createHash('sha256').update(String(c)).digest('hex') === dbHash) {
        otpCode = String(c);
        break;
      }
    }
    assert(otpCode !== null, `Recovered OTP code for registration: ${otpCode}`);

    // 3. Verify OTP
    const verifyRes = await fetch(`${API_BASE}/auth/register/verify-otp`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: testUserEmail, otp: otpCode, purpose: "REGISTRATION" })
    });
    assert(verifyRes.status === 200, "OTP verified successfully");
    const verifyData = await verifyRes.json();
    const verificationToken = verifyData.data.verificationToken;

    // 4. Complete Registration
    const completeRes = await fetch(`${API_BASE}/auth/register/complete`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: testUserEmail,
        name: "Audit Learner",
        password: "Password@123",
        verificationToken
      })
    });
    assert(completeRes.status === 201 || completeRes.status === 200, "Registration completed successfully");

    // 5. Login as Learner
    const loginRes = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: testUserEmail, password: "Password@123" })
    });
    assert(loginRes.status === 200, "Learner login succeeded");
    const loginData = await loginRes.json();
    userToken = loginData.data.accessToken;
    assert(loginData.data.role === "USER", "User role is confirmed as USER");

    // 6. User Progress
    const progressRes = await fetch(`${API_BASE}/users/progress`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(progressRes.status === 200, "User progress retrieved");

    // 7. Browse Domains & Resources
    const domainsRes = await fetch(`${API_BASE}/domains`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(domainsRes.status === 200, "Domains browsed by learner");
    const domData = await domainsRes.json();
    assert(domData.data?.length > 0, `Learner received ${domData.data?.length} domains`);

    const resourcesRes = await fetch(`${API_BASE}/resources?page=0&size=5`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(resourcesRes.status === 200, "Resources browsed by learner");
    const resData = await resourcesRes.json();
    const firstResource = resData.data?.content?.[0];
    assert(firstResource !== undefined, `Found first resource ID ${firstResource?.id}: ${firstResource?.title}`);

    // 8. Bookmark Resource via /users/bookmarks/{id}
    const bookmarkRes = await fetch(`${API_BASE}/users/bookmarks/${firstResource.id}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(bookmarkRes.status === 200 || bookmarkRes.status === 201, "Resource bookmarked");
    testBookmarkId = firstResource.id;

    // Check bookmarks list
    const myBookmarksRes = await fetch(`${API_BASE}/users/bookmarks`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(myBookmarksRes.status === 200, "Retrieved learner bookmarks");
    const myBmJson = await myBookmarksRes.json();
    assert(myBmJson.data?.some(b => b.resourceId === testBookmarkId), "Bookmarked resource verified in list");

    // 9. Remove bookmark
    const unbmRes = await fetch(`${API_BASE}/users/bookmarks/${testBookmarkId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(unbmRes.status === 200 || unbmRes.status === 204, "Bookmark removed successfully");

    results.scenarioA = "PASSED";
  } catch (err) {
    results.scenarioA = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO G: Ordinary User Denied Admin Operations
  // -------------------------------------------------------------
  logStep("SCENARIO G: Ordinary User Attempting Admin-Only Operations");
  try {
    const adminAttempt = await fetch(`${API_BASE}/admin/users`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(adminAttempt.status === 403, `Learner cannot access /api/admin/users (returned ${adminAttempt.status})`);

    const adminStatsAttempt = await fetch(`${API_BASE}/admin/dashboard/stats`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(adminStatsAttempt.status === 403, `Learner cannot access /api/admin/dashboard/stats (returned ${adminStatsAttempt.status})`);

    results.scenarioG = "PASSED";
  } catch (err) {
    results.scenarioG = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO B & C: Admin Login, Create Topic, Create Resource, Edit Resource
  // -------------------------------------------------------------
  logStep("SCENARIO B & C: Admin Lifecycle (Create Topic, Resource, Edit, Verify in User Catalog)");
  let adminToken = null;
  let createdTopicId = null;
  let createdResourceId = null;

  try {
    // 1. Admin Login
    const adminLoginRes = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: "admin@learningplatform.com", password: "Password@123" })
    });
    assert(adminLoginRes.status === 200, "Admin login successful");
    const adminLoginData = await adminLoginRes.json();
    adminToken = adminLoginData.data.accessToken;
    assert(adminLoginData.data.role === "ADMIN", "Role is confirmed as ADMIN");

    // 2. Admin Dashboard Stats
    const statsRes = await fetch(`${API_BASE}/admin/dashboard/stats`, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    assert(statsRes.status === 200, "Admin dashboard stats retrieved");
    const statsData = await statsRes.json();
    console.log("  Initial stats:", statsData.data);

    // 3. Fetch domains to get active domain ID
    const domRes = await fetch(`${API_BASE}/domains`, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    const domData = await domRes.json();
    const activeDomainId = domData.data[0].id;
    console.log(`  Using active domain ID: ${activeDomainId} (${domData.data[0].name})`);
    const newTopicPayload = {
      name: `Test Topic ${Date.now()}`,
      description: "Automated test topic created during full audit",
      sequenceNumber: 99,
      difficulty: "BEGINNER",
      status: "ACTIVE"
    };

    const createTopicRes = await fetch(`${API_BASE}/admin/domains/${activeDomainId}/topics`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${adminToken}`
      },
      body: JSON.stringify(newTopicPayload)
    });
    assert(createTopicRes.status === 201 || createTopicRes.status === 200, "Topic created by admin");
    const topicJson = await createTopicRes.json();
    createdTopicId = topicJson.data.id;
    console.log(`  Created topic ID: ${createdTopicId}`);

    // 4. Create Resource under New Topic
    const newResourcePayload = {
      title: `Automated Test Video ${Date.now()}`,
      description: "Comprehensive testing resource link",
      resourceType: "VIDEO",
      topicId: createdTopicId,
      externalUrl: "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
      youtubeContentType: "VIDEO",
      difficulty: "BEGINNER",
      status: "PUBLISHED",
      durationMinutes: 15
    };

    const createResRes = await fetch(`${API_BASE}/admin/resources`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${adminToken}`
      },
      body: JSON.stringify(newResourcePayload)
    });
    assert(createResRes.status === 201 || createResRes.status === 200, "Resource created by admin");
    const resCreatedJson = await createResRes.json();
    createdResourceId = resCreatedJson.data.id;
    console.log(`  Created resource ID: ${createdResourceId}`);

    // 5. Verify Resource appears in Public Learner API
    const publicResCheck = await fetch(`${API_BASE}/resources/${createdResourceId}`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(publicResCheck.status === 200, "New resource is immediately visible in User portal");
    const publicResJson = await publicResCheck.json();
    assert(publicResJson.data.title === newResourcePayload.title, "Resource title matches created payload");

    // 6. SCENARIO C: Edit Resource
    const updatedPayload = {
      ...newResourcePayload,
      title: `Updated Video Title ${Date.now()}`,
      durationMinutes: 25
    };

    const editRes = await fetch(`${API_BASE}/admin/resources/${createdResourceId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${adminToken}`
      },
      body: JSON.stringify(updatedPayload)
    });
    assert(editRes.status === 200, "Resource updated successfully by admin");

    // Verify persistence in Public Learner API
    const verifyEdit = await fetch(`${API_BASE}/resources/${createdResourceId}`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    const verifyEditJson = await verifyEdit.json();
    assert(verifyEditJson.data.title === updatedPayload.title, "Updated title persisted and visible to user");
    assert(verifyEditJson.data.durationMinutes === 25, "Updated duration persisted");

    results.scenarioB = "PASSED";
    results.scenarioC = "PASSED";
  } catch (err) {
    results.scenarioB = `FAILED: ${err.message}`;
    results.scenarioC = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO D: Delete Resource & Topic Cleanup
  // -------------------------------------------------------------
  logStep("SCENARIO D: Admin Deletes Resource & Topic, Verify Removal");
  try {
    if (createdResourceId) {
      const deleteRes = await fetch(`${API_BASE}/admin/resources/${createdResourceId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${adminToken}` }
      });
      assert(deleteRes.status === 200 || deleteRes.status === 204, `Resource ${createdResourceId} deleted`);

      // Verify resource no longer accessible
      const checkDeleted = await fetch(`${API_BASE}/resources/${createdResourceId}`, {
        headers: { Authorization: `Bearer ${userToken}` }
      });
      assert(checkDeleted.status === 404, "Deleted resource returns 404 in public API");
    }

    if (createdTopicId) {
      const deleteTopic = await fetch(`${API_BASE}/admin/topics/${createdTopicId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${adminToken}` }
      });
      assert(deleteTopic.status === 200 || deleteTopic.status === 204, `Topic ${createdTopicId} deleted`);

      const checkDeletedTopic = await fetch(`${API_BASE}/topics/${createdTopicId}`, {
        headers: { Authorization: `Bearer ${userToken}` }
      });
      assert(checkDeletedTopic.status === 404, "Deleted topic returns 404 in public API");
    }

    results.scenarioD = "PASSED";
  } catch (err) {
    results.scenarioD = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO H: Backend Error Handling & Bad Requests
  // -------------------------------------------------------------
  logStep("SCENARIO H: Bad Request / Non-existent Entity Handling");
  try {
    const notFound = await fetch(`${API_BASE}/resources/99999999`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(notFound.status === 404, "Fetching non-existent resource returns 404");

    const badPost = await fetch(`${API_BASE}/admin/resources`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${adminToken}`
      },
      body: JSON.stringify({ title: "" }) // Invalid empty body
    });
    assert(badPost.status === 400, "Validation failure on empty payload returns 400 Bad Request");
    results.scenarioH = "PASSED";
  } catch (err) {
    results.scenarioH = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO I: AI Tutor & Provider Fallback
  // -------------------------------------------------------------
  logStep("SCENARIO I: AI Tutor Status, Conversation, & Messaging");
  let convId = null;
  try {
    const aiStatusRes = await fetch(`${API_BASE}/ai/status`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(aiStatusRes.status === 200, "AI Tutor status endpoint works");
    const aiStatus = await aiStatusRes.json();
    console.log("  AI Tutor config status:", aiStatus.data);
    assert(aiStatus.data.configured === true, "AI Tutor is configured");
    assert(aiStatus.data.primaryModel === "gemini-3.1-flash-lite", "Primary model is gemini-3.1-flash-lite");

    // Create Conversation
    const createConvRes = await fetch(`${API_BASE}/ai/conversations`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${userToken}`
      },
      body: JSON.stringify({ title: "Audit Verification Session" })
    });
    assert(createConvRes.status === 201, "AI conversation created");
    const convJson = await createConvRes.json();
    convId = convJson.data.id;
    console.log(`  Conversation ID: ${convId}`);

    // Send synchronous message with valid TeachingStyle: "BEGINNER"
    const msgRes = await fetch(`${API_BASE}/ai/conversations/${convId}/messages`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${userToken}`
      },
      body: JSON.stringify({
        content: "Hello! In one short sentence, explain what Java bytecode is.",
        teachingStyle: "BEGINNER"
      })
    });
    assert(msgRes.status === 200, "AI Tutor message processed");
    const msgJson = await msgRes.json();
    console.log("  AI Tutor response received:", msgJson.data.content);
    console.log(`  Provider: ${msgJson.data.provider}, Model: ${msgJson.data.model}`);
    assert(msgJson.data.content?.length > 10, "AI returned a coherent response");

    // Clean up conversation
    const delConv = await fetch(`${API_BASE}/ai/conversations/${convId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${userToken}` }
    });
    assert(delConv.status === 200, "Test conversation deleted successfully");

    results.scenarioI = "PASSED";
  } catch (err) {
    results.scenarioI = `FAILED: ${err.message}`;
  }

  // -------------------------------------------------------------
  // SCENARIO J: Repeat & Refresh Persistence
  // -------------------------------------------------------------
  logStep("SCENARIO J: Session Persistence & Repeat Operations");
  try {
    const finalCheck = await fetch(`${API_BASE}/admin/dashboard/stats`, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    assert(finalCheck.status === 200, "Admin session remains valid and stats query succeeds");
    results.scenarioJ = "PASSED";
  } catch (err) {
    results.scenarioJ = `FAILED: ${err.message}`;
  }

  // Cleanup test user and associated records cleanly
  try {
    const userId = execSync(`mysql -u root -ptiger -N -e "USE learning_platform; SELECT id FROM users WHERE email = '${testUserEmail}';"`).toString().trim();
    if (userId) {
      execSync(`mysql -u root -ptiger -e "USE learning_platform;
        DELETE FROM ai_messages WHERE conversation_id IN (SELECT id FROM ai_conversations WHERE user_id = ${userId});
        DELETE FROM ai_conversations WHERE user_id = ${userId};
        DELETE FROM bookmarks WHERE user_id = ${userId};
        DELETE FROM user_progress WHERE user_id = ${userId};
        DELETE FROM email_otps WHERE email = '${testUserEmail}';
        DELETE FROM users WHERE id = ${userId};"`);
      console.log(`\nCleaned up test user (ID ${userId}): ${testUserEmail}`);
    }
  } catch (e) {
    console.error("Cleanup warning:", e.message);
  }

  logStep("SUMMARY OF SCENARIO RESULTS");
  console.table(results);
}

runAllScenarios().catch(err => {
  console.error("Critical test execution failure:", err);
  process.exit(1);
});
