-- Insert sample users for testing the 3-level workflow system
-- Note: Passwords are encoded using BCrypt - raw password is 'password123'
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_date, last_modified_date) VALUES
('admin', 'admin@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'System', 'Administrator', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l1maker1', 'l1maker1@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L1', 'Maker1', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l1maker2', 'l1maker2@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L1', 'Maker2', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l1checker1', 'l1checker1@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L1', 'Checker1', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l1checker2', 'l1checker2@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L1', 'Checker2', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l2maker1', 'l2maker1@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L2', 'Maker1', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l2checker1', 'l2checker1@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L2', 'Checker1', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('l3decision1', 'l3decision1@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'L3', 'DecisionMaker1', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('testuser1', 'testuser1@workflowplatform.com', '$2a$10$N8R.QTKTklKWyUhNUgvmhuXAXBj6oVXxJYNa5YhKpT7D5XJlvOGe6', 'Test', 'User1', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
INSERT INTO user_roles (user_id, role) VALUES
-- Admin user
(1, 'ROLE_ADMIN'),
(1, 'ROLE_PROCESS_MANAGER'),
-- L1 Makers
(2, 'ROLE_L1_MAKER'),
(2, 'ROLE_USER'),
(3, 'ROLE_L1_MAKER'),
(3, 'ROLE_USER'),
-- L1 Checkers
(4, 'ROLE_L1_CHECKER'),
(4, 'ROLE_USER'),
(5, 'ROLE_L1_CHECKER'),
(5, 'ROLE_USER'),
-- L2 Maker
(6, 'ROLE_L2_MAKER'),
(6, 'ROLE_USER'),
-- L2 Checker
(7, 'ROLE_L2_CHECKER'),
(7, 'ROLE_USER'),
-- L3 Decision Maker
(8, 'ROLE_L3_DECISION_MAKER'),
(8, 'ROLE_USER'),
-- Regular test user
(9, 'ROLE_USER');

-- Insert sample workflow cases for testing
INSERT INTO workflow_cases (case_title, description, status, current_level, created_by, created_date, last_modified_date) VALUES
('Investment Portfolio Review Q1 2024', 'Quarterly review of investment portfolio allocations and risk assessments', 'PENDING', 'L1', 'testuser1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Risk Assessment - High Value Client', 'Risk assessment and model selection for high-value client portfolio', 'IN_PROGRESS', 'L1', 'testuser1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Strategic Asset Allocation Review', 'Annual strategic asset allocation review requiring model validation', 'PENDING', 'L1', 'testuser1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample decision items for the workflow cases
INSERT INTO decision_items (item_name, description, workflow_case_id, created_date, last_modified_date) VALUES
-- For Case 1: Investment Portfolio Review Q1 2024
('Equity Allocation Model', 'Selection of appropriate model for equity allocation across domestic and international markets', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fixed Income Strategy', 'Determination of fixed income allocation model based on interest rate environment', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alternative Investment Framework', 'Model selection for alternative investments including REITs and commodities', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- For Case 2: Risk Assessment - High Value Client
('Client Risk Profile Assessment', 'Risk tolerance and capacity evaluation for high net worth individual', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Portfolio Optimization Model', 'Selection of optimization approach for client-specific constraints', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Performance Attribution Framework', 'Method for ongoing performance measurement and attribution', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- For Case 3: Strategic Asset Allocation Review
('Long-term Market Outlook Model', 'Model for projecting long-term asset class returns and volatilities', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Liability Matching Strategy', 'Approach for matching assets to institutional liability profile', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ESG Integration Framework', 'Environmental, Social, and Governance factor integration methodology', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);