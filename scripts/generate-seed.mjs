import fs from 'fs';
import path from 'path';
import { execSync } from 'child_process';

const sqlStatements = [];

function sqlEscape(str) {
  if (str === null || str === undefined) return 'NULL';
  return "'" + String(str).replace(/\\/g, '\\\\').replace(/'/g, "\\'") + "'";
}

function now() {
  return "NOW(6)";
}

// 1. Promote admin user & ensure enabled
sqlStatements.push(`
UPDATE users SET role = 'ADMIN', enabled = 1 WHERE email = 'admin@learningplatform.com';
UPDATE users SET enabled = 1 WHERE email = 'pavanyendluru@gmail.com';
`);

// Definition of domains, topics, resources
const domains = [
  {
    name: "Frontend Engineering & Web Development",
    slug: "frontend-engineering-and-web-development",
    description: "Master modern client-side engineering with HTML5, CSS3, modern JavaScript (ESNext), TypeScript, React 19, Next.js, and web performance optimization.",
    difficulty: "BEGINNER",
    iconUrl: "https://cdn-icons-png.flaticon.com/512/1055/1055687.png",
    bannerUrl: "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=1200",
    status: "ACTIVE",
    topics: [
      {
        name: "HTML5 Semantics & Modern CSS3 Mastery",
        slug: "html5-semantics-and-modern-css3",
        sequenceNumber: 1,
        difficulty: "BEGINNER",
        description: "Modern layout techniques including CSS Grid, Flexbox, responsive design, CSS variables, and modern animations.",
        resources: [
          {
            title: "MDN Web Docs - Learn CSS",
            description: "The definitive guide to learning CSS from basics to advanced layout techniques and styling.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://developer.mozilla.org/en-US/docs/Learn/CSS",
            sourceName: "Mozilla MDN",
            author: "MDN Contributors",
            publisher: "Mozilla",
            difficulty: "BEGINNER",
            durationMinutes: 180,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "CSS Grid & Flexbox Crash Course",
            description: "A complete visual walkthrough of building modern responsive layouts using Flexbox and Grid.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=jV8B24rSN5o",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Traversy Media",
            difficulty: "BEGINNER",
            durationMinutes: 65,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Web.dev Learn CSS Course",
            description: "Google's comprehensive evergreen CSS course and reference to level up styling fundamentals.",
            resourceType: "COURSE",
            externalUrl: "https://web.dev/learn/css",
            sourceName: "web.dev",
            author: "Google Chrome Team",
            publisher: "Google",
            difficulty: "BEGINNER",
            durationMinutes: 240,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "Modern JavaScript (ES6+ & Beyond)",
        slug: "modern-javascript-es6-and-beyond",
        sequenceNumber: 2,
        difficulty: "BEGINNER",
        description: "Deep dive into JS fundamentals: closures, prototypes, asynchronous JavaScript (Promises, async/await), and event loop internals.",
        resources: [
          {
            title: "The Modern JavaScript Tutorial (javascript.info)",
            description: "From the basics to advanced topics with simple, but detailed explanations, tasks and playground.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://javascript.info/",
            sourceName: "JavaScript.info",
            author: "Ilya Kantor",
            difficulty: "BEGINNER",
            durationMinutes: 300,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "JavaScript Event Loop and Concurrency Explained",
            description: "Essential deep dive on how the browser JavaScript runtime and microtask queue handle asynchronous execution.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=8aGhZQkoFbQ",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Philip Roberts",
            difficulty: "INTERMEDIATE",
            durationMinutes: 30,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "You Don't Know JS Yet (Book Series)",
            description: "Deep dive book series exploring the core mechanisms of JavaScript without syntactic hand-waving.",
            resourceType: "BOOK",
            externalUrl: "https://github.com/getify/You-Dont-Know-JS",
            sourceName: "GitHub",
            author: "Kyle Simpson",
            difficulty: "INTERMEDIATE",
            durationMinutes: 360,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "TypeScript in Production",
        slug: "typescript-in-production",
        sequenceNumber: 3,
        difficulty: "INTERMEDIATE",
        description: "Type safety, generics, union types, discriminated unions, utility types, and strict tsconfig configuration.",
        resources: [
          {
            title: "Official TypeScript Handbook",
            description: "The authoritative manual for learning TypeScript language features, typing systems, and type manipulation.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://www.typescriptlang.org/docs/handbook/intro.html",
            sourceName: "TypeScript Official",
            author: "Microsoft",
            publisher: "Microsoft",
            difficulty: "INTERMEDIATE",
            durationMinutes: 180,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "TypeScript Full Course for Beginners",
            description: "Comprehensive introduction to TypeScript fundamentals, compiler configurations, and typed React components.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=gieEQFIfgYc",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "freeCodeCamp",
            difficulty: "BEGINNER",
            durationMinutes: 120,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Total TypeScript Essentials",
            description: "Interactive patterns and real-world tips for solving complex generic types and compiler narrowing in TypeScript.",
            resourceType: "ARTICLE",
            externalUrl: "https://www.totaltypescript.com/tutorials",
            sourceName: "Total TypeScript",
            author: "Matt Pocock",
            difficulty: "ADVANCED",
            durationMinutes: 90,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "React 19 & Modern State Architecture",
        slug: "react-19-and-modern-state-architecture",
        sequenceNumber: 4,
        difficulty: "INTERMEDIATE",
        description: "Master React 19 hooks, compiler optimizations, Suspense, Error Boundaries, custom hooks, and state management.",
        resources: [
          {
            title: "React.dev - The Official Documentation",
            description: "Modern interactive React guide covering functional components, hooks, component lifecycles, and best practices.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://react.dev/",
            sourceName: "React Official",
            author: "Meta React Team",
            publisher: "Meta",
            difficulty: "INTERMEDIATE",
            durationMinutes: 240,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "React 19 New Features & Upgrades Deep Dive",
            description: "A thorough look into React 19 additions: useActionState, useFormStatus, useOptimistic, and the React Compiler.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=0vl_c3i6Y9w",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Jack Herrington",
            difficulty: "INTERMEDIATE",
            durationMinutes: 45,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Awesome React Ecosystem GitHub",
            description: "Curated collection of state management, UI component libraries, form validators, and React design patterns.",
            resourceType: "GITHUB",
            externalUrl: "https://github.com/enaqx/awesome-react",
            sourceName: "GitHub",
            author: "Community",
            difficulty: "INTERMEDIATE",
            durationMinutes: 60,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "Next.js & Full-Stack React Engineering",
        slug: "nextjs-and-fullstack-react-engineering",
        sequenceNumber: 5,
        difficulty: "ADVANCED",
        description: "Next.js App Router, React Server Components (RSC), Server Actions, Route Handlers, middleware, and caching.",
        resources: [
          {
            title: "Next.js Learn App Router Course",
            description: "Hands-on guided walkthrough building a production dashboard with Next.js App Router, authentication, and database querying.",
            resourceType: "COURSE",
            externalUrl: "https://nextjs.org/learn",
            sourceName: "Next.js Official",
            author: "Vercel",
            publisher: "Vercel",
            difficulty: "INTERMEDIATE",
            durationMinutes: 200,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Next.js 15 / 14 Full Tutorial & Production Project",
            description: "End-to-end fullstack web development tutorial covering server actions, database integration, and UI styling.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=wm5gMKuwSYk",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "JavaScript Mastery",
            difficulty: "ADVANCED",
            durationMinutes: 180,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      }
    ]
  },
  {
    name: "Python, AI & Data Science",
    slug: "python-ai-and-data-science",
    description: "Learn Python from fundamentals to advanced machine learning, deep learning with PyTorch, LangChain, and Generative AI applications.",
    difficulty: "INTERMEDIATE",
    iconUrl: "https://cdn-icons-png.flaticon.com/512/5968/5968350.png",
    bannerUrl: "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=1200",
    status: "ACTIVE",
    topics: [
      {
        name: "Python 3.12 Core & Idiomatic Code",
        slug: "python-312-core-and-idiomatic-code",
        sequenceNumber: 1,
        difficulty: "BEGINNER",
        description: "Syntax, data structures, generators, decorators, context managers, and object-oriented Python.",
        resources: [
          {
            title: "Official Python Documentation & Tutorial",
            description: "The official guide covering Python data structures, standard library, modules, and error handling.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://docs.python.org/3/tutorial/",
            sourceName: "Python.org",
            author: "Python Software Foundation",
            difficulty: "BEGINNER",
            durationMinutes: 150,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Python for Beginners - Full Course",
            description: "Comprehensive 6-hour Python beginner to intermediate tutorial covering core coding and real-world examples.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=_uQrJ0TkZlc",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Programming with Mosh",
            difficulty: "BEGINNER",
            durationMinutes: 360,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "Data Wrangling with NumPy & Pandas",
        slug: "data-wrangling-with-numpy-and-pandas",
        sequenceNumber: 2,
        difficulty: "INTERMEDIATE",
        description: "Vectorized computation, series and dataframes, aggregation, joins, data cleaning, and exploratory data analysis.",
        resources: [
          {
            title: "Pandas User Guide & 10 Minutes to Pandas",
            description: "Essential official cookbook for tabular data manipulation, filtering, reshaping, and time-series handling.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://pandas.pydata.org/docs/user_guide/10min.html",
            sourceName: "Pandas Dev Team",
            difficulty: "INTERMEDIATE",
            durationMinutes: 120,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "NumPy & Pandas Full Bootcamp",
            description: "Hands-on data analysis course working through real financial and user behavioral datasets.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=vmEHCJofslg",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "freeCodeCamp",
            difficulty: "INTERMEDIATE",
            durationMinutes: 240,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "Deep Learning Foundations with PyTorch",
        slug: "deep-learning-foundations-with-pytorch",
        sequenceNumber: 3,
        difficulty: "ADVANCED",
        description: "Tensors, autograd, building feedforward neural networks, CNNs, RNNs, and training loops on GPU.",
        resources: [
          {
            title: "PyTorch Official 60-Minute Blitz",
            description: "The fastest way to understand PyTorch tensors, autograd engine, and training your first neural network.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://pytorch.org/tutorials/beginner/deep_learning_60min_blitz.html",
            sourceName: "PyTorch.org",
            author: "PyTorch Team",
            publisher: "Linux Foundation",
            difficulty: "ADVANCED",
            durationMinutes: 90,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Neural Networks: Zero to Hero by Andrej Karpathy",
            description: "Masterclass video series building micrograd, backpropagation, and transformer language models from scratch.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/playlist?list=PLAqhIrjkxbuWI23v9cThsA9GvCAUhRvKZ",
            youtubeContentType: "PLAYLIST",
            sourceName: "YouTube",
            author: "Andrej Karpathy",
            difficulty: "ADVANCED",
            durationMinutes: 480,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "LLMs, Prompt Engineering & LangChain",
        slug: "llms-prompt-engineering-and-langchain",
        sequenceNumber: 4,
        difficulty: "ADVANCED",
        description: "Building production RAG (Retrieval-Augmented Generation) systems, vector databases, tool calling, and autonomous agents.",
        resources: [
          {
            title: "LangChain Documentation & Architecture",
            description: "Building context-aware LLM applications with chains, agents, memory, and vector store retrievers.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://python.langchain.com/docs/get_started/introduction",
            sourceName: "LangChain",
            author: "Harrison Chase",
            difficulty: "ADVANCED",
            durationMinutes: 120,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Build a Full RAG AI Application from Scratch",
            description: "Step-by-step tutorial creating a vector search application with embeddings, OpenAI/Gemini, and LangChain.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=2TJxpyO3ei4",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "FreeCodeCamp",
            difficulty: "ADVANCED",
            durationMinutes: 110,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      }
    ]
  },
  {
    name: "Cloud Computing, DevOps & Kubernetes",
    slug: "cloud-computing-devops-and-kubernetes",
    description: "Master modern DevOps engineering: Linux systems, Docker containerization, Kubernetes orchestration, CI/CD with GitHub Actions, and Terraform.",
    difficulty: "INTERMEDIATE",
    iconUrl: "https://cdn-icons-png.flaticon.com/512/5968/5968853.png",
    bannerUrl: "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200",
    status: "ACTIVE",
    topics: [
      {
        name: "Docker Containerization & Production Images",
        slug: "docker-containerization-and-production-images",
        sequenceNumber: 1,
        difficulty: "BEGINNER",
        description: "Container fundamentals, Dockerfile multi-stage builds, layers caching, container networking, and docker-compose.",
        resources: [
          {
            title: "Docker Get Started Guide",
            description: "Official hands-on guide for containerizing backend applications, managing volumes, and structuring containers.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://docs.docker.com/get-started/",
            sourceName: "Docker Docs",
            author: "Docker Inc",
            difficulty: "BEGINNER",
            durationMinutes: 90,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Docker Crash Course for Web Developers",
            description: "Comprehensive guide to writing optimal Dockerfiles, reducing image sizes, and running multi-container stacks.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=pg19Z8LL06w",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Traversy Media",
            difficulty: "BEGINNER",
            durationMinutes: 75,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "Kubernetes (K8s) Cluster Orchestration",
        slug: "kubernetes-k8s-cluster-orchestration",
        sequenceNumber: 2,
        difficulty: "ADVANCED",
        description: "Managing containerized applications at scale: Pods, Deployments, Services, Ingress, ConfigMaps, Secrets, and Helm charts.",
        resources: [
          {
            title: "Kubernetes Official Documentation & Interactive Tutorials",
            description: "Concepts and interactive katacoda scenarios for deploying resilient cluster workloads.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://kubernetes.io/docs/home/",
            sourceName: "Kubernetes.io",
            author: "CNCF",
            difficulty: "ADVANCED",
            durationMinutes: 200,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Kubernetes Tutorial for Beginners [Full Course]",
            description: "Complete 4-hour breakdown of architectural components, kubectl commands, services, and production deployment.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=X48VuDVv0do",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "TechWorld with Nana",
            difficulty: "INTERMEDIATE",
            durationMinutes: 215,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "CI/CD Automation with GitHub Actions",
        slug: "cicd-automation-with-github-actions",
        sequenceNumber: 3,
        difficulty: "INTERMEDIATE",
        description: "Automated testing, linting, semantic versioning, building Docker images, and deploying to staging/production on git push.",
        resources: [
          {
            title: "GitHub Actions Quickstart & Syntax",
            description: "Official guide to workflow YAML syntax, job matrices, runners, secrets management, and triggering conditions.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://docs.github.com/en/actions",
            sourceName: "GitHub Docs",
            author: "GitHub",
            difficulty: "INTERMEDIATE",
            durationMinutes: 90,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "GitHub Actions CI/CD Complete Course",
            description: "End-to-end practical walkthrough configuring continuous integration and automated deployment pipelines.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=R8_veQiYBjI",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "freeCodeCamp",
            difficulty: "INTERMEDIATE",
            durationMinutes: 120,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      }
    ]
  },
  {
    name: "Database Architecture, SQL & System Design",
    slug: "database-architecture-sql-and-system-design",
    description: "Deep dive into relational database modeling, indexing strategies, query execution plans, Redis caching, and scalable system design.",
    difficulty: "INTERMEDIATE",
    iconUrl: "https://cdn-icons-png.flaticon.com/512/2906/2906274.png",
    bannerUrl: "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=1200",
    status: "ACTIVE",
    topics: [
      {
        name: "Advanced SQL & Relational Schema Design",
        slug: "advanced-sql-and-relational-schema-design",
        sequenceNumber: 1,
        difficulty: "INTERMEDIATE",
        description: "Entity relationships, 3NF normalization, transactions (ACID), isolation levels, window functions, and CTEs.",
        resources: [
          {
            title: "Use The Index, Luke! - SQL Indexing Guide",
            description: "A database indexing guide for developers explaining B-tree internals, index scans, and query tuning.",
            resourceType: "ARTICLE",
            externalUrl: "https://use-the-index-luke.com/",
            sourceName: "Use The Index, Luke",
            author: "Markus Winand",
            difficulty: "INTERMEDIATE",
            durationMinutes: 120,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "SQL Indexing and Query Performance Course",
            description: "In-depth video covering B-Trees, execution plans, table scans vs index seeks, and composite keys.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=HubezKbFL7E",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Hussein Nasser",
            difficulty: "INTERMEDIATE",
            durationMinutes: 90,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      },
      {
        name: "High-Performance Caching with Redis",
        slug: "high-performance-caching-with-redis",
        sequenceNumber: 2,
        difficulty: "INTERMEDIATE",
        description: "Data structures (hashes, sets, sorted sets, streams), cache invalidation patterns, pub/sub, and distributed locks.",
        resources: [
          {
            title: "Redis University & Official Documentation",
            description: "Interactive courses and architecture guidelines for implementing high-throughput in-memory caching.",
            resourceType: "DOCUMENTATION",
            externalUrl: "https://redis.io/docs/latest/",
            sourceName: "Redis.io",
            author: "Redis Team",
            difficulty: "INTERMEDIATE",
            durationMinutes: 90,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          },
          {
            title: "Redis Crash Course for Backend Developers",
            description: "Practical guide to setting up Redis, caching database queries, key expiration strategies, and pub/sub messaging.",
            resourceType: "VIDEO",
            externalUrl: "https://www.youtube.com/watch?v=jgpVdJB2sKQ",
            youtubeContentType: "VIDEO",
            sourceName: "YouTube",
            author: "Web Dev Simplified",
            difficulty: "BEGINNER",
            durationMinutes: 45,
            language: "en",
            status: "PUBLISHED",
            verificationStatus: "ACTIVE"
          }
        ]
      }
    ]
  }
];

// Additional topics and resources for existing Domain 5 (Java Full Stack Web Development)
const existingDomainTopics = [
  {
    domainId: 5,
    name: "Spring Data JPA & Hibernate Persistence",
    slug: "spring-data-jpa-hibernate-persistence",
    sequenceNumber: 4,
    difficulty: "INTERMEDIATE",
    description: "JPA entities, relationships (OneToMany, ManyToMany), Spring Data Repositories, custom JPQL queries, and N+1 query problem solutions.",
    resources: [
      {
        title: "Spring Data JPA Official Reference",
        description: "Complete guide to repositories, derived queries, pagination, auditing, and entity specifications.",
        resourceType: "DOCUMENTATION",
        externalUrl: "https://docs.spring.io/spring-data/jpa/reference/",
        sourceName: "Spring.io",
        author: "VMware / Spring Team",
        difficulty: "INTERMEDIATE",
        durationMinutes: 120,
        language: "en",
        status: "PUBLISHED",
        verificationStatus: "ACTIVE"
      },
      {
        title: "Solving N+1 Query Problem in Hibernate and Spring Data",
        description: "Deep explanation of EntityGraph, JOIN FETCH, and batch fetching to optimize Hibernate performance.",
        resourceType: "ARTICLE",
        externalUrl: "https://vladmihalcea.com/n-plus-1-query-problem/",
        sourceName: "Vlad Mihalcea Blog",
        author: "Vlad Mihalcea",
        difficulty: "ADVANCED",
        durationMinutes: 40,
        language: "en",
        status: "PUBLISHED",
        verificationStatus: "ACTIVE"
      }
    ]
  },
  {
    domainId: 5,
    name: "Spring Security 6 & JWT Authentication",
    slug: "spring-security-6-and-jwt-authentication",
    sequenceNumber: 5,
    difficulty: "ADVANCED",
    description: "Configuring SecurityFilterChain, password hashing with BCrypt, JWT token generation, role-based access control (RBAC), and CORS filters.",
    resources: [
      {
        title: "Spring Security Architecture & Reference Guide",
        description: "Comprehensive guide to filter chains, authentication managers, user details service, and method-level security.",
        resourceType: "DOCUMENTATION",
        externalUrl: "https://docs.spring.io/spring-security/reference/",
        sourceName: "Spring.io",
        author: "Spring Security Team",
        difficulty: "ADVANCED",
        durationMinutes: 150,
        language: "en",
        status: "PUBLISHED",
        verificationStatus: "ACTIVE"
      },
      {
        title: "Spring Boot 3 + Spring Security 6 + JWT Full Tutorial",
        description: "Step-by-step walkthrough creating a secure REST API with JWT tokens, refresh tokens, and role-based permissions.",
        resourceType: "VIDEO",
        externalUrl: "https://www.youtube.com/watch?v=KxqlJblhzfI",
        youtubeContentType: "VIDEO",
        sourceName: "YouTube",
        author: "Amigoscode",
        difficulty: "INTERMEDIATE",
        durationMinutes: 110,
        language: "en",
        status: "PUBLISHED",
        verificationStatus: "ACTIVE"
      }
    ]
  }
];

// Technology Updates to seed
const techUpdates = [
  {
    domainSlug: "frontend-engineering-and-web-development",
    title: "React 19 Officially Released - Server Actions & Asset Loading",
    summary: "React 19 brings stable Actions, Server Functions, document metadata support directly in JSX, and the React Compiler.",
    content: "The React team has officially released React 19. Key improvements include the addition of useActionState, useOptimistic, native support for async scripts and stylesheet precedence, eliminating the need for external head management libraries.",
    sourceName: "React Blog",
    sourceUrl: "https://react.dev/blog/2024/12/05/react-19",
    publishedDate: "2026-09-15",
    technologyVersion: "19.0.0",
    updateType: "RELEASE",
    externalReference: "https://github.com/facebook/react/releases",
    status: "PUBLISHED"
  },
  {
    domainId: 5, // Java Full Stack
    title: "Spring Boot 3.4 Highlights - Virtual Threads & GraalVM Native Image Enhancements",
    summary: "Spring Boot 3.4 brings enhanced observability, structured concurrency integration, and improved startup speed.",
    content: "Spring Boot 3.4 delivers deeper support for Java 21+ features including Project Loom virtual threads, optimized Docker Compose integration for local development, and streamlined Spring Data repository configuration.",
    sourceName: "Spring Blog",
    sourceUrl: "https://spring.io/blog",
    publishedDate: "2026-09-28",
    technologyVersion: "3.4.0",
    updateType: "FEATURE",
    externalReference: "https://github.com/spring-projects/spring-boot/releases",
    status: "PUBLISHED"
  },
  {
    domainSlug: "python-ai-and-data-science",
    title: "Python 3.13 JIT Compiler & Free-Threaded GIL Removal",
    summary: "Experimental support for running without the Global Interpreter Lock (GIL) and a basic JIT compiler.",
    content: "Python 3.13 introduces experimental free-threading mode (PEP 703) allowing genuine multi-core parallel execution in CPU-bound multi-threaded workloads, alongside a new interactive REPL with colorized syntax highlighting.",
    sourceName: "Python Release Notes",
    sourceUrl: "https://docs.python.org/3/whatsnew/3.13.html",
    publishedDate: "2026-10-01",
    technologyVersion: "3.13.0",
    updateType: "RELEASE",
    externalReference: "https://www.python.org/downloads/release/python-3130/",
    status: "PUBLISHED"
  }
];

// Roadmaps to seed
const roadmaps = [
  {
    domainSlug: "frontend-engineering-and-web-development",
    title: "Modern Frontend Developer Career Path",
    description: "Step-by-step sequential guide from modern styling fundamentals through reactive state and full-stack deployment.",
    status: "PUBLISHED",
    steps: [
      { topicSlug: "html5-semantics-and-modern-css3", title: "Master Modern Semantic HTML & CSS3", description: "Learn responsive design, flexbox, grid, and CSS variables.", stepOrder: 1 },
      { topicSlug: "modern-javascript-es6-and-beyond", title: "Core & Asynchronous JavaScript", description: "Deeply understand closures, event loops, promises, and modern ES features.", stepOrder: 2 },
      { topicSlug: "typescript-in-production", title: "Type-Safe Application Development", description: "Learn TypeScript types, generics, and compiler configurations.", stepOrder: 3 },
      { topicSlug: "react-19-and-modern-state-architecture", title: "Production React 19 Development", description: "Build scalable web components with hooks, state patterns, and Suspense.", stepOrder: 4 },
      { topicSlug: "nextjs-and-fullstack-react-engineering", title: "Next.js & Fullstack SSR Apps", description: "Deploy fullstack applications with server components and edge rendering.", stepOrder: 5 }
    ]
  },
  {
    domainId: 5,
    title: "Full Stack Java Developer Career Roadmap",
    description: "Comprehensive pathway to becoming an enterprise Java Full Stack engineer using Spring Boot and React.",
    status: "PUBLISHED",
    steps: [
      { topicId: 2, title: "Core Java 21 & OOP Foundations", description: "Master Java OOP, collections, records, and streams.", stepOrder: 1 },
      { topicSlug: "spring-data-jpa-hibernate-persistence", title: "Database Persistence with Spring Data JPA", description: "Design relational models and optimize Hibernate queries.", stepOrder: 2 },
      { topicId: 3, title: "Building REST APIs with Spring Boot 3", description: "Develop robust enterprise microservice controllers and services.", stepOrder: 3 },
      { topicSlug: "spring-security-6-and-jwt-authentication", title: "Securing APIs with Spring Security & JWT", description: "Implement authentication and role-based permissions.", stepOrder: 4 },
      { topicId: 5, title: "Frontend Integration with React & TypeScript", description: "Connect reactive user interfaces to Spring Boot REST endpoints.", stepOrder: 5 }
    ]
  },
  {
    domainSlug: "python-ai-and-data-science",
    title: "AI Engineer & Data Scientist Roadmap",
    description: "From programming fundamentals in Python to training neural networks and building production LLM apps.",
    status: "PUBLISHED",
    steps: [
      { topicSlug: "python-312-core-and-idiomatic-code", title: "Python Programming Mastery", description: "Write clean, idiomatic Python code.", stepOrder: 1 },
      { topicSlug: "data-wrangling-with-numpy-and-pandas", title: "Exploratory Data Analysis", description: "Transform, clean, and analyze datasets with Pandas and NumPy.", stepOrder: 2 },
      { topicSlug: "deep-learning-foundations-with-pytorch", title: "Deep Learning with PyTorch", description: "Build neural network architectures, autograd, and training pipelines.", stepOrder: 3 },
      { topicSlug: "llms-prompt-engineering-and-langchain", title: "Generative AI & LLM Systems", description: "Build production RAG pipelines, agents, and vector database systems.", stepOrder: 4 }
    ]
  }
];

// Build the SQL script
let sql = `
-- ==========================================================
-- Learning Platform Comprehensive Seed Data
-- ==========================================================
USE \`learning_platform\`;

-- 1. Ensure Admin Account is Configured and Active
UPDATE users SET role = 'ADMIN', enabled = 1 WHERE email = 'admin@learningplatform.com';
UPDATE users SET enabled = 1 WHERE email = 'pavanyendluru@gmail.com';

`;

// Generate domain inserts
for (const d of domains) {
  sql += `
-- Domain: ${d.name}
INSERT INTO domains (name, slug, description, difficulty, icon_url, banner_url, status, created_at, updated_at)
VALUES (${sqlEscape(d.name)}, ${sqlEscape(d.slug)}, ${sqlEscape(d.description)}, ${sqlEscape(d.difficulty)}, ${sqlEscape(d.iconUrl)}, ${sqlEscape(d.bannerUrl)}, ${sqlEscape(d.status)}, ${now()}, ${now()})
ON DUPLICATE KEY UPDATE 
  description = VALUES(description),
  difficulty = VALUES(difficulty),
  icon_url = VALUES(icon_url),
  banner_url = VALUES(banner_url),
  status = VALUES(status),
  updated_at = ${now()};

SET @domain_id = (SELECT id FROM domains WHERE slug = ${sqlEscape(d.slug)});
`;

  // Topics for this domain
  for (const t of d.topics) {
    sql += `
INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (@domain_id, ${sqlEscape(t.name)}, ${sqlEscape(t.slug)}, ${t.sequenceNumber}, ${sqlEscape(t.difficulty)}, ${sqlEscape(t.description)}, 'ACTIVE', ${now()}, ${now()})
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = ${now()};

SET @topic_id = (SELECT id FROM topics WHERE domain_id = @domain_id AND slug = ${sqlEscape(t.slug)});
`;

    // Resources for this topic
    for (const r of t.resources) {
      sql += `
INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, ${sqlEscape(r.title)}, ${sqlEscape(r.description)}, ${sqlEscape(r.resourceType)}, 
  ${sqlEscape(r.externalUrl)}, ${sqlEscape(r.youtubeContentType)}, ${sqlEscape(r.sourceName)}, 
  ${sqlEscape(r.author)}, ${sqlEscape(r.publisher)}, ${sqlEscape(r.difficulty)}, 
  ${r.durationMinutes ?? 'NULL'}, ${sqlEscape(r.language ?? 'en')}, 
  ${sqlEscape(r.status)}, ${sqlEscape(r.verificationStatus)}, ${now()}, ${now()}
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = ${sqlEscape(r.externalUrl)}
);
`;
    }
  }
}

// Existing Domain 5 additions
for (const t of existingDomainTopics) {
  sql += `
-- Topic in Domain ${t.domainId}: ${t.name}
INSERT INTO topics (domain_id, name, slug, sequence_number, difficulty, description, status, created_at, updated_at)
VALUES (${t.domainId}, ${sqlEscape(t.name)}, ${sqlEscape(t.slug)}, ${t.sequenceNumber}, ${sqlEscape(t.difficulty)}, ${sqlEscape(t.description)}, 'ACTIVE', ${now()}, ${now()})
ON DUPLICATE KEY UPDATE 
  name = VALUES(name),
  sequence_number = VALUES(sequence_number),
  difficulty = VALUES(difficulty),
  description = VALUES(description),
  status = 'ACTIVE',
  updated_at = ${now()};

SET @topic_id = (SELECT id FROM topics WHERE domain_id = ${t.domainId} AND slug = ${sqlEscape(t.slug)});
`;

  for (const r of t.resources) {
    sql += `
INSERT INTO resources (
  topic_id, title, description, resource_type, external_url, youtube_content_type, 
  source_name, author, publisher, difficulty, duration_minutes, language, 
  status, verification_status, created_at, updated_at
)
SELECT 
  @topic_id, ${sqlEscape(r.title)}, ${sqlEscape(r.description)}, ${sqlEscape(r.resourceType)}, 
  ${sqlEscape(r.externalUrl)}, ${sqlEscape(r.youtubeContentType)}, ${sqlEscape(r.sourceName)}, 
  ${sqlEscape(r.author)}, ${sqlEscape(r.publisher)}, ${sqlEscape(r.difficulty)}, 
  ${r.durationMinutes ?? 'NULL'}, ${sqlEscape(r.language ?? 'en')}, 
  ${sqlEscape(r.status)}, ${sqlEscape(r.verificationStatus)}, ${now()}, ${now()}
WHERE NOT EXISTS (
  SELECT 1 FROM resources WHERE topic_id = @topic_id AND external_url = ${sqlEscape(r.externalUrl)}
);
`;
  }
}

// Technology Updates
for (const u of techUpdates) {
  const domainIdExpr = u.domainId ? `${u.domainId}` : `(SELECT id FROM domains WHERE slug = ${sqlEscape(u.domainSlug)})`;
  sql += `
-- Technology Update: ${u.title}
INSERT INTO technology_updates (
  domain_id, title, summary, content, source_name, source_url, 
  published_date, technology_version, update_type, external_reference, 
  status, created_at, updated_at
)
SELECT 
  ${domainIdExpr}, ${sqlEscape(u.title)}, ${sqlEscape(u.summary)}, ${sqlEscape(u.content)}, 
  ${sqlEscape(u.sourceName)}, ${sqlEscape(u.sourceUrl)}, ${sqlEscape(u.publishedDate)}, 
  ${sqlEscape(u.technologyVersion)}, ${sqlEscape(u.updateType)}, ${sqlEscape(u.externalReference)}, 
  ${sqlEscape(u.status)}, ${now()}, ${now()}
WHERE NOT EXISTS (
  SELECT 1 FROM technology_updates WHERE title = ${sqlEscape(u.title)}
);
`;
}

// Roadmaps and Steps
for (const rm of roadmaps) {
  const domainIdExpr = rm.domainId ? `${rm.domainId}` : `(SELECT id FROM domains WHERE slug = ${sqlEscape(rm.domainSlug)})`;
  sql += `
-- Roadmap: ${rm.title}
INSERT INTO roadmaps (domain_id, title, description, status, created_at, updated_at)
SELECT ${domainIdExpr}, ${sqlEscape(rm.title)}, ${sqlEscape(rm.description)}, ${sqlEscape(rm.status)}, ${now()}, ${now()}
WHERE NOT EXISTS (
  SELECT 1 FROM roadmaps WHERE title = ${sqlEscape(rm.title)}
);

SET @roadmap_id = (SELECT id FROM roadmaps WHERE title = ${sqlEscape(rm.title)} LIMIT 1);
`;

  for (const step of rm.steps) {
    const topicIdExpr = step.topicId ? `${step.topicId}` : `(SELECT id FROM topics WHERE slug = ${sqlEscape(step.topicSlug)} LIMIT 1)`;
    sql += `
INSERT INTO roadmap_steps (roadmap_id, topic_id, title, description, step_order)
SELECT @roadmap_id, ${topicIdExpr}, ${sqlEscape(step.title)}, ${sqlEscape(step.description)}, ${step.stepOrder}
WHERE NOT EXISTS (
  SELECT 1 FROM roadmap_steps WHERE roadmap_id = @roadmap_id AND step_order = ${step.stepOrder}
);
`;
  }
}

// Write the SQL file
const outputPath = path.join(process.cwd(), 'scripts', 'seed.sql');
fs.writeFileSync(outputPath, sql, 'utf-8');
console.log(`Generated SQL seed script at: ${outputPath}`);
console.log(`Statements count: ${sql.split(';').length}`);
