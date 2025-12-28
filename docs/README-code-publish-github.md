# 📤 Pushing the Code to GitHub

This project can be pushed to GitHub using standard Git commands.

### Prerequisites
- Git installed locally
- A GitHub repository created (empty)

Repository URL:
```jsunicoderegexp
https://github.com/ksktechai/llamacpp-rag.git
```

---

### Initial push (first time only)

Run the following commands from the project root:

```bash
# Initialize a new Git repository
git init

# Ensure the default branch is 'main'
git branch -M main

# Stage all files
git add .

# Create the initial commit
git commit -m "Initial commit: llama.cpp RAG application"

# Add the GitHub repository as the remote
git remote add origin https://github.com/ksktechai/llamacpp-rag.git

# Push the code to GitHub
git push -u origin main
```
---
### Subsequent updates

After making changes:
```bash
git add .
git commit -m "Describe your change"
git push
```
---