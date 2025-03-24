INSERT INTO follow (id, requester_id, target_id, status, created_at, updated_at)
VALUES
    (1, 7,1, 'PENDING', sysdate(), sysdate()),
    (2, 8,1, 'PENDING', sysdate(), sysdate()),
    (3, 8,7, 'PENDING', sysdate(), sysdate()),
    (4, 2,7, 'PENDING', sysdate(), sysdate()),
    (5, 2,8, 'ACCEPTED', sysdate(), sysdate()),
    (6, 7,8, 'PENDING', sysdate(), sysdate()),
    (7, 1,2, 'PENDING', sysdate(), sysdate()),
    (8, 1,10, 'ACCEPTED', sysdate(), sysdate()),
    (9, 1,11, 'ACCEPTED', sysdate(), sysdate());
