CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE,
    full_name VARCHAR(150),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,   -- ví dụ: admin, user, editor
    description VARCHAR(255)
);
CREATE TABLE permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,  -- ví dụ: view_users, edit_users
    description VARCHAR(255)
);

CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE menus (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,          -- Tên menu (vd: Quản lý người dùng)
    icon VARCHAR(50),                    -- Icon (vd: "fa-users")
    path VARCHAR(150),                   -- Đường dẫn Vue (vd: "/users")
    parent_id INT NULL,                  -- Menu cha (nếu là menu con)
    order_index INT DEFAULT 0,           -- Thứ tự sắp xếp
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (parent_id) REFERENCES menus(id) ON DELETE SET NULL
);
CREATE TABLE menu_roles (
    menu_id INT,
    role_id INT,
    PRIMARY KEY (menu_id, role_id),
    FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE menu_permissions (
    menu_id INT,
    permission_id INT,
    PRIMARY KEY (menu_id, permission_id),
    FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);
CREATE TABLE refresh_tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(255) UNIQUE NOT NULL,
  user_id INT NOT NULL,
  expiry_date DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Thêm bảng blacklist token (nếu bạn muốn lưu access token bị logout):
CREATE TABLE token_blacklist (
  id INT AUTO_INCREMENT PRIMARY KEY,
  jti VARCHAR(255) UNIQUE NOT NULL,
  token VARCHAR(2000) NOT NULL,
  blacklisted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  expiry_date DATETIME
);
-- users ─┬─< user_roles >─┬─ roles ─┬─< role_permissions >─┬─ permissions
--        │                │         │                      │
--        │                │         └─< menu_roles >──────> menus
--        │                │
--        │                └─< menu_permissions >──────────> menus