# Hospital Management System (HMS) - Development Plan

## Technology Stack

### Backend (Spring Boot 3.x)
- **Framework:** Spring Boot 3.3.x (Latest LTS)
- **Java Version:** Java 21 (LTS)
- **Build Tool:** Maven / Gradle
- **Database:** PostgreSQL 16 (Primary), Redis (Caching)
- **Security:** Spring Security 6 + JWT + OAuth2
- **API Documentation:** SpringDoc OpenAPI 3 (Swagger)
- **ORM:** Spring Data JPA + Hibernate 6
- **Messaging:** Apache Kafka / RabbitMQ
- **File Storage:** MinIO (S3-compatible) for DICOM/documents
- **Search:** Elasticsearch (for patient/record search)
- **Reporting:** JasperReports / Apache POI

### Frontend (Angular 18+)
- **Framework:** Angular 18.x (Latest)
- **UI Library:** Angular Material / PrimeNG
- **State Management:** NgRx / Signals
- **Charts:** Chart.js / ngx-charts
- **Forms:** Reactive Forms
- **HTTP:** HttpClient with Interceptors
- **Authentication:** JWT with HTTP-only cookies
- **PWA:** Service Workers for offline capability

### DevOps & Infrastructure
- **Containerization:** Docker + Docker Compose
- **Orchestration:** Kubernetes (Production)
- **CI/CD:** GitHub Actions / GitLab CI
- **Monitoring:** Prometheus + Grafana
- **Logging:** ELK Stack (Elasticsearch, Logstash, Kibana)
- **API Gateway:** Spring Cloud Gateway

---

## Project Structure

### Backend Microservices Architecture

```
hms-backend/
├── hms-gateway/                    # API Gateway (Spring Cloud Gateway)
├── hms-discovery/                  # Service Discovery (Eureka)
├── hms-config/                     # Centralized Configuration
├── hms-auth-service/               # Authentication & Authorization
├── hms-patient-service/            # Patient Registration & Management
├── hms-appointment-service/        # Appointments & Queue Management
├── hms-consultation-service/       # OPD/IPD Consultations & EMR
├── hms-ipd-service/                # Inpatient Management
├── hms-nursing-service/            # Nursing Charts & Care Plans
├── hms-lab-service/                # Laboratory Information System
├── hms-radiology-service/          # Radiology & PACS
├── hms-pharmacy-service/           # Pharmacy & Drug Management
├── hms-billing-service/            # Billing & Payments
├── hms-insurance-service/          # Insurance & TPA
├── hms-ot-service/                 # Operation Theatre
├── hms-icu-service/                # ICU & Critical Care
├── hms-bloodbank-service/          # Blood Bank Management
├── hms-inventory-service/          # Inventory & Store
├── hms-dietary-service/            # Dietary & Food Services
├── hms-notification-service/       # SMS/Email/WhatsApp Notifications
├── hms-report-service/             # Reports & Analytics
├── hms-common/                     # Shared DTOs, Utils, Exceptions
└── docker-compose.yml
```

### Frontend Modular Architecture

```
hms-frontend/
├── src/
│   ├── app/
│   │   ├── core/                   # Core services, guards, interceptors
│   │   │   ├── auth/
│   │   │   ├── guards/
│   │   │   ├── interceptors/
│   │   │   └── services/
│   │   ├── shared/                 # Shared components, pipes, directives
│   │   │   ├── components/
│   │   │   ├── directives/
│   │   │   ├── pipes/
│   │   │   └── models/
│   │   ├── features/               # Feature modules (lazy-loaded)
│   │   │   ├── dashboard/
│   │   │   ├── patient/
│   │   │   ├── appointment/
│   │   │   ├── consultation/
│   │   │   ├── ipd/
│   │   │   ├── nursing/
│   │   │   ├── laboratory/
│   │   │   ├── radiology/
│   │   │   ├── pharmacy/
│   │   │   ├── billing/
│   │   │   ├── insurance/
│   │   │   ├── ot/
│   │   │   ├── icu/
│   │   │   ├── bloodbank/
│   │   │   ├── inventory/
│   │   │   ├── dietary/
│   │   │   ├── reports/
│   │   │   └── settings/
│   │   ├── layouts/                # Layout components
│   │   └── app.routes.ts
│   ├── assets/
│   ├── environments/
│   └── styles/
├── angular.json
└── package.json
```

---

## Development Phases - Detailed Breakdown

### Phase 1: Foundation & Core Infrastructure (Weeks 1-4)

#### Week 1-2: Project Setup & Authentication

**Backend Tasks:**
1. Initialize Spring Boot 3.3 multi-module project
2. Setup PostgreSQL database with Flyway migrations
3. Configure Spring Security 6 with JWT authentication
4. Implement user management (roles: Admin, Doctor, Nurse, Receptionist, Pharmacist, Lab Tech, Billing Staff)
5. Setup API Gateway with rate limiting
6. Configure Swagger/OpenAPI documentation

**Frontend Tasks:**
1. Initialize Angular 18 project with standalone components
2. Setup Angular Material theme
3. Implement authentication module (login, logout, password reset)
4. Create base layout (sidebar, header, footer)
5. Setup HTTP interceptors for JWT handling
6. Configure route guards

**Database Schema - Auth:**
```sql
-- Users & Roles
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    employee_id VARCHAR(20),
    department_id UUID,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id),
    role_id UUID REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    module VARCHAR(50)
);

CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(id),
    permission_id UUID REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);
```

#### Week 3-4: Patient Registration Module

**Backend - Patient Service:**
```java
// Key Entities
- Patient (demographics, contact, emergency contact)
- PatientIdentifier (UHID, Aadhaar, insurance IDs)
- PatientInsurance (insurance details)
- PatientVisit (OPD visits tracking)

// Key APIs
POST   /api/patients                    - Register new patient
GET    /api/patients/{id}               - Get patient details
GET    /api/patients/search             - Search patients (name, phone, UHID)
PUT    /api/patients/{id}               - Update patient
GET    /api/patients/{id}/visits        - Get visit history
POST   /api/patients/{id}/visits        - Create new visit
GET    /api/patients/uhid/generate      - Generate UHID
```

**Frontend - Patient Module:**
- Patient registration form (multi-step wizard)
- Patient search with filters
- Patient profile view
- Visit history timeline
- Quick registration for emergencies

**Database Schema - Patient:**
```sql
CREATE TABLE patients (
    id UUID PRIMARY KEY,
    uhid VARCHAR(20) UNIQUE NOT NULL,
    mr_number VARCHAR(20),
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    blood_group VARCHAR(5),
    marital_status VARCHAR(20),
    nationality VARCHAR(50),
    religion VARCHAR(50),
    occupation VARCHAR(100),
    
    -- Contact
    phone_primary VARCHAR(20),
    phone_secondary VARCHAR(20),
    email VARCHAR(100),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    
    -- Emergency Contact
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    
    -- Identification
    aadhaar_number VARCHAR(12),
    abha_id VARCHAR(20),
    passport_number VARCHAR(20),
    
    -- Medical
    allergies TEXT,
    chronic_conditions TEXT,
    
    -- Metadata
    photo_url VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE patient_visits (
    id UUID PRIMARY KEY,
    patient_id UUID REFERENCES patients(id),
    visit_number VARCHAR(20) UNIQUE,
    visit_type VARCHAR(20), -- OPD, IPD, EMERGENCY
    visit_date TIMESTAMP,
    department_id UUID,
    doctor_id UUID,
    status VARCHAR(20), -- REGISTERED, IN_CONSULTATION, COMPLETED, CANCELLED
    chief_complaint TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### Phase 2: Appointment & Consultation (Weeks 5-8)

#### Week 5-6: Appointment Management

**Backend - Appointment Service:**
```java
// Key Entities
- Doctor (profile, specialization, departments)
- DoctorSchedule (weekly schedule, slots)
- Appointment (booking details)
- AppointmentSlot (available slots)
- Queue (token management)

// Key APIs
GET    /api/doctors                           - List doctors
GET    /api/doctors/{id}/schedule             - Get doctor schedule
GET    /api/doctors/{id}/slots?date=          - Get available slots
POST   /api/appointments                      - Book appointment
GET    /api/appointments/{id}                 - Get appointment
PUT    /api/appointments/{id}/cancel          - Cancel appointment
PUT    /api/appointments/{id}/reschedule      - Reschedule
GET    /api/queue/today?doctor_id=            - Get today's queue
POST   /api/queue/call-next                   - Call next patient
```

**Frontend - Appointment Module:**
- Doctor listing with filters (department, specialization)
- Calendar view for slot selection
- Appointment booking wizard
- My appointments list (for patients)
- Queue management dashboard
- Token display screen (for waiting area)

**Database Schema - Appointments:**
```sql
CREATE TABLE doctors (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    employee_id VARCHAR(20),
    registration_number VARCHAR(50),
    specialization VARCHAR(100),
    qualification VARCHAR(255),
    experience_years INTEGER,
    consultation_fee DECIMAL(10,2),
    bio TEXT,
    photo_url VARCHAR(255),
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE doctor_departments (
    doctor_id UUID REFERENCES doctors(id),
    department_id UUID REFERENCES departments(id),
    PRIMARY KEY (doctor_id, department_id)
);

CREATE TABLE doctor_schedules (
    id UUID PRIMARY KEY,
    doctor_id UUID REFERENCES doctors(id),
    day_of_week INTEGER, -- 0=Sunday, 6=Saturday
    start_time TIME,
    end_time TIME,
    slot_duration_minutes INTEGER DEFAULT 15,
    max_patients INTEGER,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    appointment_number VARCHAR(20) UNIQUE,
    patient_id UUID REFERENCES patients(id),
    doctor_id UUID REFERENCES doctors(id),
    department_id UUID REFERENCES departments(id),
    appointment_date DATE,
    appointment_time TIME,
    slot_duration INTEGER,
    appointment_type VARCHAR(20), -- WALK_IN, SCHEDULED, FOLLOW_UP, TELECONSULT
    status VARCHAR(20), -- SCHEDULED, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    chief_complaint TEXT,
    notes TEXT,
    token_number INTEGER,
    check_in_time TIMESTAMP,
    consultation_start_time TIMESTAMP,
    consultation_end_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE queue_entries (
    id UUID PRIMARY KEY,
    appointment_id UUID REFERENCES appointments(id),
    doctor_id UUID REFERENCES doctors(id),
    queue_date DATE,
    token_number INTEGER,
    status VARCHAR(20), -- WAITING, CALLED, IN_CONSULTATION, COMPLETED, SKIPPED
    called_at TIMESTAMP,
    priority INTEGER DEFAULT 0, -- Higher = more priority
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Week 7-8: Doctor Consultation & EMR

**Backend - Consultation Service:**
```java
// Key Entities
- Consultation (visit details)
- VitalSigns (BP, temp, pulse, etc.)
- Diagnosis (ICD-10 codes)
- Prescription (medications)
- ClinicalNote (SOAP notes)
- LabOrder (lab test orders)
- RadiologyOrder (imaging orders)

// Key APIs
POST   /api/consultations                     - Start consultation
GET    /api/consultations/{id}                - Get consultation
PUT    /api/consultations/{id}                - Update consultation
POST   /api/consultations/{id}/vitals         - Record vitals
POST   /api/consultations/{id}/diagnosis      - Add diagnosis
POST   /api/consultations/{id}/prescription   - Add prescription
POST   /api/consultations/{id}/lab-orders     - Order lab tests
POST   /api/consultations/{id}/complete       - Complete consultation
GET    /api/patients/{id}/emr                 - Get patient EMR
GET    /api/icd10/search?q=                   - Search ICD-10 codes
GET    /api/drugs/search?q=                   - Search drugs
```

**Frontend - Consultation Module:**
- Doctor dashboard (today's patients, pending tasks)
- Consultation screen with tabs:
  - Patient summary & history
  - Vitals entry
  - Chief complaint & history
  - Examination findings
  - Diagnosis (ICD-10 autocomplete)
  - Prescription (drug autocomplete, dosage templates)
  - Lab orders
  - Radiology orders
  - Clinical notes (SOAP format)
  - Advice & follow-up
- Prescription print preview
- Past visit comparison view

**Database Schema - Consultation:**
```sql
CREATE TABLE consultations (
    id UUID PRIMARY KEY,
    visit_id UUID REFERENCES patient_visits(id),
    patient_id UUID REFERENCES patients(id),
    doctor_id UUID REFERENCES doctors(id),
    consultation_date TIMESTAMP,
    chief_complaint TEXT,
    history_of_present_illness TEXT,
    past_medical_history TEXT,
    family_history TEXT,
    social_history TEXT,
    examination_findings TEXT,
    clinical_notes TEXT,
    advice TEXT,
    follow_up_date DATE,
    follow_up_notes TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vital_signs (
    id UUID PRIMARY KEY,
    consultation_id UUID REFERENCES consultations(id),
    patient_id UUID REFERENCES patients(id),
    recorded_at TIMESTAMP,
    temperature DECIMAL(4,1),
    temperature_unit VARCHAR(1), -- C or F
    pulse_rate INTEGER,
    respiratory_rate INTEGER,
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    spo2 INTEGER,
    weight DECIMAL(5,2),
    height DECIMAL(5,2),
    bmi DECIMAL(4,2),
    blood_sugar_fasting DECIMAL(5,2),
    blood_sugar_random DECIMAL(5,2),
    pain_scale INTEGER,
    notes TEXT,
    recorded_by UUID REFERENCES users(id)
);

CREATE TABLE diagnoses (
    id UUID PRIMARY KEY,
    consultation_id UUID REFERENCES consultations(id),
    icd_code VARCHAR(10),
    icd_description VARCHAR(255),
    diagnosis_type VARCHAR(20), -- PRIMARY, SECONDARY, DIFFERENTIAL
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prescriptions (
    id UUID PRIMARY KEY,
    consultation_id UUID REFERENCES consultations(id),
    patient_id UUID REFERENCES patients(id),
    doctor_id UUID REFERENCES doctors(id),
    prescription_date DATE,
    status VARCHAR(20), -- ACTIVE, DISPENSED, CANCELLED
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prescription_items (
    id UUID PRIMARY KEY,
    prescription_id UUID REFERENCES prescriptions(id),
    drug_id UUID REFERENCES drugs(id),
    drug_name VARCHAR(255),
    dosage VARCHAR(100),
    frequency VARCHAR(100), -- e.g., "1-0-1", "TID", "QID"
    duration INTEGER,
    duration_unit VARCHAR(20), -- DAYS, WEEKS, MONTHS
    quantity INTEGER,
    instructions TEXT,
    route VARCHAR(50), -- ORAL, IV, IM, TOPICAL
    is_substitutable BOOLEAN DEFAULT true
);
```

---

### Phase 3: Billing & Pharmacy (Weeks 9-12)

#### Week 9-10: Billing Module

**Backend - Billing Service:**
```java
// Key Entities
- Bill (invoice)
- BillItem (line items)
- Payment (payment records)
- ServiceMaster (service catalog)
- PackageMaster (treatment packages)
- Discount (discount rules)

// Key APIs
POST   /api/bills                             - Create bill
GET    /api/bills/{id}                        - Get bill
PUT    /api/bills/{id}/items                  - Add/update items
POST   /api/bills/{id}/payments               - Record payment
GET    /api/bills/{id}/receipt                - Generate receipt
POST   /api/bills/{id}/refund                 - Process refund
GET    /api/services                          - List services
GET    /api/packages                          - List packages
```

**Frontend - Billing Module:**
- Bill creation screen
- Service/package selection
- Discount application
- Payment collection (cash, card, UPI, insurance)
- Receipt printing
- Bill history & search
- Refund processing
- Daily collection report

**Database Schema - Billing:**
```sql
CREATE TABLE service_masters (
    id UUID PRIMARY KEY,
    service_code VARCHAR(20) UNIQUE,
    service_name VARCHAR(255),
    department_id UUID,
    category VARCHAR(100),
    base_price DECIMAL(10,2),
    tax_percentage DECIMAL(5,2),
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE bills (
    id UUID PRIMARY KEY,
    bill_number VARCHAR(20) UNIQUE,
    patient_id UUID REFERENCES patients(id),
    visit_id UUID REFERENCES patient_visits(id),
    bill_type VARCHAR(20), -- OPD, IPD, PHARMACY, LAB, RADIOLOGY
    bill_date TIMESTAMP,
    subtotal DECIMAL(12,2),
    discount_amount DECIMAL(12,2),
    tax_amount DECIMAL(12,2),
    total_amount DECIMAL(12,2),
    paid_amount DECIMAL(12,2),
    balance_amount DECIMAL(12,2),
    status VARCHAR(20), -- DRAFT, PENDING, PARTIAL, PAID, CANCELLED
    payment_mode VARCHAR(20),
    insurance_claim_id UUID,
    notes TEXT,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bill_items (
    id UUID PRIMARY KEY,
    bill_id UUID REFERENCES bills(id),
    service_id UUID REFERENCES service_masters(id),
    description VARCHAR(255),
    quantity INTEGER,
    unit_price DECIMAL(10,2),
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    tax_percentage DECIMAL(5,2),
    tax_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    bill_id UUID REFERENCES bills(id),
    payment_number VARCHAR(20) UNIQUE,
    payment_date TIMESTAMP,
    amount DECIMAL(12,2),
    payment_mode VARCHAR(20), -- CASH, CARD, UPI, CHEQUE, INSURANCE, ONLINE
    reference_number VARCHAR(100),
    status VARCHAR(20), -- SUCCESS, FAILED, REFUNDED
    notes TEXT,
    received_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Week 11-12: Pharmacy Module

**Backend - Pharmacy Service:**
```java
// Key Entities
- Drug (drug master)
- DrugStock (inventory)
- DrugBatch (batch tracking)
- PharmacyOrder (dispensing)
- PurchaseOrder (procurement)
- Supplier (vendor master)

// Key APIs
GET    /api/drugs                             - List drugs
GET    /api/drugs/{id}/stock                  - Get stock info
POST   /api/pharmacy/dispense                 - Dispense drugs
GET    /api/pharmacy/orders                   - List orders
POST   /api/pharmacy/return                   - Return drugs
GET    /api/pharmacy/expiry-alerts            - Get expiry alerts
POST   /api/pharmacy/purchase-orders          - Create PO
GET    /api/pharmacy/stock-report             - Stock report
```

**Frontend - Pharmacy Module:**
- Drug master management
- Stock management
- Prescription queue (from doctors)
- Dispensing screen
- Drug return handling
- Expiry alerts dashboard
- Purchase order creation
- Stock reports

**Database Schema - Pharmacy:**
```sql
CREATE TABLE drugs (
    id UUID PRIMARY KEY,
    drug_code VARCHAR(20) UNIQUE,
    drug_name VARCHAR(255),
    generic_name VARCHAR(255),
    brand_name VARCHAR(255),
    manufacturer VARCHAR(255),
    category VARCHAR(100), -- TABLET, CAPSULE, SYRUP, INJECTION, etc.
    composition TEXT,
    strength VARCHAR(100),
    unit VARCHAR(50),
    hsn_code VARCHAR(20),
    schedule VARCHAR(10), -- H, H1, X, etc.
    requires_prescription BOOLEAN DEFAULT true,
    mrp DECIMAL(10,2),
    purchase_price DECIMAL(10,2),
    selling_price DECIMAL(10,2),
    tax_percentage DECIMAL(5,2),
    reorder_level INTEGER,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE drug_batches (
    id UUID PRIMARY KEY,
    drug_id UUID REFERENCES drugs(id),
    batch_number VARCHAR(50),
    manufacturing_date DATE,
    expiry_date DATE,
    quantity_received INTEGER,
    quantity_available INTEGER,
    purchase_price DECIMAL(10,2),
    mrp DECIMAL(10,2),
    supplier_id UUID,
    grn_number VARCHAR(50),
    received_date DATE,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE pharmacy_dispensing (
    id UUID PRIMARY KEY,
    dispensing_number VARCHAR(20) UNIQUE,
    prescription_id UUID REFERENCES prescriptions(id),
    patient_id UUID REFERENCES patients(id),
    dispensed_by UUID REFERENCES users(id),
    dispensing_date TIMESTAMP,
    status VARCHAR(20), -- PENDING, DISPENSED, PARTIAL, RETURNED
    bill_id UUID REFERENCES bills(id),
    notes TEXT
);

CREATE TABLE dispensing_items (
    id UUID PRIMARY KEY,
    dispensing_id UUID REFERENCES pharmacy_dispensing(id),
    prescription_item_id UUID,
    drug_id UUID REFERENCES drugs(id),
    batch_id UUID REFERENCES drug_batches(id),
    quantity_prescribed INTEGER,
    quantity_dispensed INTEGER,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2)
);
```

---

### Phase 4: Laboratory & Radiology (Weeks 13-16)

#### Week 13-14: Laboratory Information System (LIS)

**Backend - Lab Service:**
```java
// Key Entities
- LabTest (test master)
- LabTestParameter (test parameters)
- LabOrder (test orders)
- LabSample (sample collection)
- LabResult (test results)

// Key APIs
GET    /api/lab/tests                         - List tests
POST   /api/lab/orders                        - Create order
GET    /api/lab/orders/{id}                   - Get order
POST   /api/lab/samples/collect               - Collect sample
PUT    /api/lab/samples/{id}/receive          - Receive in lab
POST   /api/lab/results                       - Enter results
PUT    /api/lab/results/{id}/verify           - Verify results
GET    /api/lab/results/{id}/report           - Generate report
GET    /api/lab/worklist                      - Lab worklist
```

**Frontend - Lab Module:**
- Test master management
- Order entry screen
- Sample collection with barcode
- Lab worklist
- Result entry (manual + machine interface)
- Result verification workflow
- Report generation & printing
- Critical value alerts
- TAT monitoring dashboard

**Database Schema - Laboratory:**
```sql
CREATE TABLE lab_tests (
    id UUID PRIMARY KEY,
    test_code VARCHAR(20) UNIQUE,
    test_name VARCHAR(255),
    department VARCHAR(100), -- BIOCHEMISTRY, HEMATOLOGY, MICROBIOLOGY, etc.
    sample_type VARCHAR(100), -- BLOOD, URINE, STOOL, etc.
    container_type VARCHAR(100),
    method VARCHAR(255),
    tat_hours INTEGER, -- Turn around time
    price DECIMAL(10,2),
    is_panel BOOLEAN DEFAULT false,
    instructions TEXT,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE lab_test_parameters (
    id UUID PRIMARY KEY,
    test_id UUID REFERENCES lab_tests(id),
    parameter_name VARCHAR(255),
    parameter_code VARCHAR(50),
    unit VARCHAR(50),
    reference_range_male VARCHAR(100),
    reference_range_female VARCHAR(100),
    reference_range_child VARCHAR(100),
    critical_low DECIMAL(10,2),
    critical_high DECIMAL(10,2),
    display_order INTEGER,
    is_calculated BOOLEAN DEFAULT false,
    formula TEXT
);

CREATE TABLE lab_orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(20) UNIQUE,
    patient_id UUID REFERENCES patients(id),
    visit_id UUID REFERENCES patient_visits(id),
    consultation_id UUID REFERENCES consultations(id),
    ordered_by UUID REFERENCES doctors(id),
    order_date TIMESTAMP,
    priority VARCHAR(20), -- ROUTINE, URGENT, STAT
    clinical_notes TEXT,
    status VARCHAR(20), -- ORDERED, SAMPLE_COLLECTED, IN_PROGRESS, COMPLETED, CANCELLED
    bill_id UUID REFERENCES bills(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lab_samples (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES lab_orders(id),
    sample_number VARCHAR(20) UNIQUE,
    barcode VARCHAR(50) UNIQUE,
    sample_type VARCHAR(100),
    collection_date TIMESTAMP,
    collected_by UUID REFERENCES users(id),
    received_date TIMESTAMP,
    received_by UUID REFERENCES users(id),
    status VARCHAR(20), -- PENDING, COLLECTED, RECEIVED, PROCESSING, COMPLETED, REJECTED
    rejection_reason TEXT
);

CREATE TABLE lab_results (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES lab_orders(id),
    sample_id UUID REFERENCES lab_samples(id),
    test_id UUID REFERENCES lab_tests(id),
    parameter_id UUID REFERENCES lab_test_parameters(id),
    result_value VARCHAR(255),
    result_numeric DECIMAL(15,5),
    unit VARCHAR(50),
    reference_range VARCHAR(100),
    is_abnormal BOOLEAN DEFAULT false,
    is_critical BOOLEAN DEFAULT false,
    entered_by UUID REFERENCES users(id),
    entered_at TIMESTAMP,
    verified_by UUID REFERENCES users(id),
    verified_at TIMESTAMP,
    status VARCHAR(20) -- PENDING, ENTERED, VERIFIED, AMENDED
);
```

#### Week 15-16: Radiology & PACS

**Backend - Radiology Service:**
```java
// Key Entities
- RadiologyExam (exam master)
- RadiologyOrder (imaging orders)
- RadiologyStudy (DICOM studies)
- RadiologyReport (radiologist reports)

// Key APIs
GET    /api/radiology/exams                   - List exams
POST   /api/radiology/orders                  - Create order
GET    /api/radiology/orders/{id}             - Get order
PUT    /api/radiology/orders/{id}/schedule    - Schedule exam
POST   /api/radiology/studies                 - Upload study
GET    /api/radiology/studies/{id}/images     - Get DICOM images
POST   /api/radiology/reports                 - Create report
PUT    /api/radiology/reports/{id}/approve    - Approve report
GET    /api/radiology/worklist                - Radiology worklist
```

**Frontend - Radiology Module:**
- Exam master management
- Order entry screen
- Scheduling calendar
- Worklist for technicians
- DICOM viewer integration (Cornerstone.js / OHIF)
- Report entry with templates
- Report approval workflow
- Image sharing (CD burning, WhatsApp)

---

### Phase 5: IPD & Nursing (Weeks 17-20)

#### Week 17-18: Inpatient Management

**Backend - IPD Service:**
```java
// Key Entities
- Ward (ward master)
- Bed (bed master)
- Admission (IPD admission)
- BedOccupancy (bed allocation)
- Transfer (bed transfers)
- Discharge (discharge details)

// Key APIs
GET    /api/wards                             - List wards
GET    /api/beds?ward_id=&status=             - List beds
POST   /api/admissions                        - Admit patient
GET    /api/admissions/{id}                   - Get admission
POST   /api/admissions/{id}/transfer          - Transfer bed
POST   /api/admissions/{id}/discharge         - Discharge patient
GET    /api/ipd/census                        - Bed census
GET    /api/ipd/dashboard                     - IPD dashboard
```

**Frontend - IPD Module:**
- Ward/bed master management
- Bed availability board (visual)
- Admission form
- Patient list by ward
- Bed transfer
- Discharge process
- IPD dashboard (occupancy, admissions, discharges)

**Database Schema - IPD:**
```sql
CREATE TABLE wards (
    id UUID PRIMARY KEY,
    ward_code VARCHAR(20) UNIQUE,
    ward_name VARCHAR(100),
    ward_type VARCHAR(50), -- GENERAL, SEMI_PRIVATE, PRIVATE, ICU, NICU, etc.
    floor VARCHAR(20),
    building VARCHAR(50),
    total_beds INTEGER,
    department_id UUID,
    charge_per_day DECIMAL(10,2),
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE beds (
    id UUID PRIMARY KEY,
    bed_number VARCHAR(20),
    ward_id UUID REFERENCES wards(id),
    bed_type VARCHAR(50), -- REGULAR, ELECTRIC, ICU, VENTILATOR
    status VARCHAR(20), -- AVAILABLE, OCCUPIED, MAINTENANCE, BLOCKED
    features TEXT, -- AC, TV, ATTACHED_BATHROOM, etc.
    charge_per_day DECIMAL(10,2),
    is_active BOOLEAN DEFAULT true,
    UNIQUE(ward_id, bed_number)
);

CREATE TABLE admissions (
    id UUID PRIMARY KEY,
    admission_number VARCHAR(20) UNIQUE,
    patient_id UUID REFERENCES patients(id),
    visit_id UUID REFERENCES patient_visits(id),
    admission_date TIMESTAMP,
    admission_type VARCHAR(20), -- ELECTIVE, EMERGENCY, TRANSFER
    admitting_doctor_id UUID REFERENCES doctors(id),
    attending_doctor_id UUID REFERENCES doctors(id),
    department_id UUID,
    ward_id UUID REFERENCES wards(id),
    bed_id UUID REFERENCES beds(id),
    diagnosis_at_admission TEXT,
    expected_los INTEGER, -- Expected length of stay
    status VARCHAR(20), -- ADMITTED, DISCHARGED, TRANSFERRED, ABSCONDED, EXPIRED
    discharge_date TIMESTAMP,
    discharge_type VARCHAR(20), -- NORMAL, LAMA, DAMA, ABSCONDED, EXPIRED, TRANSFER
    discharge_summary_id UUID,
    final_bill_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bed_transfers (
    id UUID PRIMARY KEY,
    admission_id UUID REFERENCES admissions(id),
    from_ward_id UUID REFERENCES wards(id),
    from_bed_id UUID REFERENCES beds(id),
    to_ward_id UUID REFERENCES wards(id),
    to_bed_id UUID REFERENCES beds(id),
    transfer_date TIMESTAMP,
    reason TEXT,
    transferred_by UUID REFERENCES users(id)
);
```

#### Week 19-20: Nursing Module

**Backend - Nursing Service:**
```java
// Key Entities
- NursingAssessment (initial assessment)
- VitalChart (TPR chart)
- IntakeOutput (I/O chart)
- MedicationAdministration (e-MAR)
- NursingNote (nursing documentation)
- CarePlan (care planning)

// Key APIs
POST   /api/nursing/assessments               - Create assessment
GET    /api/nursing/patients/{id}/vitals      - Get vital chart
POST   /api/nursing/vitals                    - Record vitals
GET    /api/nursing/patients/{id}/io          - Get I/O chart
POST   /api/nursing/io                        - Record I/O
GET    /api/nursing/patients/{id}/mar         - Get MAR
POST   /api/nursing/mar/administer            - Administer medication
POST   /api/nursing/notes                     - Add nursing note
GET    /api/nursing/handover                  - Shift handover
```

**Frontend - Nursing Module:**
- Nursing dashboard (assigned patients)
- Patient assessment form
- Vital signs chart (graphical)
- Intake-output chart
- e-MAR (medication administration)
- Nursing notes
- Care plan management
- Shift handover report

---

### Phase 6: Advanced Modules (Weeks 21-28)

#### Week 21-22: Operation Theatre

**Backend - OT Service:**
- OT master, OT scheduling
- Surgery booking, team allocation
- Anesthesia chart
- OT consumables, implant tracking
- Post-op notes

#### Week 23-24: ICU Module

**Backend - ICU Service:**
- ICU bed management
- Ventilator monitoring
- Specialized charts (Glasgow, sedation)
- ABG entry
- ICU scoring systems

#### Week 25-26: Insurance & TPA

**Backend - Insurance Service:**
- Insurance master
- Pre-authorization workflow
- Claim generation
- Deduction management
- Cashless processing

#### Week 27-28: Blood Bank

**Backend - Blood Bank Service:**
- Donor registration
- Blood collection
- Component separation
- Cross-match
- Issue & transfusion
- Stock management

---

### Phase 7: Support Modules (Weeks 29-34)

#### Week 29-30: Inventory & Store

- Multi-store management
- Indent & issue
- Purchase orders
- GRN (Goods Receipt Note)
- Stock reports
- Expiry management

#### Week 31-32: Dietary Services

- Diet master
- Diet orders
- Meal planning
- Kitchen management
- Tray tracking

#### Week 33-34: CSSD

- Instrument set master
- Sterilization tracking
- Issue/return to OT
- Cycle records

---

### Phase 8: Reports & Analytics (Weeks 35-38)

#### Week 35-36: Reports Module

**Key Reports:**
- Patient statistics
- Revenue reports
- Department-wise collection
- Doctor performance
- Bed occupancy
- Lab TAT
- Pharmacy sales
- Outstanding dues

#### Week 37-38: Analytics Dashboard

- Executive dashboard
- Real-time KPIs
- Trend analysis
- Predictive analytics
- Custom report builder

---

### Phase 9: Mobile & Integration (Weeks 39-44)

#### Week 39-40: Patient Portal (Angular PWA)

- Appointment booking
- Lab/radiology reports
- Bill payments
- Prescription view
- Teleconsultation

#### Week 41-42: Mobile App (Ionic/Capacitor)

- Doctor app
- Nurse app
- Patient app

#### Week 43-44: External Integrations

- ABHA/ABDM integration
- Payment gateway (Razorpay/PayU)
- SMS gateway
- WhatsApp Business API
- Email service

---

### Phase 10: Testing & Deployment (Weeks 45-48)

#### Week 45-46: Testing

- Unit tests (JUnit 5, Jasmine)
- Integration tests
- E2E tests (Cypress)
- Performance testing (JMeter)
- Security testing

#### Week 47-48: Deployment

- Docker containerization
- Kubernetes deployment
- CI/CD pipeline setup
- Production deployment
- Documentation

---

## API Design Standards

### RESTful Conventions
```
GET    /api/v1/{resource}           - List resources
GET    /api/v1/{resource}/{id}      - Get single resource
POST   /api/v1/{resource}           - Create resource
PUT    /api/v1/{resource}/{id}      - Update resource
DELETE /api/v1/{resource}/{id}      - Delete resource
PATCH  /api/v1/{resource}/{id}      - Partial update
```

### Response Format
```json
{
  "success": true,
  "data": { },
  "message": "Operation successful",
  "timestamp": "2025-01-15T10:30:00Z",
  "pagination": {
    "page": 1,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "PATIENT_NOT_FOUND",
    "message": "Patient with ID xyz not found",
    "details": []
  },
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Security Implementation

### Authentication Flow
1. User login with username/password
2. Server validates and returns JWT access token + refresh token
3. Access token stored in memory, refresh token in HTTP-only cookie
4. Access token sent in Authorization header
5. Token refresh on expiry using refresh token

### Authorization
- Role-based access control (RBAC)
- Permission-based fine-grained control
- Method-level security with @PreAuthorize
- Row-level security for multi-tenant data

### Security Headers
```java
@Configuration
public class SecurityConfig {
    // CORS, CSRF, XSS protection
    // Content Security Policy
    // Rate limiting
    // Audit logging
}
```

---

## Estimated Timeline Summary

| Phase | Description | Duration | Cumulative |
|-------|-------------|----------|------------|
| 1 | Foundation & Auth | 4 weeks | Week 4 |
| 2 | Appointment & Consultation | 4 weeks | Week 8 |
| 3 | Billing & Pharmacy | 4 weeks | Week 12 |
| 4 | Laboratory & Radiology | 4 weeks | Week 16 |
| 5 | IPD & Nursing | 4 weeks | Week 20 |
| 6 | Advanced Modules | 8 weeks | Week 28 |
| 7 | Support Modules | 6 weeks | Week 34 |
| 8 | Reports & Analytics | 4 weeks | Week 38 |
| 9 | Mobile & Integration | 6 weeks | Week 44 |
| 10 | Testing & Deployment | 4 weeks | Week 48 |

**Total Duration: ~48 weeks (12 months)**

---

## Team Structure Recommendation

| Role | Count | Responsibility |
|------|-------|----------------|
| Project Manager | 1 | Overall coordination |
| Tech Lead | 1 | Architecture & code review |
| Backend Developers | 3-4 | Spring Boot services |
| Frontend Developers | 2-3 | Angular application |
| Database Admin | 1 | Database design & optimization |
| QA Engineers | 2 | Testing & quality |
| DevOps Engineer | 1 | CI/CD & infrastructure |
| UI/UX Designer | 1 | Design & user experience |

**Minimum Team Size: 10-12 members**

---

## Getting Started Commands

### Backend Setup
```bash
# Create Spring Boot project
spring init --dependencies=web,data-jpa,security,validation,actuator \
  --java-version=21 --type=maven-project hms-backend

# Run with Maven
./mvnw spring-boot:run

# Run with Docker
docker-compose up -d
```

### Frontend Setup
```bash
# Create Angular project
ng new hms-frontend --routing --style=scss --standalone

# Add Angular Material
ng add @angular/material

# Generate feature module
ng generate module features/patient --route patient --module app.routes

# Run development server
ng serve
```

---

## Next Steps

1. **Finalize Requirements**: Confirm hospital type and priority modules
2. **Setup Development Environment**: Initialize repositories, CI/CD
3. **Database Design Review**: Finalize schema for Phase 1
4. **UI/UX Wireframes**: Design key screens
5. **Sprint Planning**: Break Phase 1 into 2-week sprints
6. **Begin Development**: Start with authentication and patient registration

---

*This plan provides a comprehensive roadmap for building a full-featured Hospital Management System using Spring Boot and Angular. Adjust timelines and features based on specific requirements and team capacity.*
