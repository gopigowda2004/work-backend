# Railway Deployment Setup

## Prerequisites
- Railway CLI installed: `npm i -g @railway/cli`
- GitHub repository connected to Railway

## Environment Variables

Set these in Railway dashboard or via CLI:

### Database Variables
- `DATABASE_URL`: Full PostgreSQL connection string (auto-populated by Railway)
- `SPRING_DATASOURCE_USERNAME`: Database user (auto-populated)
- `SPRING_DATASOURCE_PASSWORD`: Database password (auto-populated)

### Application Variables
- `PORT`: 8080
- `SPRING_PROFILES_ACTIVE`: prod
- `JWT_SECRET`: Your JWT secret key
- `CORS_ORIGINS`: https://workloghub.vercel.app

## Setup Steps

1. **Create Railway Project**
   ```bash
   railway init
   ```

2. **Connect GitHub Repository**
   - Link your GitHub account
   - Select `work-backend` repository

3. **Add PostgreSQL Plugin**
   - In Railway dashboard, click "Add Service"
   - Select "PostgreSQL"
   - Railway will auto-populate `DATABASE_URL`

4. **Set Environment Variables**
   - Go to Variables tab
   - Add all variables listed above
   - Railway auto-injects `DATABASE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

5. **Deploy**
   - Push to GitHub main branch
   - Railway auto-deploys via webhook
   - Check dashboard for logs

## How It Works

- **Docker Build**: Railway uses the Dockerfile to build your image
- **railway.json**: Specifies Docker builder and start command
- **entrypoint.sh**: Handles JDBC URL prefix for database connection
- **Database URL**: Railway passes full connection string; script prepends `jdbc:` if needed

## Troubleshooting

Check logs in Railway dashboard:
- Build errors: Check Maven compilation
- Runtime errors: Check Spring Boot initialization
- Database errors: Verify DATABASE_URL is set and accessible
