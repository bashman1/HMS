# HMS Frontend - Angular Application

This directory contains the Angular frontend build artifacts for the HMS (Hospital Management System).

## Build Instructions

1. Navigate to the project root directory
2. Run `npm install` to install dependencies
3. Run `npm run build` to build the Angular application
4. The built files will be placed in `src/main/resources/frontend/`
5. Run the Spring Boot application with `mvn spring-boot:run`

## Development

For development with hot reload:

1. Start Spring Boot backend: `mvn spring-boot:run`
2. In a separate terminal, start Angular dev server: `npm start`
3. Access the app at `http://localhost:4200`
4. API calls are proxied to `http://localhost:8080/api/v1`

## Production Build

```bash
npm install
npm run build:prod
cp -r dist/hms-frontend/browser/* src/main/resources/frontend/
mvn package
java -jar target/hms-auth-service.jar
```

## Technology Stack

- Angular 18 (Standalone Components)
- Tailwind CSS 3.4
- TypeScript 5.4
- RxJS 7.8

## Project Structure

```
src/
├── app/
│   ├── core/           # Core services, guards, interceptors
│   │   ├── guards/     # Route guards (auth.guard.ts, public.guard.ts)
│   │   ├── interceptors/ # HTTP interceptors (auth, error)
│   │   ├── models/     # TypeScript interfaces
│   │   └── services/  # Core services (auth, toast)
│   ├── public/         # Public routes (landing, login, register)
│   │   ├── auth/       # Authentication pages
│   │   └── pages/      # Public pages
│   ├── protected/      # Protected routes (dashboard, layout)
│   │   ├── layout/     # Main layout with sidebar
│   │   └── pages/      # Protected pages (dashboard, profile, settings)
│   └── shared/         # Shared components
├── environments/       # Environment configuration
└── styles.css          # Tailwind CSS styles
```
