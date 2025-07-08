<?php
// ملف معالجة رسائل الواتساب المعلقة
// يمكن تشغيله من cron job كل 5 دقائق

require_once 'includes/db.php';
require_once 'includes/functions.php';

echo "بدء معالجة رسائل الواتساب المعلقة...\n";
echo "الوقت: " . date('Y-m-d H:i:s') . "\n";

try {
    $result = process_pending_whatsapp_messages();
    
    echo "تم معالجة: {$result['processed']} رسالة\n";
    echo "تم إرسال: {$result['sent']} رسالة\n";
    
    // إنشاء إشعار نظام إذا تم إرسال رسائل
    if ($result['sent'] > 0) {
        create_system_notification(
            "إرسال رسائل واتساب",
            "تم إرسال {$result['sent']} رسالة واتساب بنجاح",
            "success"
        );
    }
    
    // إنشاء ملف log
    $log_entry = date('Y-m-d H:i:s') . " - معالج: {$result['processed']}, مرسل: {$result['sent']}\n";
    file_put_contents('logs/whatsapp_queue.log', $log_entry, FILE_APPEND | LOCK_EX);
    
    echo "تمت المعالجة بنجاح\n";
    
} catch (Exception $e) {
    $error_msg = "خطأ في معالجة رسائل الواتساب: " . $e->getMessage();
    echo $error_msg . "\n";
    
    // تسجيل الخطأ
    error_log($error_msg);
    
    // إنشاء إشعار خطأ
    create_system_notification(
        "خطأ في إرسال الواتساب",
        $error_msg,
        "error"
    );
}

echo "انتهت المعالجة\n";
echo "================================\n";
?>