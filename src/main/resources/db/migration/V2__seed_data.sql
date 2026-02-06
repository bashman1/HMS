-- HMS Authentication Service - Seed Data
-- Version: 2.0.0 - Hospital-specific roles

-- Insert permissions
INSERT INTO auth.permissions (id, name, description, resource, action) VALUES
    -- User permissions
    ('c1a1c1a1-0000-0000-0000-000000000001', 'users:read', 'View user information', 'users', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000002', 'users:write', 'Create or update users', 'users', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000003', 'users:delete', 'Delete users', 'users', 'delete'),
    ('c1a1c1a1-0000-0000-0000-000000000004', 'users:manage', 'Full access to users', 'users', 'manage'),
    
    -- Role permissions
    ('c1a1c1a1-0000-0000-0000-000000000005', 'roles:read', 'View role information', 'roles', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000006', 'roles:write', 'Create or update roles', 'roles', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000007', 'roles:delete', 'Delete roles', 'roles', 'delete'),
    ('c1a1c1a1-0000-0000-0000-000000000008', 'roles:manage', 'Full access to roles', 'roles', 'manage'),
    
    -- Self-management permissions
    ('c1a1c1a1-0000-0000-0000-000000000009', 'self:read', 'View own profile', 'self', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000010', 'self:write', 'Update own profile', 'self', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000011', 'self:password', 'Change own password', 'self', 'password'),
    
    -- Medical record permissions
    ('c1a1c1a1-0000-0000-0000-000000000020', 'medical:read', 'View medical records', 'medical', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000021', 'medical:write', 'Create medical records', 'medical', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000022', 'medical:update', 'Update medical records', 'medical', 'update'),
    
    -- Appointment permissions
    ('c1a1c1a1-0000-0000-0000-000000000030', 'appointments:read', 'View appointments', 'appointments', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000031', 'appointments:write', 'Create appointments', 'appointments', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000032', 'appointments:manage', 'Manage all appointments', 'appointments', 'manage'),
    
    -- Billing permissions
    ('c1a1c1a1-0000-0000-0000-000000000040', 'billing:read', 'View billing information', 'billing', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000041', 'billing:write', 'Create billing records', 'billing', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000042', 'billing:process', 'Process payments', 'billing', 'process'),
    
    -- Laboratory permissions
    ('c1a1c1a1-0000-0000-0000-000000000050', 'lab:read', 'View lab results', 'lab', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000051', 'lab:write', 'Create lab orders', 'lab', 'write'),
    ('c1a1c1a1-0000-0000-0000-000000000052', 'lab:result', 'Enter lab results', 'lab', 'result'),
    
    -- Pharmacy permissions
    ('c1a1c1a1-0000-0000-0000-000000000060', 'pharmacy:read', 'View prescriptions', 'pharmacy', 'read'),
    ('c1a1c1a1-0000-0000-0000-000000000061', 'pharmacy:dispense', 'Dispense medication', 'pharmacy', 'dispense')
ON CONFLICT (id) DO NOTHING;

-- Insert roles
INSERT INTO auth.roles (id, name, description, is_default, is_system) VALUES
    -- General roles
    ('d2a2c2a2-0000-0000-0000-000000000001', 'USER', 'Standard user role', TRUE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000002', 'ADMIN', 'Administrator role with full access', FALSE, TRUE),
    
    -- Hospital Staff Roles
    ('d2a2c2a2-0000-0000-0000-000000000010', 'DOCTOR', 'Medical Doctor', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000011', 'NURSE', 'Nurse', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000012', 'LAB_TECHNICIAN', 'Laboratory Technician', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000013', 'PHARMACIST', 'Pharmacist', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000014', 'CASHIER', 'Billing Cashier', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000015', 'RECEPTIONIST', 'Hospital Receptionist', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000016', 'LAB_STAFF', 'General Lab Staff', FALSE, FALSE),
    ('d2a2c2a2-0000-0000-0000-000000000017', 'HOSPITAL_ADMIN', 'Hospital Administrator', FALSE, FALSE),
    
    -- Patient role (for patient portal access)
    ('d2a2c2a2-0000-0000-0000-000000000020', 'PATIENT', 'Patient - can access own records', FALSE, FALSE)
ON CONFLICT (id) DO NOTHING;

-- Assign permissions to USER role (basic authenticated user)
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'USER'),
    id
FROM auth.permissions
WHERE name IN ('users:read', 'self:read', 'self:write', 'self:password')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to ADMIN role (full access)
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'ADMIN'),
    id
FROM auth.permissions
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to DOCTOR role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'DOCTOR'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'self:read', 'self:write', 'self:password',
    'medical:read', 'medical:write', 'medical:update',
    'appointments:read', 'appointments:write',
    'lab:read', 'lab:write',
    'pharmacy:read'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to NURSE role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'NURSE'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'self:read', 'self:write', 'self:password',
    'medical:read', 'medical:write',
    'appointments:read',
    'lab:read',
    'pharmacy:read', 'pharmacy:dispense'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to LAB_TECHNICIAN role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'LAB_TECHNICIAN'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'self:read', 'self:write', 'self:password',
    'lab:read', 'lab:write', 'lab:result',
    'appointments:read'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to PHARMACIST role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'PHARMACIST'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'self:read', 'self:write', 'self:password',
    'medical:read',
    'pharmacy:read', 'pharmacy:dispense'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to CASHIER role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'CASHIER'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'self:read', 'self:write', 'self:password',
    'billing:read', 'billing:write', 'billing:process'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to RECEPTIONIST role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'RECEPTIONIST'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'self:read', 'self:write', 'self:password',
    'appointments:read', 'appointments:write',
    'users:read'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to HOSPITAL_ADMIN role (full hospital access)
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'HOSPITAL_ADMIN'),
    id
FROM auth.permissions
WHERE name IN (
    'users:read', 'users:write',
    'medical:read',
    'appointments:read', 'appointments:write', 'appointments:manage',
    'billing:read', 'billing:write',
    'lab:read', 'lab:write',
    'pharmacy:read'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to PATIENT role (limited access to own records)
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM auth.roles WHERE name = 'PATIENT'),
    id
FROM auth.permissions
WHERE name IN (
    'self:read', 'self:write', 'self:password',
    'medical:read',
    'appointments:read',
    'billing:read',
    'lab:read',
    'pharmacy:read'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;
