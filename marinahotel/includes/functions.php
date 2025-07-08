<?php
// تضمين ملف قاعدة البيانات
require_once __DIR__ . '/db.php';

// دالة لتنظيف وتنسيق رقم الهاتف اليمني
function format_yemeni_phone($phone) {
    // إزالة جميع الأحرف غير الرقمية
    $phone = preg_replace('/[^0-9]/', '', $phone);
    // تحويل إلى الصيغة الدولية (967)
    if (strlen($phone) >= 12 && strpos($phone, '00967') === 0) {
        return '967' . substr($phone, 5);
    } elseif (strlen($phone) >= 10 && strpos($phone, '967') === 0) {
        return $phone; // الرقم بالفعل بالصيغة الصحيحة
    } elseif (strlen($phone) >= 9 && strpos($phone, '07') === 0) {
        return '967' . substr($phone, 1);
    } elseif (strlen($phone) >= 8 && strpos($phone, '7') === 0) {
        return '967' . $phone;
    }
    return false; // رقم غير صالح
}

// دالة لحفظ رسائل الواتساب محليا وإرسالها لاحقا
function send_yemeni_whatsapp($phone, $message, $booking_id = null) {
    global $conn;
    
    $phone = format_yemeni_phone($phone);
    if (!$phone) {
        return ['status' => 'error', 'message' => 'رقم الهاتف اليمني غير صالح'];
    }

    try {
        // التأكد من وجود جدول الرسائل
        create_whatsapp_messages_table();
        
        // حفظ الرسالة في قاعدة البيانات
        $sql = "INSERT INTO whatsapp_messages (phone, message, booking_id, status, created_at) VALUES (?, ?, ?, 'pending', NOW())";
        $stmt = $conn->prepare($sql);
        if (!$stmt) {
            throw new Exception("خطأ في تحضير الاستعلام: " . $conn->error);
        }
        
        $stmt->bind_param("ssi", $phone, $message, $booking_id);
        
        if ($stmt->execute()) {
            // محاولة الإرسال الفوري (اختياري - يمكن تعطيله في حالة عدم وجود إنترنت)
            $sent = attempt_immediate_send($phone, $message);
            
            if ($sent) {
                // تحديث حالة الرسالة إلى مرسلة
                $update_sql = "UPDATE whatsapp_messages SET status = 'sent', sent_at = NOW() WHERE id = ?";
                $update_stmt = $conn->prepare($update_sql);
                $update_stmt->bind_param("i", $conn->insert_id);
                $update_stmt->execute();
                
                return ['status' => 'sent', 'message' => 'تم إرسال الرسالة بنجاح'];
            } else {
                return ['status' => 'saved', 'message' => 'تم حفظ الرسالة وسيتم إرسالها عند توفر الإنترنت'];
            }
        } else {
            throw new Exception("خطأ في حفظ الرسالة: " . $stmt->error);
        }
        
    } catch (Exception $e) {
        error_log("خطأ في إرسال رسالة الواتساب: " . $e->getMessage());
        return ['status' => 'error', 'message' => 'فشل في حفظ الرسالة: ' . $e->getMessage()];
    }
}

// دالة لإنشاء جدول رسائل الواتساب
function create_whatsapp_messages_table() {
    global $conn;
    
    $sql = "CREATE TABLE IF NOT EXISTS whatsapp_messages (
        id INT AUTO_INCREMENT PRIMARY KEY,
        phone VARCHAR(20) NOT NULL,
        message TEXT NOT NULL,
        booking_id INT NULL,
        status ENUM('pending', 'sent', 'failed') DEFAULT 'pending',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        sent_at TIMESTAMP NULL,
        error_message TEXT NULL,
        retry_count INT DEFAULT 0,
        INDEX idx_status (status),
        INDEX idx_booking_id (booking_id),
        INDEX idx_created_at (created_at)
    )";
    
    if (!$conn->query($sql)) {
        error_log("خطأ في إنشاء جدول رسائل الواتساب: " . $conn->error);
    }
}

// دالة لمحاولة الإرسال الفوري (اختيارية)
function attempt_immediate_send($phone, $message) {
    // هذه الدالة تحاول الإرسال عبر API خارجي إذا كان الإنترنت متوفر
    // في حالة عدم توفر الإنترنت، ستفشل وستبقى الرسالة محفوظة للإرسال لاحقا
    
    // التحقق من توفر الإنترنت
    if (!is_internet_available()) {
        return false;
    }
    
    try {
        $api_url = "https://wa.nux.my.id/api/sendWA";
        $secret_key = "d4fc5abd713b541b7013f978e8cc4495";

        $url = sprintf(
            "%s?to=%s&msg=%s&secret=%s",
            $api_url,
            urlencode($phone),
            urlencode($message),
            $secret_key
        );

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5); // مهلة قصيرة
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 3);
        $response = curl_exec($ch);
        $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($response !== false && $http_code == 200) {
            $result = json_decode($response, true);
            return isset($result['status']) && $result['status'] === 'sent';
        }
        
        return false;
    } catch (Exception $e) {
        error_log("خطأ في الإرسال الفوري: " . $e->getMessage());
        return false;
    }
}

// دالة للتحقق من توفر الإنترنت
function is_internet_available() {
    $connected = @fsockopen("www.google.com", 80, $errno, $errstr, 2);
    if ($connected) {
        fclose($connected);
        return true;
    }
    return false;
}

// دالة لجلب الرسائل المعلقة
function get_pending_whatsapp_messages($limit = 50) {
    global $conn;
    
    $sql = "SELECT * FROM whatsapp_messages WHERE status = 'pending' ORDER BY created_at ASC LIMIT ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $limit);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $messages = [];
    while ($row = $result->fetch_assoc()) {
        $messages[] = $row;
    }
    
    return $messages;
}

// دالة لمعالجة الرسائل المعلقة (يمكن تشغيلها من cron job)
function process_pending_whatsapp_messages() {
    $messages = get_pending_whatsapp_messages();
    $processed = 0;
    $sent = 0;
    
    foreach ($messages as $message) {
        $processed++;
        
        if (attempt_immediate_send($message['phone'], $message['message'])) {
            // تحديث حالة الرسالة إلى مرسلة
            update_message_status($message['id'], 'sent');
            $sent++;
        } else {
            // زيادة عداد المحاولات
            increment_retry_count($message['id']);
            
            // إذا تجاوز عدد المحاولات الحد الأقصى، وضع علامة فشل
            if ($message['retry_count'] >= 5) {
                update_message_status($message['id'], 'failed', 'تجاوز الحد الأقصى للمحاولات');
            }
        }
    }
    
    return ['processed' => $processed, 'sent' => $sent];
}

// دالة لتحديث حالة الرسالة
function update_message_status($message_id, $status, $error_message = null) {
    global $conn;
    
    if ($status === 'sent') {
        $sql = "UPDATE whatsapp_messages SET status = ?, sent_at = NOW() WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("si", $status, $message_id);
    } else {
        $sql = "UPDATE whatsapp_messages SET status = ?, error_message = ? WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ssi", $status, $error_message, $message_id);
    }
    
    return $stmt->execute();
}

// دالة لزيادة عداد المحاولات
function increment_retry_count($message_id) {
    global $conn;
    
    $sql = "UPDATE whatsapp_messages SET retry_count = retry_count + 1 WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $message_id);
    return $stmt->execute();
}

// دالة لإنشاء إشعار نظام محلي
function create_system_notification($title, $message, $type = 'info', $user_id = null) {
    global $conn;
    
    // التأكد من وجود جدول الإشعارات
    create_notifications_table();
    
    try {
        $sql = "INSERT INTO system_notifications (title, message, type, user_id, created_at) VALUES (?, ?, ?, ?, NOW())";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssi", $title, $message, $type, $user_id);
        
        return $stmt->execute();
    } catch (Exception $e) {
        error_log("خطأ في إنشاء الإشعار: " . $e->getMessage());
        return false;
    }
}

// دالة لإنشاء جدول الإشعارات
function create_notifications_table() {
    global $conn;
    
    $sql = "CREATE TABLE IF NOT EXISTS system_notifications (
        id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        message TEXT NOT NULL,
        type ENUM('success', 'error', 'warning', 'info') DEFAULT 'info',
        user_id INT NULL,
        is_read BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        read_at TIMESTAMP NULL,
        INDEX idx_user_id (user_id),
        INDEX idx_is_read (is_read),
        INDEX idx_created_at (created_at)
    )";
    
    if (!$conn->query($sql)) {
        error_log("خطأ في إنشاء جدول الإشعارات: " . $conn->error);
    }
}

// دالة لجلب الإشعارات غير المقروءة
function get_unread_notifications($user_id = null, $limit = 10) {
    global $conn;
    
    if ($user_id) {
        $sql = "SELECT * FROM system_notifications WHERE (user_id = ? OR user_id IS NULL) AND is_read = FALSE ORDER BY created_at DESC LIMIT ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ii", $user_id, $limit);
    } else {
        $sql = "SELECT * FROM system_notifications WHERE is_read = FALSE ORDER BY created_at DESC LIMIT ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $limit);
    }
    
    $stmt->execute();
    $result = $stmt->get_result();
    
    $notifications = [];
    while ($row = $result->fetch_assoc()) {
        $notifications[] = $row;
    }
    
    return $notifications;
}

// دالة لوضع علامة مقروء على الإشعار
function mark_notification_as_read($notification_id) {
    global $conn;
    
    $sql = "UPDATE system_notifications SET is_read = TRUE, read_at = NOW() WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $notification_id);
    
    return $stmt->execute();
}

// إنشاء الجداول عند تحميل الملف
create_whatsapp_messages_table();
create_notifications_table();
?>
