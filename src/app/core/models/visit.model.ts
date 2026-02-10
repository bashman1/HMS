export interface Visit {
  uuid: string;
  visitNumber: string;

  // Patient Information
  patientUuid: string;
  patientUhid: string;
  patientName: string;
  patientId: number;

  visitType: VisitType;
  visitDate: string;
  departmentId: number;
  departmentName: string;
  doctorId: number;
  doctorName: string;
  status: VisitStatus;
  chiefComplaint: string;
  notes: string;

  // Consultation Details
  diagnosis: string;
  treatmentPlan: string;
  prescription: string;

  // Referral Details
  referredDepartmentId: number;
  referredDepartmentName: string;
  referralReason: string;

  tokenNumber: number;
  priority: number;
  checkInTime: string;
  consultationStartTime: string;
  consultationEndTime: string;
  followUp: boolean;
  previousVisitId: number;
  consultationFee: number;
  billed: boolean;

  // Audit Fields
  createdAt: string;
  updatedAt: string;
}

export enum VisitType {
  OPD = 'OPD',
  IPD = 'IPD',
  EMERGENCY = 'EMERGENCY',
  FOLLOW_UP = 'FOLLOW_UP',
  CONSULTATION = 'CONSULTATION'
}

export enum VisitStatus {
  REGISTERED = 'REGISTERED',
  IN_QUEUE = 'IN_QUEUE',
  IN_CONSULTATION = 'IN_CONSULTATION',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW',
  REFERRED = 'REFERRED'
}

export interface CreateVisitRequest {
  patientId: number;
  visitType: VisitType;
  departmentId: number;
  departmentName?: string;
  doctorId?: number;
  doctorName?: string;
  chiefComplaint?: string;
  notes?: string;
  isFollowUp?: boolean;
  priority?: number;
}

export interface UpdateVisitRequest {
  visitType?: VisitType;
  departmentId?: number;
  departmentName?: string;
  doctorId?: number;
  doctorName?: string;
  chiefComplaint?: string;
  notes?: string;
  isFollowUp?: boolean;
  priority?: number;
  diagnosis?: string;
  treatmentPlan?: string;
  prescription?: string;
  referredDepartmentId?: number;
  referredDepartmentName?: string;
  referralReason?: string;
}
