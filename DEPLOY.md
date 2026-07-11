# Deploying Charifit to get a live link

This app needs a running Java/Tomcat server *and* a MySQL database — a
plain GitHub link or GitHub Pages can't do that. Below is the path to an
actual live URL.

## 1. Push to GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

`.gitignore` already excludes `target/`, Tomcat temp folders, and IDE
files, and `DBConnection.java` no longer has a hardcoded password.

## 2. Get a MySQL database somewhere reachable

Pick one (all have free tiers):
- **Railway** → "New Project" → "Provision MySQL" (gives you host/port/user/password instantly)
- **Aiven** → free MySQL plan
- **PlanetScale**

Once created, load your schema into it (run whatever `.sql` file created
`charifit_db` locally, against this new database).

## 3. Deploy the app on Render (or Railway)

**On Render:**
1. New → Web Service → connect your GitHub repo
2. Runtime: **Docker** (Render will detect the `Dockerfile` automatically)
3. Add environment variables under "Environment":
   - `DB_HOST` = your MySQL host
   - `DB_PORT` = usually `3306`
   - `DB_NAME` = `charifit_db`
   - `DB_USER` = your MySQL user
   - `DB_PASSWORD` = your MySQL password
4. Click "Create Web Service"

Render builds the Docker image and gives you a live URL like:
`https://charifit.onrender.com`

**On Railway:** same idea — "New Project" → "Deploy from GitHub repo" →
it detects the Dockerfile → add the same env vars → it gives you a
public URL.

## 4. Test locally with Docker first (recommended)

```bash
docker build -t charifit .
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=charifit_db \
  -e DB_USER=root \
  -e DB_PASSWORD=your_password \
  charifit
```

Then open `http://localhost:8080`.

## Notes
- The server now reads its port from the `PORT` env var (falls back to
  8080), since Render/Railway assign this dynamically.
- Never commit real database credentials. Use `.env.example` as a
  template only — your actual `.env` is gitignored.
