<?php
/**
 * صفحة اختبار تسجيل الدخول للمستخدم admin
 * يتحقق من صحة بيانات تسجيل الدخول مباشرة من قاعدة البيانات
 */

require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/db.php';

echo "<!DOCTYPE html>\n";
echo "<html lang='ar' dir='rtl'>\n";
echo "<head>\n";
echo "    <meta charset='UTF-8'>\n";
echo "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n";
echo "    <title>اختبار تسجيل الدخول - Admin</title>\n";
echo "    <style>\n";
echo "        body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; background: #f5f5f5; }\n";
echo "        .container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n";
echo "        h1 { color: #333; text-align: center; border-bottom: 2px solid #007bff; padding-bottom: 10px; }\n";
echo "        .test-section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }\n";
echo "        .success { background: #d4edda; border-color: #c3e6cb; color: #155724; }\n";
echo "        .error { background: #f8d7da; border-color: #f5c6cb; color: #721c24; }\n";
echo "        .info { background: #d1ecf1; border-color: #bee5eb; color: #0c5460; }\n";
echo "        .code { background: #f8f9fa; padding: 10px; border-left: 3px solid #007bff; font-family: monospace; margin: 10px 0; }\n";
echo "        table { width: 100%; border-collapse: collapse; margin: 10px 0; }\n";
echo "        th, td { padding: 8px 12px; text-align: right; border: 1px solid #ddd; }\n";
echo "        th { background: #f8f9fa; font-weight: bold; }\n";
echo "    </style>\n";
echo "</head>\n";
echo "<body>\n";

echo "<div class='container'>\n";
echo "<h1>🔐 اختبار تسجيل الدخول - المستخدم Admin</h1>\n";

// فحص الاتصال بقاعدة البيانات
echo "<div class='test-section info'>\n";
echo "<h3>📊 فحص الاتصال بقاعدة البيانات</h3>\n";

if (!$conn) {
    echo "<div class='error'>❌ فشل الاتصال بقاعدة البيانات: " . mysqli_connect_error() . "</div>\n";
    echo "</div></div></body></html>";
    exit;
}

echo "<div class='success'>✅ تم الاتصال بقاعدة البيانات بنجاح</div>\n";
echo "<div class='code'>Host: " . DB_HOST . "<br>Database: " . DB_NAME . "<br>User: " . DB_USER . "</div>\n";
echo "</div>\n";

// البحث عن المستخدم admin
echo "<div class='test-section info'>\n";
echo "<h3>👤 البحث عن المستخدم Admin</h3>\n";

$stmt = $conn->prepare("SELECT user_id, username, full_name, user_type, is_active, password, password_hash, last_login FROM users WHERE username = ? LIMIT 1");
$stmt->bind_param('s', $username);
$username = 'admin';
$stmt->execute();
$user = $stmt->get_result()->fetch_assoc();
$stmt->close();

if (!$user) {
    echo "<div class='error'>❌ المستخدم 'admin' غير موجود في قاعدة البيانات</div>\n";
} else {
    echo "<div class='success'>✅ تم العثور على المستخدم 'admin'</div>\n";
    
    echo "<table>\n";
    echo "<tr><th>الحقل</th><th>القيمة</th></tr>\n";
    echo "<tr><td>معرف المستخدم</td><td>{$user['user_id']}</td></tr>\n";
    echo "<tr><td>اسم المستخدم</td><td>{$user['username']}</td></tr>\n";
    echo "<tr><td>الاسم الكامل</td><td>{$user['full_name']}</td></tr>\n";
    echo "<tr><td>نوع المستخدم</td><td>{$user['user_type']}</td></tr>\n";
    echo "<tr><td>نشط</td><td>" . ($user['is_active'] ? 'نعم' : 'لا') . "</td></tr>\n";
    echo "<tr><td>كلمة المرور (نص خام)</td><td>" . (!empty($user['password']) ? 'موجودة' : 'غير موجودة') . "</td></tr>\n";
    echo "<tr><td>كلمة المرور (مشفرة)</td><td>" . (!empty($user['password_hash']) ? 'موجودة' : 'غير موجودة') . "</td></tr>\n";
    echo "<tr><td>آخر تسجيل دخول</td><td>" . ($user['last_login'] ?? 'لم يسجل دخول من قبل') . "</td></tr>\n";
    echo "</table>\n";
}
echo "</div>\n";

// اختبار كلمة المرور
if ($user) {
    echo "<div class='test-section info'>\n";
    echo "<h3>🔑 اختبار كلمة المرور</h3>\n";
    
    $test_password = '1234';
    $verified = false;
    $method_used = '';
    
    // اختبار password_hash أولاً
    if (!empty($user['password_hash'])) {
        $verified = password_verify($test_password, $user['password_hash']);
        $method_used = 'password_hash (recommended)';
        
        if ($verified) {
            echo "<div class='success'>✅ كلمة المرور صحيحة باستخدام $method_used</div>\n";
        } else {
            echo "<div class='error'>❌ كلمة المرور خطأ باستخدام $method_used</div>\n";
        }
    }
    
    // اختبار plaintext password كبديل
    if (!$verified && !empty($user['password'])) {
        $verified = hash_equals($user['password'], $test_password);
        $method_used = 'plaintext password (legacy)';
        
        if ($verified) {
            echo "<div class='success'>✅ كلمة المرور صحيحة باستخدام $method_used</div>\n";
        } else {
            echo "<div class='error'>❌ كلمة المرور خطأ باستخدام $method_used</div>\n";
        }
    }
    
    if (!$verified) {
        echo "<div class='error'>❌ فشل في التحقق من كلمة المرور بجميع الطرق</div>\n";
        echo "<div class='code'>";
        echo "كلمة المرور المدخلة: '$test_password'<br>";
        if (!empty($user['password'])) {
            echo "كلمة المرور النصية في قاعدة البيانات: '" . htmlspecialchars($user['password']) . "'<br>";
        }
        if (!empty($user['password_hash'])) {
            echo "كلمة المرور المشفرة: " . htmlspecialchars($user['password_hash']) . "<br>";
            echo "نتيجة password_verify: " . (password_verify($test_password, $user['password_hash']) ? 'true' : 'false') . "<br>";
        }
        echo "</div>";
    }
    
    echo "</div>\n";
    
    // اختبار حالة المستخدم
    echo "<div class='test-section info'>\n";
    echo "<h3>🛡️ اختبار حالة المستخدم</h3>\n";
    
    if ((int)$user['is_active'] !== 1) {
        echo "<div class='error'>❌ المستخدم غير مفعل (is_active = {$user['is_active']})</div>\n";
    } else {
        echo "<div class='success'>✅ المستخدم مفعل ويمكنه تسجيل الدخول</div>\n";
    }
    echo "</div>\n";
    
    // اختبار صلاحيات المستخدم
    echo "<div class='test-section info'>\n";
    echo "<h3>🔐 صلاحيات المستخدم</h3>\n";
    
    $perms = [];
    $ps = $conn->prepare("SELECT p.permission_code, p.permission_name FROM user_permissions up JOIN permissions p ON p.permission_id = up.permission_id WHERE up.user_id = ?");
    if ($ps) {
        $ps->bind_param('i', $user['user_id']);
        $ps->execute();
        $res = $ps->get_result();
        while ($row = $res->fetch_assoc()) {
            $perms[] = $row;
        }
        $ps->close();
        
        if (!empty($perms)) {
            echo "<div class='success'>✅ المستخدم لديه " . count($perms) . " صلاحية</div>\n";
            echo "<table>\n";
            echo "<tr><th>كود الصلاحية</th><th>اسم الصلاحية</th></tr>\n";
            foreach ($perms as $perm) {
                echo "<tr><td>{$perm['permission_code']}</td><td>{$perm['permission_name']}</td></tr>\n";
            }
            echo "</table>\n";
        } else {
            echo "<div class='error'>⚠️ المستخدم لا يملك صلاحيات محددة</div>\n";
        }
    } else {
        echo "<div class='error'>❌ خطأ في الاستعلام عن الصلاحيات</div>\n";
    }
    echo "</div>\n";
}

// اختبار API endpoint
echo "<div class='test-section info'>\n";
echo "<h3>🌐 اختبار API Endpoint</h3>\n";

$api_url = (isset($_SERVER['HTTPS']) ? 'https' : 'http') . '://' . $_SERVER['HTTP_HOST'] . dirname($_SERVER['REQUEST_URI']) . '/api/v1/auth/login.php';

echo "<div class='code'>API URL: <a href='$api_url' target='_blank'>$api_url</a></div>\n";

$test_data = json_encode(['username' => 'admin', 'password' => '1234']);

echo "<div class='code'>";
echo "curl -X POST '$api_url' \\<br>";
echo "&nbsp;&nbsp;-H 'Content-Type: application/json' \\<br>";
echo "&nbsp;&nbsp;-d '$test_data'";
echo "</div>\n";

// اختبار مباشر للـ API
echo "<h4>🔄 اختبار مباشر:</h4>\n";

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $api_url);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $test_data);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 10);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
curl_close($ch);

if ($curl_error) {
    echo "<div class='error'>❌ خطأ في CURL: $curl_error</div>\n";
} else {
    echo "<div class='code'>";
    echo "HTTP Status: $http_code<br>";
    echo "Response: " . htmlspecialchars($response);
    echo "</div>\n";
    
    $api_data = json_decode($response, true);
    if (json_last_error() === JSON_ERROR_NONE) {
        if ($api_data['success']) {
            echo "<div class='success'>✅ API تسجيل الدخول نجح!</div>\n";
            if (isset($api_data['data']['token'])) {
                echo "<div class='code'>Token: " . substr($api_data['data']['token'], 0, 50) . "...</div>\n";
            }
        } else {
            echo "<div class='error'>❌ API تسجيل الدخول فشل: " . ($api_data['data']['error'] ?? 'Unknown error') . "</div>\n";
        }
    } else {
        echo "<div class='error'>❌ استجابة API غير صالحة (ليس JSON)</div>\n";
    }
}

echo "</div>\n";

// الخلاصة النهائية
echo "<div class='test-section " . ($user && $verified && (int)$user['is_active'] === 1 ? 'success' : 'error') . "'>\n";
echo "<h3>📋 الخلاصة النهائية</h3>\n";

if (!$user) {
    echo "❌ المستخدم admin غير موجود في قاعدة البيانات<br>";
    echo "🔧 حل مقترح: تشغيل script إنشاء المستخدمين أو استيراد قاعدة البيانات مرة أخرى<br>";
} else if (!$verified) {
    echo "❌ كلمة المرور '1234' غير صحيحة للمستخدم admin<br>";
    echo "🔧 حل مقترح: تحديث كلمة المرور في قاعدة البيانات<br>";
} else if ((int)$user['is_active'] !== 1) {
    echo "❌ المستخدم admin غير مفعل<br>";
    echo "🔧 حل مقترح: تحديث حقل is_active إلى 1<br>";
} else {
    echo "✅ جميع الاختبارات نجحت! المستخدم admin/1234 جاهز للاستخدام<br>";
    echo "📱 يمكن الآن تسجيل الدخول في تطبيق Flutter باستخدام admin/1234<br>";
}

echo "</div>\n";

echo "</div>\n";
echo "</body></html>";

$conn->close();
?>