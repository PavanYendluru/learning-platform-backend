
-- ==========================================================
-- Learning Platform Comprehensive Seed Data
-- ==========================================================
USE `learning_platform`;

-- 1. Ensure Admin Account is Configured and Active
UPDATE users SET role = 'ADMIN', enabled = 1 WHERE email = 'admin@learningplatform.com';
UPDATE users SET enabled = 1 WHERE email = 'pavanyendluru@gmail.com';


-- Domain: Frontend Engineering & Web Development
INSERT INTO domains (name, slug, description, difficulty, icon_url, banner_url, status, created_at, updated_at)
VALUES ('Frontend Engineering & Web Development', 'frontend-engineering-and-web-development', 'Master modern client-side engineering with HTML5, CSS3, modern JavaScript (ESNext), TypeScript, React 19, Next.js, and web performance optimization.', 'BEGINNER', 'https://cdn-icons-png.flaticon.com/512/1055/1055687.png', 'https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=1200', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  description = VALUES(description),
  difficulty = VALUES(difficulty),
  icon_url = VALUES(icon_url),
  banner_url = VALUES(banner_url),
  status = VALUES(status),
  updated_at = NOW(6);

SET @domain_id = (SELECT id FROM domains WHERE slug = 'frontend-engineering-and-web-development');

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'HTML5 Semantics & Modern CSS3 Mastery', 'html5-semantics-and-modern-css3', 1, 'BEGINNER', 'Modern layout techniques including CSS Grid, Flexbox, responsive design, CSS variables, and modern animations.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'html5-semantics-and-modern-css3');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'MDN Web Docs - Learn CSS', 'The definitive guide to learning CSS from basics to advanced layout techniques and styling.', 'DOCUMENTATION', 
  'https://developer.mozilla.org/en-US/docs/Learn/CSS', NULL, 'Mozilla MDN', 
  'MDN Contributors', 'Mozilla', 'BEGINNER', 
  180, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://developer.mozilla.org/en-US/docs/Learn/CSS'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'CSS Grid & Flexbox Crash Course', 'A complete visual walkthrough of building modern responsive layouts using Flexbox and Grid.', 'VIDEO', 
  'https://www.youtube.com/watch?v=jV8B24rSN5o', 'VIDEO', 'YouTube', 
  'Traversy Media', NULL, 'BEGINNER', 
  65, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=jV8B24rSN5o'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Web.dev Learn CSS Course', 'Google\'s comprehensive evergreen CSS course and reference to level up styling fundamentals.', 'COURSE', 
  'https://web.dev/learn/css', NULL, 'web.dev', 
  'Google Chrome Team', 'Google', 'BEGINNER', 
  240, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://web.dev/learn/css'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Modern JavaScript (ES6+ & Beyond)', 'modern-javascript-es6-and-beyond', 2, 'BEGINNER', 'Deep dive into JS fundamentals: closures, prototypes, asynchronous JavaScript (Promises, async/await), and event loop internals.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'modern-javascript-es6-and-beyond');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'The Modern JavaScript Tutorial (javascript.info)', 'From the basics to advanced topics with simple, but detailed explanations, tasks and playground.', 'DOCUMENTATION', 
  'https://javascript.info/', NULL, 'JavaScript.info', 
  'Ilya Kantor', NULL, 'BEGINNER', 
  300, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://javascript.info/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'JavaScript Event Loop and Concurrency Explained', 'Essential deep dive on how the browser JavaScript runtime and microtask queue handle asynchronous execution.', 'VIDEO', 
  'https://www.youtube.com/watch?v=8aGhZQkoFbQ', 'VIDEO', 'YouTube', 
  'Philip Roberts', NULL, 'INTERMEDIATE', 
  30, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=8aGhZQkoFbQ'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'You Don\'t Know JS Yet (Book Series)', 'Deep dive book series exploring the core mechanisms of JavaScript without syntactic hand-waving.', 'BOOK', 
  'https://github.com/getify/You-Dont-Know-JS', NULL, 'GitHub', 
  'Kyle Simpson', NULL, 'INTERMEDIATE', 
  360, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://github.com/getify/You-Dont-Know-JS'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'TypeScript in Production', 'typescript-in-production', 3, 'INTERMEDIATE', 'Type safety, generics, union types, discriminated unions, utility types, and strict tsconfig configuration.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'typescript-in-production');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Official TypeScript Handbook', 'The authoritative manual for learning TypeScript language features, typing systems, and type manipulation.', 'DOCUMENTATION', 
  'https://www.typescriptlang.org/docs/handbook/intro.html', NULL, 'TypeScript Official', 
  'Microsoft', 'Microsoft', 'INTERMEDIATE', 
  180, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.typescriptlang.org/docs/handbook/intro.html'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'TypeScript Full Course for Beginners', 'Comprehensive introduction to TypeScript fundamentals, compiler configurations, and typed React components.', 'VIDEO', 
  'https://www.youtube.com/watch?v=gieEQFIfgYc', 'VIDEO', 'YouTube', 
  'freeCodeCamp', NULL, 'BEGINNER', 
  120, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=gieEQFIfgYc'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Total TypeScript Essentials', 'Interactive patterns and real-world tips for solving complex generic types and compiler narrowing in TypeScript.', 'ARTICLE', 
  'https://www.totaltypescript.com/tutorials', NULL, 'Total TypeScript', 
  'Matt Pocock', NULL, 'ADVANCED', 
  90, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.totaltypescript.com/tutorials'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'React 19 & Modern State Architecture', 'react-19-and-modern-state-architecture', 4, 'INTERMEDIATE', 'Master React 19 hooks, compiler optimizations, Suspense, Error Boundaries, custom hooks, and state management.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'react-19-and-modern-state-architecture');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'React.dev - The Official Documentation', 'Modern interactive React guide covering functional components, hooks, component lifecycles, and best practices.', 'DOCUMENTATION', 
  'https://react.dev/', NULL, 'React Official', 
  'Meta React Team', 'Meta', 'INTERMEDIATE', 
  240, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://react.dev/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'React 19 New Features & Upgrades Deep Dive', 'A thorough look into React 19 additions: useActionState, useFormStatus, useOptimistic, and the React Compiler.', 'VIDEO', 
  'https://www.youtube.com/watch?v=0vl_c3i6Y9w', 'VIDEO', 'YouTube', 
  'Jack Herrington', NULL, 'INTERMEDIATE', 
  45, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=0vl_c3i6Y9w'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Awesome React Ecosystem GitHub', 'Curated collection of state management, UI component libraries, form validators, and React design patterns.', 'GITHUB', 
  'https://github.com/enaqx/awesome-react', NULL, 'GitHub', 
  'Community', NULL, 'INTERMEDIATE', 
  60, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://github.com/enaqx/awesome-react'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Next.js & Full-Stack React Engineering', 'nextjs-and-fullstack-react-engineering', 5, 'ADVANCED', 'Next.js App Router, React Server Components (RSC), Server Actions, Route Handlers, middleware, and caching.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'nextjs-and-fullstack-react-engineering');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Next.js Learn App Router Course', 'Hands-on guided walkthrough building a production dashboard with Next.js App Router, authentication, and database querying.', 'COURSE', 
  'https://nextjs.org/learn', NULL, 'Next.js Official', 
  'Vercel', 'Vercel', 'INTERMEDIATE', 
  200, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://nextjs.org/learn'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Next.js 15 / 14 Full Tutorial & Production Project', 'End-to-end fullstack web development tutorial covering server actions, database integration, and UI styling.', 'VIDEO', 
  'https://www.youtube.com/watch?v=wm5gMKuwSYk', 'VIDEO', 'YouTube', 
  'JavaScript Mastery', NULL, 'ADVANCED', 
  180, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=wm5gMKuwSYk'
);

-- Domain: Python, AI & Data Science
INSERT INTO domains (name, slug, description, difficulty, icon_url, banner_url, status, created_at, updated_at)
VALUES ('Python, AI & Data Science', 'python-ai-and-data-science', 'Learn Python from fundamentals to advanced machine learning, deep learning with PyTorch, LangChain, and Generative AI applications.', 'INTERMEDIATE', 'https://cdn-icons-png.flaticon.com/512/5968/5968350.png', 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=1200', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  description = VALUES(description),
  difficulty = VALUES(difficulty),
  icon_url = VALUES(icon_url),
  banner_url = VALUES(banner_url),
  status = VALUES(status),
  updated_at = NOW(6);

SET @domain_id = (SELECT id FROM domains WHERE slug = 'python-ai-and-data-science');

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Python 3.12 Core & Idiomatic Code', 'python-312-core-and-idiomatic-code', 1, 'BEGINNER', 'Syntax, data structures, generators, decorators, context managers, and object-oriented Python.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'python-312-core-and-idiomatic-code');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Official Python Documentation & Tutorial', 'The official guide covering Python data structures, standard library, modules, and error handling.', 'DOCUMENTATION', 
  'https://docs.python.org/3/tutorial/', NULL, 'Python.org', 
  'Python Software Foundation', NULL, 'BEGINNER', 
  150, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://docs.python.org/3/tutorial/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Python for Beginners - Full Course', 'Comprehensive 6-hour Python beginner to intermediate tutorial covering core coding and real-world examples.', 'VIDEO', 
  'https://www.youtube.com/watch?v=_uQrJ0TkZlc', 'VIDEO', 'YouTube', 
  'Programming with Mosh', NULL, 'BEGINNER', 
  360, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=_uQrJ0TkZlc'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Data Wrangling with NumPy & Pandas', 'data-wrangling-with-numpy-and-pandas', 2, 'INTERMEDIATE', 'Vectorized computation, series and dataframes, aggregation, joins, data cleaning, and exploratory data analysis.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'data-wrangling-with-numpy-and-pandas');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Pandas User Guide & 10 Minutes to Pandas', 'Essential official cookbook for tabular data manipulation, filtering, reshaping, and time-series handling.', 'DOCUMENTATION', 
  'https://pandas.pydata.org/docs/user_guide/10min.html', NULL, 'Pandas Dev Team', 
  NULL, NULL, 'INTERMEDIATE', 
  120, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://pandas.pydata.org/docs/user_guide/10min.html'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'NumPy & Pandas Full Bootcamp', 'Hands-on data analysis course working through real financial and user behavioral datasets.', 'VIDEO', 
  'https://www.youtube.com/watch?v=vmEHCJofslg', 'VIDEO', 'YouTube', 
  'freeCodeCamp', NULL, 'INTERMEDIATE', 
  240, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=vmEHCJofslg'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Deep Learning Foundations with PyTorch', 'deep-learning-foundations-with-pytorch', 3, 'ADVANCED', 'Tensors, autograd, building feedforward neural networks, CNNs, RNNs, and training loops on GPU.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'deep-learning-foundations-with-pytorch');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'PyTorch Official 60-Minute Blitz', 'The fastest way to understand PyTorch tensors, autograd engine, and training your first neural network.', 'DOCUMENTATION', 
  'https://pytorch.org/tutorials/beginner/deep_learning_60min_blitz.html', NULL, 'PyTorch.org', 
  'PyTorch Team', 'Linux Foundation', 'ADVANCED', 
  90, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://pytorch.org/tutorials/beginner/deep_learning_60min_blitz.html'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Neural Networks: Zero to Hero by Andrej Karpathy', 'Masterclass video series building micrograd, backpropagation, and transformer language models from scratch.', 'VIDEO', 
  'https://www.youtube.com/playlist?list=PLAqhIrjkxbuWI23v9cThsA9GvCAUhRvKZ', 'PLAYLIST', 'YouTube', 
  'Andrej Karpathy', NULL, 'ADVANCED', 
  480, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/playlist?list=PLAqhIrjkxbuWI23v9cThsA9GvCAUhRvKZ'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'LLMs, Prompt Engineering & LangChain', 'llms-prompt-engineering-and-langchain', 4, 'ADVANCED', 'Building production RAG (Retrieval-Augmented Generation) systems, vector databases, tool calling, and autonomous agents.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'llms-prompt-engineering-and-langchain');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'LangChain Documentation & Architecture', 'Building context-aware LLM applications with chains, agents, memory, and vector store retrievers.', 'DOCUMENTATION', 
  'https://python.langchain.com/docs/get_started/introduction', NULL, 'LangChain', 
  'Harrison Chase', NULL, 'ADVANCED', 
  120, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://python.langchain.com/docs/get_started/introduction'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Build a Full RAG AI Application from Scratch', 'Step-by-step tutorial creating a vector search application with embeddings, OpenAI/Gemini, and LangChain.', 'VIDEO', 
  'https://www.youtube.com/watch?v=2TJxpyO3ei4', 'VIDEO', 'YouTube', 
  'FreeCodeCamp', NULL, 'ADVANCED', 
  110, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=2TJxpyO3ei4'
);

-- Domain: Cloud Computing, DevOps & Kubernetes
INSERT INTO domains (name, slug, description, difficulty, icon_url, banner_url, status, created_at, updated_at)
VALUES ('Cloud Computing, DevOps & Kubernetes', 'cloud-computing-devops-and-kubernetes', 'Master modern DevOps engineering: Linux systems, Docker containerization, Kubernetes orchestration, CI/CD with GitHub Actions, and Terraform.', 'INTERMEDIATE', 'https://cdn-icons-png.flaticon.com/512/5968/5968853.png', 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  description = VALUES(description),
  difficulty = VALUES(difficulty),
  icon_url = VALUES(icon_url),
  banner_url = VALUES(banner_url),
  status = VALUES(status),
  updated_at = NOW(6);

SET @domain_id = (SELECT id FROM domains WHERE slug = 'cloud-computing-devops-and-kubernetes');

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Docker Containerization & Production Images', 'docker-containerization-and-production-images', 1, 'BEGINNER', 'Container fundamentals, Dockerfile multi-stage builds, layers caching, container networking, and docker-compose.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'docker-containerization-and-production-images');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Docker Get Started Guide', 'Official hands-on guide for containerizing backend applications, managing volumes, and structuring containers.', 'DOCUMENTATION', 
  'https://docs.docker.com/get-started/', NULL, 'Docker Docs', 
  'Docker Inc', NULL, 'BEGINNER', 
  90, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://docs.docker.com/get-started/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Docker Crash Course for Web Developers', 'Comprehensive guide to writing optimal Dockerfiles, reducing image sizes, and running multi-container stacks.', 'VIDEO', 
  'https://www.youtube.com/watch?v=pg19Z8LL06w', 'VIDEO', 'YouTube', 
  'Traversy Media', NULL, 'BEGINNER', 
  75, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=pg19Z8LL06w'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Kubernetes (K8s) Cluster Orchestration', 'kubernetes-k8s-cluster-orchestration', 2, 'ADVANCED', 'Managing containerized applications at scale: Pods, Deployments, Services, Ingress, ConfigMaps, Secrets, and Helm charts.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'kubernetes-k8s-cluster-orchestration');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Kubernetes Official Documentation & Interactive Tutorials', 'Concepts and interactive katacoda scenarios for deploying resilient cluster workloads.', 'DOCUMENTATION', 
  'https://kubernetes.io/docs/home/', NULL, 'Kubernetes.io', 
  'CNCF', NULL, 'ADVANCED', 
  200, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://kubernetes.io/docs/home/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Kubernetes Tutorial for Beginners [Full Course]', 'Complete 4-hour breakdown of architectural components, kubectl commands, services, and production deployment.', 'VIDEO', 
  'https://www.youtube.com/watch?v=X48VuDVv0do', 'VIDEO', 'YouTube', 
  'TechWorld with Nana', NULL, 'INTERMEDIATE', 
  215, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=X48VuDVv0do'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'CI/CD Automation with GitHub Actions', 'cicd-automation-with-github-actions', 3, 'INTERMEDIATE', 'Automated testing, linting, semantic versioning, building Docker images, and deploying to staging/production on git push.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'cicd-automation-with-github-actions');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'GitHub Actions Quickstart & Syntax', 'Official guide to workflow YAML syntax, job matrices, runners, secrets management, and triggering conditions.', 'DOCUMENTATION', 
  'https://docs.github.com/en/actions', NULL, 'GitHub Docs', 
  'GitHub', NULL, 'INTERMEDIATE', 
  90, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://docs.github.com/en/actions'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'GitHub Actions CI/CD Complete Course', 'End-to-end practical walkthrough configuring continuous integration and automated deployment pipelines.', 'VIDEO', 
  'https://www.youtube.com/watch?v=R8_veQiYBjI', 'VIDEO', 'YouTube', 
  'freeCodeCamp', NULL, 'INTERMEDIATE', 
  120, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=R8_veQiYBjI'
);

-- Domain: Database Architecture, SQL & System Design
INSERT INTO domains (name, slug, description, difficulty, icon_url, banner_url, status, created_at, updated_at)
VALUES ('Database Architecture, SQL & System Design', 'database-architecture-sql-and-system-design', 'Deep dive into relational database modeling, indexing strategies, query execution plans, Redis caching, and scalable system design.', 'INTERMEDIATE', 'https://cdn-icons-png.flaticon.com/512/2906/2906274.png', 'https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=1200', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  description = VALUES(description),
  difficulty = VALUES(difficulty),
  icon_url = VALUES(icon_url),
  banner_url = VALUES(banner_url),
  status = VALUES(status),
  updated_at = NOW(6);

SET @domain_id = (SELECT id FROM domains WHERE slug = 'database-architecture-sql-and-system-design');

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'Advanced SQL & Relational Schema Design', 'advanced-sql-and-relational-schema-design', 1, 'INTERMEDIATE', 'Entity relationships, 3NF normalization, transactions (ACID), isolation levels, window functions, and CTEs.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'advanced-sql-and-relational-schema-design');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Use The Index, Luke! - SQL Indexing Guide', 'A database indexing guide for developers explaining B-tree internals, index scans, and query tuning.', 'ARTICLE', 
  'https://use-the-index-luke.com/', NULL, 'Use The Index, Luke', 
  'Markus Winand', NULL, 'INTERMEDIATE', 
  120, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://use-the-index-luke.com/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'SQL Indexing and Query Performance Course', 'In-depth video covering B-Trees, execution plans, table scans vs index seeks, and composite keys.', 'VIDEO', 
  'https://www.youtube.com/watch?v=HubezKbFL7E', 'VIDEO', 'YouTube', 
  'Hussein Nasser', NULL, 'INTERMEDIATE', 
  90, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=HubezKbFL7E'
);

INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, 'High-Performance Caching with Redis', 'high-performance-caching-with-redis', 2, 'INTERMEDIATE', 'Data structures (hashes, sets, sorted sets, streams), cache invalidation patterns, pub/sub, and distributed locks.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = 'high-performance-caching-with-redis');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Redis University & Official Documentation', 'Interactive courses and architecture guidelines for implementing high-throughput in-memory caching.', 'DOCUMENTATION', 
  'https://redis.io/docs/latest/', NULL, 'Redis.io', 
  'Redis Team', NULL, 'INTERMEDIATE', 
  90, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://redis.io/docs/latest/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Redis Crash Course for Backend Developers', 'Practical guide to setting up Redis, caching database queries, key expiration strategies, and pub/sub messaging.', 'VIDEO', 
  'https://www.youtube.com/watch?v=jgpVdJB2sKQ', 'VIDEO', 'YouTube', 
  'Web Dev Simplified', NULL, 'BEGINNER', 
  45, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=jgpVdJB2sKQ'
);

-- Topic in Domain 5: Spring Data JPA & Hibernate Persistence
INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (5, 'Spring Data JPA & Hibernate Persistence', 'spring-data-jpa-hibernate-persistence', 4, 'INTERMEDIATE', 'JPA entities, relationships (OneToMany, ManyToMany), Spring Data Repositories, custom JPQL queries, and N+1 query problem solutions.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = 5 AND slug = 'spring-data-jpa-hibernate-persistence');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Spring Data JPA Official Reference', 'Complete guide to repositories, derived queries, pagination, auditing, and entity specifications.', 'DOCUMENTATION', 
  'https://docs.spring.io/spring-data/jpa/reference/', NULL, 'Spring.io', 
  'VMware / Spring Team', NULL, 'INTERMEDIATE', 
  120, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://docs.spring.io/spring-data/jpa/reference/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Solving N+1 Query Problem in Hibernate and Spring Data', 'Deep explanation of EntityGraph, JOIN FETCH, and batch fetching to optimize Hibernate performance.', 'ARTICLE', 
  'https://vladmihalcea.com/n-plus-1-query-problem/', NULL, 'Vlad Mihalcea Blog', 
  'Vlad Mihalcea', NULL, 'ADVANCED', 
  40, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://vladmihalcea.com/n-plus-1-query-problem/'
);

-- Topic in Domain 5: Spring Security 6 & JWT Authentication
INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (5, 'Spring Security 6 & JWT Authentication', 'spring-security-6-and-jwt-authentication', 5, 'ADVANCED', 'Configuring SecurityFilterChain, password hashing with BCrypt, JWT token generation, role-based access control (RBAC), and CORS filters.', 'ACTIVE', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = NOW(6);

SET @topic_id = (SELECT id FROM topics WHERE domain_id = 5 AND slug = 'spring-security-6-and-jwt-authentication');

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Spring Security Architecture & Reference Guide', 'Comprehensive guide to filter chains, authentication managers, user details service, and method-level security.', 'DOCUMENTATION', 
  'https://docs.spring.io/spring-security/reference/', NULL, 'Spring.io', 
  'Spring Security Team', NULL, 'ADVANCED', 
  150, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://docs.spring.io/spring-security/reference/'
);

INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, 'Spring Boot 3 + Spring Security 6 + JWT Full Tutorial', 'Step-by-step walkthrough creating a secure REST API with JWT tokens, refresh tokens, and role-based permissions.', 'VIDEO', 
  'https://www.youtube.com/watch?v=KxqlJblhzfI', 'VIDEO', 'YouTube', 
  'Amigoscode', NULL, 'INTERMEDIATE', 
  110, 'en', 
  'PUBLISHED', 'ACTIVE', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = 'https://www.youtube.com/watch?v=KxqlJblhzfI'
);

-- Technology Update: React 19 Officially Released - Server Actions & Asset Loading
INSERT INTO technology_updates (
  domain_id, title, summary, content, source_name, source_url, 
  published_date, technology_version, update_type, external_reference, 
  status, created_at, updated_at
)
SELECT 
  (SELECT id FROM domains WHERE slug = 'frontend-engineering-and-web-development'), 'React 19 Officially Released - Server Actions & Asset Loading', 'React 19 brings stable Actions, Server Functions, document metadata support directly in JSX, and the React Compiler.', 'The React team has officially released React 19. Key improvements include the addition of useActionState, useOptimistic, native support for async scripts and stylesheet precedence, eliminating the need for external head management libraries.', 
  'React Blog', 'https://react.dev/blog/2024/12/05/react-19', '2026-09-15', 
  '19.0.0', 'RELEASE', 'https://github.com/facebook/react/releases', 
  'PUBLISHED', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM technology_updates WHERE title = 'React 19 Officially Released - Server Actions & Asset Loading'
);

-- Technology Update: Spring Boot 3.4 Highlights - Virtual Threads & GraalVM Native Image Enhancements
INSERT INTO technology_updates (
  domain_id, title, summary, content, source_name, source_url, 
  published_date, technology_version, update_type, external_reference, 
  status, created_at, updated_at
)
SELECT 
  5, 'Spring Boot 3.4 Highlights - Virtual Threads & GraalVM Native Image Enhancements', 'Spring Boot 3.4 brings enhanced observability, structured concurrency integration, and improved startup speed.', 'Spring Boot 3.4 delivers deeper support for Java 21+ features including Project Loom virtual threads, optimized Docker Compose integration for local development, and streamlined Spring Data repository configuration.', 
  'Spring Blog', 'https://spring.io/blog', '2026-09-28', 
  '3.4.0', 'FEATURE', 'https://github.com/spring-projects/spring-boot/releases', 
  'PUBLISHED', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM technology_updates WHERE title = 'Spring Boot 3.4 Highlights - Virtual Threads & GraalVM Native Image Enhancements'
);

-- Technology Update: Python 3.13 JIT Compiler & Free-Threaded GIL Removal
INSERT INTO technology_updates (
  domain_id, title, summary, content, source_name, source_url, 
  published_date, technology_version, update_type, external_reference, 
  status, created_at, updated_at
)
SELECT 
  (SELECT id FROM domains WHERE slug = 'python-ai-and-data-science'), 'Python 3.13 JIT Compiler & Free-Threaded GIL Removal', 'Experimental support for running without the Global Interpreter Lock (GIL) and a basic JIT compiler.', 'Python 3.13 introduces experimental free-threading mode (PEP 703) allowing genuine multi-core parallel execution in CPU-bound multi-threaded workloads, alongside a new interactive REPL with colorized syntax highlighting.', 
  'Python Release Notes', 'https://docs.python.org/3/whatsnew/3.13.html', '2026-10-01', 
  '3.13.0', 'RELEASE', 'https://www.python.org/downloads/release/python-3130/', 
  'PUBLISHED', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM technology_updates WHERE title = 'Python 3.13 JIT Compiler & Free-Threaded GIL Removal'
);

-- Roadmap: Modern Frontend Developer Career Path
INSERT INTO roadmaps (domain_id, title, description, status, created_at, updated_at)
SELECT (SELECT id FROM domains WHERE slug = 'frontend-engineering-and-web-development'), 'Modern Frontend Developer Career Path', 'Step-by-step sequential guide from modern styling fundamentals through reactive state and full-stack deployment.', 'PUBLISHED', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM roadmaps WHERE title = 'Modern Frontend Developer Career Path'
);

SET @roadmap_id = (SELECT id FROM roadmaps WHERE title = 'Modern Frontend Developer Career Path' LIMIT 1);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'html5-semantics-and-modern-css3' LIMIT 1), 'Master Modern Semantic HTML & CSS3', 'Learn responsive design, flexbox, grid, and CSS variables.', 1
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 1
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'modern-javascript-es6-and-beyond' LIMIT 1), 'Core & Asynchronous JavaScript', 'Deeply understand closures, event loops, promises, and modern ES features.', 2
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 2
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'typescript-in-production' LIMIT 1), 'Type-Safe Application Development', 'Learn TypeScript types, generics, and compiler configurations.', 3
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 3
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'react-19-and-modern-state-architecture' LIMIT 1), 'Production React 19 Development', 'Build scalable web components with hooks, state patterns, and Suspense.', 4
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 4
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'nextjs-and-fullstack-react-engineering' LIMIT 1), 'Next.js & Fullstack SSR Apps', 'Deploy fullstack applications with server components and edge rendering.', 5
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 5
);

-- Roadmap: Full Stack Java Developer Career Roadmap
INSERT INTO roadmaps (domain_id, title, description, status, created_at, updated_at)
SELECT 5, 'Full Stack Java Developer Career Roadmap', 'Comprehensive pathway to becoming an enterprise Java Full Stack engineer using Spring Boot and React.', 'PUBLISHED', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM roadmaps WHERE title = 'Full Stack Java Developer Career Roadmap'
);

SET @roadmap_id = (SELECT id FROM roadmaps WHERE title = 'Full Stack Java Developer Career Roadmap' LIMIT 1);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, 2, 'Core Java 21 & OOP Foundations', 'Master Java OOP, collections, records, and streams.', 1
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 1
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'spring-data-jpa-hibernate-persistence' LIMIT 1), 'Database Persistence with Spring Data JPA', 'Design relational models and optimize Hibernate queries.', 2
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 2
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, 3, 'Building REST APIs with Spring Boot 3', 'Develop robust enterprise microservice controllers and services.', 3
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 3
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'spring-security-6-and-jwt-authentication' LIMIT 1), 'Securing APIs with Spring Security & JWT', 'Implement authentication and role-based permissions.', 4
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 4
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, 5, 'Frontend Integration with React & TypeScript', 'Connect reactive user interfaces to Spring Boot REST endpoints.', 5
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 5
);

-- Roadmap: AI Engineer & Data Scientist Roadmap
INSERT INTO roadmaps (domain_id, title, description, status, created_at, updated_at)
SELECT (SELECT id FROM domains WHERE slug = 'python-ai-and-data-science'), 'AI Engineer & Data Scientist Roadmap', 'From programming fundamentals in Python to training neural networks and building production LLM apps.', 'PUBLISHED', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM roadmaps WHERE title = 'AI Engineer & Data Scientist Roadmap'
);

SET @roadmap_id = (SELECT id FROM roadmaps WHERE title = 'AI Engineer & Data Scientist Roadmap' LIMIT 1);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'python-312-core-and-idiomatic-code' LIMIT 1), 'Python Programming Mastery', 'Write clean, idiomatic Python code.', 1
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 1
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'data-wrangling-with-numpy-and-pandas' LIMIT 1), 'Exploratory Data Analysis', 'Transform, clean, and analyze datasets with Pandas and NumPy.', 2
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 2
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'deep-learning-foundations-with-pytorch' LIMIT 1), 'Deep Learning with PyTorch', 'Build neural network architectures, autograd, and training pipelines.', 3
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 3
);

INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, (SELECT id FROM topics WHERE slug = 'llms-prompt-engineering-and-langchain' LIMIT 1), 'Generative AI & LLM Systems', 'Build production RAG pipelines, agents, and vector database systems.', 4
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = 4
);
