-- 1. Bảng users 
INSERT INTO users (id, username, password_hash, email, full_name, is_active)
VALUES
(1, 'admin', '$2y$10$abcdef1234567890', 'admin@example.com', 'Super Admin', 1),
(2, 'editor', '$2y$10$abcdef1234567891', 'editor@example.com', 'Content Editor', 1),
(3, 'user1', '$2y$10$abcdef1234567892', 'user1@example.com', 'Normal User', 1);

-- 2. Bảng roles
INSERT INTO roles (id, name, description)
VALUES
(1, 'admin', 'Quản trị hệ thống'),
(2, 'editor', 'Người biên tập nội dung'),
(3, 'user', 'Người dùng thông thường');

-- 3. Bảng permissions
INSERT INTO permissions (id, name, description)
VALUES
(1, 'view_users', 'Xem danh sách người dùng'),
(2, 'edit_users', 'Chỉnh sửa người dùng'),
(3, 'delete_users', 'Xóa người dùng'),
(4, 'view_posts', 'Xem bài viết'),
(5, 'create_posts', 'Tạo bài viết'),
(6, 'edit_posts', 'Chỉnh sửa bài viết'),
(7, 'delete_posts', 'Xóa bài viết');
-- 4. Bảng user_roles 
INSERT INTO user_roles (user_id, role_id)
VALUES
(1, 1),  -- admin
(2, 2),  -- editor
(3, 3);  -- user thường

-- 5. Bảng role_permissions
-- Admin có tất cả quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- Editor: quyền về bài viết
INSERT INTO role_permissions (role_id, permission_id)
VALUES
(2, 4), -- view_posts
(2, 5), -- create_posts
(2, 6); -- edit_posts

-- User thường: chỉ được xem bài viết
INSERT INTO role_permissions (role_id, permission_id)
VALUES
(3, 4);

-- 6. Bảng menus 
INSERT INTO menus (id, name, icon, path, parent_id, order_index)
VALUES
(1, 'Dashboard', 'fa-home', '/dashboard', NULL, 1),
(2, 'Quản lý người dùng', 'fa-users', NULL, NULL, 2),
(3, 'Danh sách người dùng', '', '/users', 2, 1),
(4, 'Phân quyền', '', '/roles', 2, 2),
(5, 'Bài viết', 'fa-file-alt', NULL, NULL, 3),
(6, 'Tất cả bài viết', '', '/posts', 5, 1),
(7, 'Thêm bài viết', '', '/posts/new', 5, 2);

-- 7. Bảng menu_roles 
-- Admin: thấy tất cả menu
INSERT INTO menu_roles (menu_id, role_id)
SELECT id, 1 FROM menus;

-- Editor: chỉ thấy bài viết
INSERT INTO menu_roles (menu_id, role_id)
VALUES
(1, 2), -- Dashboard
(5, 2), -- Bài viết
(6, 2), -- Tất cả bài viết
(7, 2); -- Thêm bài viết

-- User thường: chỉ thấy Dashboard
INSERT INTO menu_roles (menu_id, role_id)
VALUES
(1, 3);

-- 8. Bảng menu_permissions

INSERT INTO menu_permissions (menu_id, permission_id)
VALUES
(3, 1),  -- menu "Danh sách người dùng" cần quyền view_users
(4, 2),  -- menu "Phân quyền" cần quyền edit_users
(6, 4),  -- menu "Tất cả bài viết" cần quyền view_posts
(7, 5);  -- menu "Thêm bài viết" cần quyền create_posts


