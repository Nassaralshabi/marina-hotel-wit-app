<?php
session_start();
require_once '../../includes/db.php';

// التحقق من تسجيل الدخول
if (!isset($_SESSION['user_id'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Unauthorized']);
    exit();
}

header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];
$action = $_GET['action'] ?? '';

try {
    switch ($method) {
        case 'GET':
            if ($action === 'notifications') {
                // جلب الإشعارات
                $limit = (int)($_GET['limit'] ?? 20);
                $offset = (int)($_GET['offset'] ?? 0);
                
                $query = "
                    SELECT 
                        sn.*,
                        u_from.full_name as from_user_name,
                        u_to.full_name as to_user_name
                    FROM shift_notifications sn
                    LEFT JOIN users u_from ON sn.from_user_id = u_from.id
                    LEFT JOIN users u_to ON sn.to_user_id = u_to.id
                    WHERE sn.to_user_id = ? OR sn.to_user_id IS NULL
                    ORDER BY sn.created_at DESC
                    LIMIT ? OFFSET ?
                ";
                
                $stmt = $conn->prepare($query);
                $stmt->bind_param("iii", $_SESSION['user_id'], $limit, $offset);
                $stmt->execute();
                $result = $stmt->get_result();
                $notifications = $result->fetch_all(MYSQLI_ASSOC);
                $stmt->close();
                
                echo json_encode([
                    'success' => true,
                    'notifications' => $notifications
                ]);
                
            } elseif ($action === 'unread_count') {
                // عدد الإشعارات غير المقروءة
                $query = "SELECT COUNT(*) as count FROM shift_notifications WHERE (to_user_id = ? OR to_user_id IS NULL) AND is_read = 0";
                $stmt = $conn->prepare($query);
                $stmt->bind_param("i", $_SESSION['user_id']);
                $stmt->execute();
                $result = $stmt->get_result();
                $count = $result->fetch_assoc()['count'];
                $stmt->close();
                
                echo json_encode([
                    'success' => true,
                    'unread_count' => (int)$count
                ]);
                
            } else {
                throw new Exception('Invalid action');
            }
            break;
            
        case 'POST':
            $input = json_decode(file_get_contents('php://input'), true);
            
            if ($action === 'send') {
                // إرسال إشعار جديد
                $title = trim($input['title'] ?? '');
                $message = trim($input['message'] ?? '');
                $priority = $input['priority'] ?? 'medium';
                $to_user_id = $input['to_user_id'] === 'all' ? NULL : (int)$input['to_user_id'];
                
                if (empty($title) || empty($message)) {
                    throw new Exception('Title and message are required');
                }
                
                $stmt = $conn->prepare("INSERT INTO shift_notifications (from_user_id, to_user_id, title, message, priority) VALUES (?, ?, ?, ?, ?)");
                $stmt->bind_param("iisss", $_SESSION['user_id'], $to_user_id, $title, $message, $priority);
                
                if ($stmt->execute()) {
                    echo json_encode([
                        'success' => true,
                        'message' => 'تم إرسال الإشعار بنجاح',
                        'notification_id' => $conn->insert_id
                    ]);
                } else {
                    throw new Exception('Failed to send notification');
                }
                $stmt->close();
                
            } elseif ($action === 'mark_read') {
                // تحديد إشعار كمقروء
                $notification_id = (int)($input['notification_id'] ?? 0);
                
                if ($notification_id <= 0) {
                    throw new Exception('Invalid notification ID');
                }
                
                $stmt = $conn->prepare("UPDATE shift_notifications SET is_read = 1, read_at = NOW() WHERE id = ? AND (to_user_id = ? OR to_user_id IS NULL)");
                $stmt->bind_param("ii", $notification_id, $_SESSION['user_id']);
                
                if ($stmt->execute()) {
                    echo json_encode([
                        'success' => true,
                        'message' => 'تم تحديث حالة الإشعار'
                    ]);
                } else {
                    throw new Exception('Failed to update notification');
                }
                $stmt->close();
                
            } elseif ($action === 'mark_all_read') {
                // تحديد جميع الإشعارات كمقروءة
                $stmt = $conn->prepare("UPDATE shift_notifications SET is_read = 1, read_at = NOW() WHERE (to_user_id = ? OR to_user_id IS NULL) AND is_read = 0");
                $stmt->bind_param("i", $_SESSION['user_id']);
                
                if ($stmt->execute()) {
                    $affected_rows = $stmt->affected_rows;
                    echo json_encode([
                        'success' => true,
                        'message' => "تم تحديث $affected_rows إشعار",
                        'affected_rows' => $affected_rows
                    ]);
                } else {
                    throw new Exception('Failed to update notifications');
                }
                $stmt->close();
                
            } else {
                throw new Exception('Invalid action');
            }
            break;
            
        case 'DELETE':
            if ($action === 'delete') {
                // حذف إشعار (للمرسل فقط أو المدير)
                $notification_id = (int)($_GET['id'] ?? 0);
                
                if ($notification_id <= 0) {
                    throw new Exception('Invalid notification ID');
                }
                
                // التحقق من الصلاحية
                $check_query = "SELECT from_user_id FROM shift_notifications WHERE id = ?";
                $check_stmt = $conn->prepare($check_query);
                $check_stmt->bind_param("i", $notification_id);
                $check_stmt->execute();
                $check_result = $check_stmt->get_result();
                $notification = $check_result->fetch_assoc();
                $check_stmt->close();
                
                if (!$notification) {
                    throw new Exception('Notification not found');
                }
                
                // يمكن للمرسل أو المدير حذف الإشعار
                if ($notification['from_user_id'] != $_SESSION['user_id'] && $_SESSION['user_type'] !== 'admin') {
                    throw new Exception('Permission denied');
                }
                
                $stmt = $conn->prepare("DELETE FROM shift_notifications WHERE id = ?");
                $stmt->bind_param("i", $notification_id);
                
                if ($stmt->execute()) {
                    echo json_encode([
                        'success' => true,
                        'message' => 'تم حذف الإشعار'
                    ]);
                } else {
                    throw new Exception('Failed to delete notification');
                }
                $stmt->close();
                
            } else {
                throw new Exception('Invalid action');
            }
            break;
            
        default:
            throw new Exception('Method not allowed');
    }
    
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => $e->getMessage()
    ]);
}
?>

