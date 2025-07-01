<?php
session_start();
require_once '../../includes/db.php';

// التحقق من تسجيل الدخول
if (!isset($_SESSION['user_id'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Unauthorized']);
    exit();
}

// عدد الإشعارات غير المقروءة
$unread_count_query = "SELECT COUNT(*) as count FROM shift_notifications WHERE (to_user_id = ? OR to_user_id IS NULL) AND is_read = 0";
$unread_stmt = $conn->prepare($unread_count_query);
$unread_stmt->bind_param("i", $_SESSION['user_id']);
$unread_stmt->execute();
$unread_result = $unread_stmt->get_result();
$unread_count = $unread_result->fetch_assoc()['count'];
$unread_stmt->close();

// إرجاع النتيجة كـ JSON
header('Content-Type: application/json');
echo json_encode([
    'unread_count' => (int)$unread_count,
    'timestamp' => time()
]);
?>

