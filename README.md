# Aura Stays — Full Stack Hotel Booking Portal

A modern, full-stack boutique hotel reservation system built for technical interview evaluation. The portal features live hotel browsing, date-filtered availability search, server-side dynamic price calculation, automated room assignment, booking confirmation, reservation retrieval, and real-time cancellation with instant capacity release.

It also introduces an algorithmic **"Smart Stay Recommendation"** feature that evaluates hotels on value-for-money metrics without third-party dependencies or AI.

---

## 1. Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.5
- **ORM / Persistence**: Spring Data JPA, Hibernate
- **Database**: MySQL 8.0+ (with out-of-the-box in-memory H2 profile for zero-config evaluation)
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Build Tool**: Apache Maven (via Maven Wrapper `mvnw.cmd` / `mvnw`)
- **Frontend**: React 18, Vite 5
- **Styling**: Vanilla Modern CSS (Warm Beige, Charcoal, and Cream palette — **no blue colors**)
- **Architecture**: Layered MVC (Controller $\to$ Service $\to$ Repository $\to$ Entity $\to$ Database)

---

## 2. Prerequisites

Ensure you have the following installed on your machine:
- **Java Development Kit (JDK)**: Java 17+ (e.g. Eclipse Adoptium Temurin 17)
- **Node.js**: v18+ or v20+ with `npm`
- **MySQL Server** (Optional if running in default H2 development mode): v8.0 or v9.x

---

## 3. Database Setup

### Option A: In-Memory H2 Database (Default — Zero Setup)
The project comes pre-configured with the `h2` profile enabled by default in `application.properties`. You do **not** need to install or configure MySQL to run and evaluate the application immediately.
- Pre-seeded with 6 realistic hotels and physical rooms.
- H2 Web Console accessible at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:hotelbookingdb`, User: `sa`, Password: empty).

### Option B: MySQL Setup (Production / Local Instance)
If you wish to use your local MySQL database:
1. Start your local MySQL service.
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS hotel_booking_db;
   ```
3. Set your MySQL credentials via environment variables:
   ```powershell
   $env:DB_URL="jdbc:mysql://localhost:3306/hotel_booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   $env:DB_USERNAME="your_username"
   $env:DB_PASSWORD="your_password"
   ```
4. Activate the MySQL profile:
   In `backend/src/main/resources/application.properties`, change:
   ```properties
   spring.profiles.active=mysql
   ```

---

## 4. How to Run

### Step 1: Start the Backend (Spring Boot)
Open your terminal (PowerShell or Command Prompt):
```powershell
cd C:\Users\Pratiksha\HotelBookingPortal\backend
.\mvnw.cmd spring-boot:run
```
*The Spring Boot server will start on port `8080`.*
Verify backend health at: **http://localhost:8080/api/health**

### Step 2: Start the Frontend (React + Vite)
Open a **second** terminal window:
```powershell
cd C:\Users\Pratiksha\HotelBookingPortal\frontend
npm run dev
```
*The React Vite client will start on port `5173`.*

Open your browser and navigate to: **http://localhost:5173**

---

## 5. API Overview

All API responses follow a uniform JSON structure:
```json
{
  "success": true,
  "message": "Operation description",
  "data": { ... },
  "timestamp": "2026-11-13T18:30:00"
}
```

### Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/health` | Backend status check |
| `GET` | `/api/hotels` | Browse all hotels or filter with `?location=Miami&checkIn=...&checkOut=...` |
| `GET` | `/api/hotels/{id}` | View detailed hotel information and remaining room count |
| `GET` | `/api/hotels/{id}/availability` | Check room availability count for requested dates |
| `GET` | `/api/hotels/{id}/calculate-price` | Server-calculated price quote ($\text{nights} \times \text{rate}$) |
| `GET` | `/api/hotels/smart-recommendation` | Algorithmic "Smart Stay" best-value recommendation |
| `POST` | `/api/bookings` | Create reservation (Validates availability, assigns room, returns reference) |
| `GET` | `/api/bookings/{reference}` | Retrieve booking by reference (`?email=guest@example.com`) |
| `POST` | `/api/bookings/retrieve` | Look up booking via form payload (`{ bookingReference, email }`) |
| `POST` | `/api/bookings/{reference}/cancel` | Cancel booking and release room availability immediately |

---

## 6. Sample Credentials & Seed Data

No authentication or login credentials are required as per interview guidelines.

The database seeds **6 luxury boutique hotels** on startup:
1. **The Grand Heritage Hotel** (New York, USA) — $220.00 / night (5 rooms)
2. **Azure Coastal Resort & Spa** (Miami, USA) — $185.00 / night (4 rooms)
3. **Le Petit Marais Boutique Hotel** (Paris, France) — $160.00 / night (3 rooms)
4. **Shinjuku Garden Suites** (Tokyo, Japan) — $135.00 / night (6 rooms)
5. **The Royal Mayfair Residence** (London, UK) — $210.00 / night (4 rooms)
6. **Alpine Serenity Lodge** (Zurich, Switzerland) — $195.00 / night (3 rooms)

---

## 7. Approximate Development Time

- **Project Planning & Data Modeling**: ~45 minutes
- **Backend Infrastructure & Spring Boot Setup**: ~1 hour
- **Entities, Repositories & Overlapping Availability Queries**: ~1.5 hours
- **Service Layer, Business Rules & Price Calculations**: ~1.5 hours
- **REST Controllers, Validation & Centralized Error Handler**: ~1 hour
- **Surprise Feature ("Smart Stay Recommendation")**: ~45 minutes
- **Frontend Architecture, Responsive CSS (No Blue Palette) & UI Modals**: ~2.5 hours
- **Integration Testing & Verification**: ~1 hour
- **Documentation (`README.md` & `DESIGN.md`)**: ~1 hour
- **Total Development Time**: **~11 hours**

---

## 8. Unfinished Items & Limitations

- **Simultaneous Concurrency**: High-concurrency edge cases (two requests hitting the exact same millisecond for the final room) are mitigated at the service level, but could be enhanced with pessimistic database row locks (`SELECT ... FOR UPDATE`) or optimistic entity versioning (`@Version`).
- **Payment Processing**: Payment gateway integration was explicitly omitted per assignment requirements; reservations confirm directly.
- **Email Notifications**: Confirmation emails are rendered on-screen with reference codes; an SMTP mailer (e.g. Spring Mail) could be attached in production.

---

## 9. Disclosure of Significant AI Assistance

In compliance with interview academic integrity standards:
- **AI Tool Used**: Antigravity Assistant (Google DeepMind).
- **Assistance Scope**: Architectural scaffolding, initial boilerplate generation for Spring Boot controllers/repositories, CSS design system drafting, and test script synthesis.
- **Student Ownership**: Code structure, business validation rules, formula implementation, error-handling contracts, and full conceptual understanding for interview defense.
