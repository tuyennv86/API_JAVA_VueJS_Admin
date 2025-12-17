-- ============================
--  RBAC + MENU STRUCTURE DEMO
-- ============================
-- Compatible with MySQL 8.x
-- ============================

DROP TABLE IF EXISTS menu_permissions;
DROP TABLE IF EXISTS menu_roles;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

-- ============================
-- USERS
-- ============================
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- ROLES
-- ============================
CREATE TABLE roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- PERMISSIONS
-- ============================
CREATE TABLE permissions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(255)
);

-- ============================
-- USER_ROLES
-- ============================
CREATE TABLE user_roles (
  user_id INT,
  role_id INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ============================
-- ROLE_PERMISSIONS
-- ============================
CREATE TABLE role_permissions (
  role_id INT,
  permission_id INT,
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES roles(id),
  FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- ============================
-- MENUS
-- ============================
CREATE TABLE menus (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  path VARCHAR(100),
  icon VARCHAR(50),
  parent_id INT NULL,
  sort_order INT DEFAULT 0,
  FOREIGN KEY (parent_id) REFERENCES menus(id)
);

-- ============================
-- MENU_ROLES
-- ============================
CREATE TABLE menu_roles (
  menu_id INT,
  role_id INT,
  PRIMARY KEY (menu_id, role_id),
  FOREIGN KEY (menu_id) REFERENCES menus(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ============================
-- MENU_PERMISSIONS
-- ============================
CREATE TABLE menu_permissions (
  menu_id INT,
  permission_id INT,
  PRIMARY KEY (menu_id, permission_id),
  FOREIGN KEY (menu_id) REFERENCES menus(id),
  FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- ============================
-- INSERT SAMPLE DATA
-- ============================

-- Users
INSERT INTO users (username, password_hash, email) VALUES
('admin', '$2y$10$EXAMPLEHASHFORADMIN1234567890abcdefghiJKLmnopQRstu', 'admin@example.com'),
('user', '$2y$10$EXAMPLEHASHFORUSER1234567890abcdefghiJKLmnopQRstu', 'user@example.com');

-- Roles
INSERT INTO roles (name, description) VALUES
('Admin', 'Full access to system'),
('User', 'Limited access');

-- Permissions
INSERT INTO permissions (name, description) VALUES
('view_users', 'View user list'),
('edit_users', 'Edit user information'),
('view_menu', 'View menu items'),
('edit_menu', 'Modify menu items'),
('manage_roles', 'Manage roles and permissions');

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),  -- admin -> Admin
(2, 2);  -- user -> User

-- Role Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5), -- Admin có tất cả quyền
(2, 1),
(2, 3); -- User chỉ có quyền xem user và menu

-- Menus
INSERT INTO menus (name, path, icon, parent_id, sort_order) VALUES
('Dashboard', '/dashboard', 'home', NULL, 1),
('Users', '/users', 'users', NULL, 2),
('Roles', '/roles', 'shield', NULL, 3),
('Settings', '/settings', 'settings', NULL, 4);

-- Menu Roles
INSERT INTO menu_roles (menu_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1), -- Admin thấy tất cả menu
(1, 2),
(2, 2); -- User chỉ thấy Dashboard + Users

-- Menu Permissions
INSERT INTO menu_permissions (menu_id, permission_id) VALUES
(2, 1),
(2, 2),
(3, 5),
(4, 4);
