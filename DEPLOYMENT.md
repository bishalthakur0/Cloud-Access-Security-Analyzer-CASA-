# 🚀 CASA - Public Deployment Guide

## Live Demo URL (After Deployment)

**Your application will be live at:**
```
https://casa-security-analyzer.onrender.com
```

---

## 🌐 Deploy to Render (FREE - Recommended)

### Step 1: Sign Up for Render
1. Go to: https://render.com
2. Click "Get Started for Free"
3. Sign up with GitHub account (easiest)

### Step 2: Deploy from GitHub

1. **Dashboard** → Click "New +"
2. Select **"Web Service"**
3. **Connect GitHub Repository:**
   - Click "Connect account" (if first time)
   - Search for: `Cloud-Access-Security-Analyzer-CASA-`
   - Click "Connect"

4. **Configure Service:**
   - **Name:** `casa-security-analyzer`
   - **Region:** Oregon (US West) - Free tier
   - **Branch:** `main`
   - **Runtime:** Docker
   - **Instance Type:** Free

5. **Environment Variables** (Optional):
   ```
   SPRING_PROFILES_ACTIVE=production
   SERVER_PORT=8080
   ```

6. **Click "Create Web Service"**

### Step 3: Wait for Deployment
- First deployment takes 5-10 minutes
- Render will:
  - ✅ Pull your code from GitHub
  - ✅ Build Docker image
  - ✅ Deploy container
  - ✅ Assign public URL

### Step 4: Access Your Live Application
```
https://casa-security-analyzer.onrender.com
```

---

## 🎯 Alternative: Deploy to Railway (FREE)

### Step 1: Sign Up
1. Go to: https://railway.app
2. Sign in with GitHub

### Step 2: Deploy
1. Click "New Project"
2. Select "Deploy from GitHub repo"
3. Choose: `Cloud-Access-Security-Analyzer-CASA-`
4. Railway auto-detects Dockerfile
5. Click "Deploy"

### Step 3: Get Public URL
1. Go to Settings → Networking
2. Click "Generate Domain"
3. Your app will be at: `https://casa-production.up.railway.app`

---

## 📝 For Your Resume

### Live Demo Link
```
🔗 Live Demo: https://casa-security-analyzer.onrender.com
🔗 GitHub: https://github.com/bishalthakur0/Cloud-Access-Security-Analyzer-CASA-
```

### Project Description
```
CASA - Cloud Access Security Analyzer
• Developed Zero Trust security platform with sensitive data detection
• Implemented policy enforcement engine blocking 10+ PII pattern types
• Built with Spring Boot, MinIO, Docker, and automated CI/CD
• Achieved 85%+ test coverage using RestAssured and TestNG
• Deployed on Render with Docker containerization

Tech Stack: Java 17, Spring Boot, MinIO, Docker, RestAssured, TestNG
Live Demo: https://casa-security-analyzer.onrender.com
GitHub: https://github.com/bishalthakur0/Cloud-Access-Security-Analyzer-CASA-
```

---

## ⚠️ Important Notes

### Free Tier Limitations

**Render Free Tier:**
- ✅ 750 hours/month (enough for 24/7)
- ✅ 512 MB RAM
- ✅ Shared CPU
- ⚠️ Spins down after 15 min of inactivity
- ⚠️ Cold start takes 30-60 seconds

**Railway Free Tier:**
- ✅ 500 hours/month
- ✅ $5 credit/month
- ✅ Better performance
- ⚠️ Credit-based billing

### Recommendations

1. **Use Render** - Better for always-on demos
2. **Add to README** - Include live demo link
3. **Test Before Interview** - Wake up service before demo
4. **Monitor Usage** - Check Render dashboard

---

## 🔧 Post-Deployment Steps

### 1. Update README.md

Add this to the top of your README:

```markdown
## 🌐 Live Demo

**Try it now:** https://casa-security-analyzer.onrender.com

> Note: Free tier may take 30-60 seconds to wake up on first visit
```

### 2. Add Status Badge

```markdown
![Deployment Status](https://img.shields.io/badge/Deployed-Render-46E3B7)
```

### 3. Test Your Deployment

1. Visit your live URL
2. Upload a clean file → Should be ALLOWED
3. Upload file with email → Should be BLOCKED
4. Check dashboard → Should show statistics

### 4. Update Resume

```
CASA - Cloud Access Security Analyzer
Live Demo: https://casa-security-analyzer.onrender.com
GitHub: https://github.com/bishalthakur0/Cloud-Access-Security-Analyzer-CASA-
```

---

## 🎨 Enhance Your Deployment

### Custom Domain (Optional)

**Render:**
1. Go to Settings → Custom Domain
2. Add your domain (if you have one)
3. Update DNS records

**Railway:**
1. Settings → Networking → Custom Domain
2. Add domain and configure DNS

### Environment Variables

Add these in Render dashboard:
```
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
MINIO_AUTO_CREATE_BUCKET=false
POLICY_REQUIRE_AUTHENTICATION=false
```

---

## 📊 Monitoring

### Render Dashboard
- View logs in real-time
- Monitor CPU/Memory usage
- Check deployment history
- View metrics

### Health Check
Your app includes health endpoint:
```
https://casa-security-analyzer.onrender.com/actuator/health
```

---

## 🚀 Quick Start

1. **Sign up:** https://render.com
2. **New Web Service** → Connect GitHub
3. **Select repo:** `Cloud-Access-Security-Analyzer-CASA-`
4. **Deploy!**
5. **Get URL:** `https://casa-security-analyzer.onrender.com`
6. **Add to resume!**

---

## ✅ Deployment Checklist

- [ ] Sign up for Render/Railway
- [ ] Connect GitHub account
- [ ] Deploy from repository
- [ ] Wait for build to complete
- [ ] Test live URL
- [ ] Update README with live demo link
- [ ] Add deployment badge
- [ ] Update resume with live URL
- [ ] Test before interviews

---

**Your CASA project will be publicly accessible and perfect for your resume!** 🎉
