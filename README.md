# Budget Buddy 💸

A personal finance management application built with a modern Retro-Future aesthetic. Track your income, expenses, and budgets with ease and style.

## 🚀 Features
- **Stateless Authentication**: Secure JWT-based login and registration.
- **Dashboard Hub**: High-level overview of balances, income vs. expense trends, and category breakdowns.
- **Income Tracking**: Log multiple income sources.
- **Expense Management**: Categorize spending and track transactions in real-time.
- [x] **Budgeting**: Set monthly limits and get visual warnings when approaching or exceeding them.
- [x] **Rupee Localization**: Native support for ₹ currency symbols and formatting.
- [x] **High-Contrast UI**: Consistent neon category colors and vibrant design.

## 🛠️ Tech Stack
### Backend
- **Java 17+**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL / MySQL**

### Frontend
- **React 18**
- **Vite**
- **Recharts** (Data Visualization)
- **Lucide-react** (Icons)
- **Vanilla CSS**

## 🏁 Getting Started

### Prerequisites
- JDK 17 or higher
- Node.js & npm (v18+)
- PostgreSQL (for production) or MySQL (local)

### Backend Setup
1. Navigate to `budget-buddy-backend`.
2. Configure your database in `src/main/resources/application.properties`.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup
1. Navigate to `budget-buddy-frontend`.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

## 🚀 Deployment

The project is pre-configured for **Railway** deployment.
- **Backend**: Uses `${PORT}` and `${SPRING_DATASOURCE_URL}`.
- **Frontend**: Uses `VITE_API_URL` for production API connectivity.

See the [Deployment Plan](file:///C:/Users/manan/.gemini/antigravity/brain/a407ae02-65ee-4c39-a203-e590d7d34bff/deployment_plan.md) for more details.

## 📸 Preview
*(Detailed screenshots can be added here)*

## 📄 License
MIT License
