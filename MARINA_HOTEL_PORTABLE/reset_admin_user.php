<?php
/**
 * إعادة تعيين أو إنشاء المستخدم Admin
 * يضمن وجود المستخدم admin بكلمة المرور 1234
 */

require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';

echo "🔧 إعادة تعيين المستخدم Admin...\n\n";

if (!$conn) {
    die("❌ خطأ في الاتصال بقاعدة البيانات: " . mysqli_connect_error() . "\n");
}

echo "✅ تم الاتصال بقاعدة البيانات\n";

// التحقق من وجود المستخدم admin
$stmt = $conn->prepare("SELECT user_id FROM users WHERE username = 'admin'");
$stmt->execute();
$result = $stmt->get_result();
$user_exists = $result->num_rows > 0;
$stmt->close();

$password = '1234';
$password_hash = password_hash($password, PASSWORD_DEFAULT);
$full_name = 'مدير النظام';

if ($user_exists) {
    echo "📝 المستخدم admin موجود، سيتم تحديثه...\n";
    
    $stmt = $conn->prepare("
        UPDATE users 
        SET password = ?, 
            password_hash = ?, 
            full_name = ?, 
            user_type = 'admin', 
            is_active = 1,
            updated_at = CURRENT_TIMESTAMP
        WHERE username = 'admin'
    ");
    $stmt->bind_param('sss', $password, $password_hash, $full_name);
    
    if ($stmt->execute()) {
        echo "✅ تم تحديث المستخدم admin بنجاح\n";
    } else {
        echo "❌ خطأ في تحديث المستخدم: " . $stmt->error . "\n";
    }
    $stmt->close();
    
} else {
    echo "➕ المستخدم admin غير موجود، سيتم إنشاؤه...\n";
    
    $stmt = $conn->prepare("
        INSERT INTO users (username, password, password_hash, full_name, user_type, is_active, created_at, updated_at) 
        VALUES ('admin', ?, ?, ?, 'admin', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ");
    $stmt->bind_param('sss', $password, $password_hash, $full_name);
    
    if ($stmt->execute()) {
        echo "✅ تم إنشاء المستخدم admin بنجاح\n";
    } else {
        echo "❌ خطأ في إنشاء المستخدم: " . $stmt->error . "\n";
    }
    $stmt->close();
}

// التحقق من النتيجة النهائية
$stmt = $conn->prepare("
    SELECT user_id, username, full_name, user_type, is_active, created_at 
    FROM users 
    WHERE username = 'admin'
");
$stmt->execute();
$admin_user = $stmt->get_result()->fetch_assoc();
$stmt->close();

if ($admin_user) {
    echo "\n📊 معلومات المستخدم admin:\n";
    echo "   • معرف المستخدم: {$admin_user['user_id']}\n";
    echo "   • اسم المستخدم: {$admin_user['username']}\n";
    echo "   • الاسم الكامل: {$admin_user['full_name']}\n";
    echo "   • نوع المستخدم: {$admin_user['user_type']}\n";
    echo "   • نشط: " . ($admin_user['is_active'] ? 'نعم' : 'لا') . "\n";
    echo "   • تاريخ الإنشاء: {$admin_user['created_at']}\n";
    
    // اختبار تسجيل الدخول
    echo "\n🔐 اختبار كلمة المرور...\n";
    
    $stmt = $conn->prepare("SELECT password, password_hash FROM users WHERE username = 'admin'");
    $stmt->execute();
    $pass_data = $stmt->get_result()->fetch_assoc();
    $stmt->close();
    
    $test_password = '1234';
    $verified = false;
    
    if (!empty($pass_data['password_hash'])) {
        $verified = password_verify($test_password, $pass_data['password_hash']);
        if ($verified) {
            echo "✅ كلمة المرور المشفرة صحيحة\n";
        }
    }
    
    if (!$verified && !empty($pass_data['password'])) {
        $verified = hash_equals($pass_data['password'], $test_password);
        if ($verified) {
            echo "✅ كلمة المرور النصية صحيحة\n";
        }
    }
    
    if (!$verified) {
        echo "❌ فشل في التحقق من كلمة المرور!\n";
    }
    
    echo "\n🎯 النتيجة النهائية:\n";
    if ($verified && $admin_user['is_active']) {
        echo "✅ المستخدم admin جاهز للاستخدام بكلمة المرور 1234\n";
        echo "📱 يمكن الآن تسجيل الدخول في تطبيق Flutter\n";
        echo "\n🔗 للاختبار:\n";
        echo "   • تطبيق Flutter: username=admin, password=1234\n";
        echo "   • اختبار API: http://hotelmarina.com/MARINA_HOTEL_PORTABLE/test_admin_login.php\n";
        echo "   • اختبار HTML: test_api_login.html\n";
    } else {
        echo "❌ هناك مشكلة في إعداد المستخدم\n";
    }
    
} else {
    echo "\n❌ فشل في التحقق من المستخدم admin\n";
}

$conn->close();

echo "\n🔧 انتهى script إعادة تعيين المستخدم Admin\n";
?>