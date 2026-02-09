export interface Patient {
  id: number;
  uuid: string;
  uhid: string;
  mrNumber: string;
  firstName: string;
  middleName: string;
  lastName: string;
  fullName: string;
  dateOfBirth: string;
  age: number;
  gender: Gender;
  bloodGroup: BloodGroup;
  maritalStatus: MaritalStatus;
  nationality: string;
  religion: string;
  occupation: string;

  // Contact Information
  phonePrimary: string;
  phoneSecondary: string;
  email: string;
  addressLine1: string;
  addressLine2: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;

  // Emergency Contact
  emergencyContactName: string;
  emergencyContactPhone: string;
  emergencyContactRelation: string;

  // Identification
  aadhaarNumber: string;
  abhaId: string;
  passportNumber: string;

  // Medical Information
  allergies: string;
  chronicConditions: string;
  photoUrl: string;
  active: boolean;

  // Audit Fields
  createdAt: string;
  updatedAt: string;
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
  UNKNOWN = 'UNKNOWN'
}

export enum BloodGroup {
  A_POSITIVE = 'A_POSITIVE',
  A_NEGATIVE = 'A_NEGATIVE',
  B_POSITIVE = 'B_POSITIVE',
  B_NEGATIVE = 'B_NEGATIVE',
  AB_POSITIVE = 'AB_POSITIVE',
  AB_NEGATIVE = 'AB_NEGATIVE',
  O_POSITIVE = 'O_POSITIVE',
  O_NEGATIVE = 'O_NEGATIVE',
  UNKNOWN = 'UNKNOWN'
}

export enum MaritalStatus {
  SINGLE = 'SINGLE',
  MARRIED = 'MARRIED',
  DIVORCED = 'DIVORCED',
  WIDOWED = 'WIDOWED',
  SEPARATED = 'SEPARATED',
  UNKNOWN = 'UNKNOWN'
}

export interface PatientRegistrationRequest {
  firstName: string;
  middleName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  bloodGroup?: BloodGroup;
  maritalStatus?: MaritalStatus;
  nationality?: string;
  religion?: string;
  occupation?: string;
  phonePrimary: string;
  phoneSecondary?: string;
  email?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  aadhaarNumber?: string;
  abhaId?: string;
  passportNumber?: string;
  allergies?: string;
  chronicConditions?: string;
}

export interface PatientUpdateRequest extends Partial<PatientRegistrationRequest> {
  active?: boolean;
}
