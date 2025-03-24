INSERT INTO user (id, name, email, is_private, password, status, created_at, updated_at)
VALUES
    (1, 'name', 'tester_1@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (2, 'name', 'update_tester@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (3, 'name', 'delete_tester1@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (4, 'name', 'delete_tester2@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'DELETED', sysdate(), sysdate()),
    (5, 'name', 'public_tester@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (6, 'name', 'private_tester@test.com', true, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (7, 'follow_test', 'follow_public_tester@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (8, 'follow_test', 'follow_private_tester@test.com', true, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (9, 'profile-image', 'profile-image_tester@test.com', true, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (10, 'name', 'find-private@test.com', true, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (11, 'followers', 'find-followers@test.com', false, '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate());
