# recepie-sharing-plattform
A platform where you can share recipes and rate them.

## Starta projektet

Förutsättningar: Java, Maven, Node.js/npm och Docker Desktop.

Öppna tre terminaler från projektroten.

### 1. Starta PostgreSQL

```bash
docker compose up -d
```

### 2. Starta backend

```bash
cd backend
mvn spring-boot:run
```

Backend: http://localhost:8080

### 3. Starta frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173

## Användbara kommandon

Kör backendtester:

```bash
cd backend
mvn test
```

Bygg frontend:

```bash
cd frontend
npm run build
```

Visa databasens status:

```bash
docker compose ps
```

Stoppa PostgreSQL utan att radera recept:

```bash
docker compose stop
```

Starta PostgreSQL igen:

```bash
docker compose start
```

Ta bort containern men behåll datan:

```bash
docker compose down
```

Använd inte `docker compose down -v` om recepten ska behållas. Det raderar databasvolymen.
