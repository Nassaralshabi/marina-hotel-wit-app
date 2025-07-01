<?php
session_start();
require_once '../../includes/db.php';
require_once '../../includes/functions.php';

// التحقق من تسجيل الدخول
if (!isset($_SESSION['user_id'])) {
    header('Location: ../../login.php');
    exit();
}

// إنشاء جدول الإشعارات إذا لم يكن موجوداً
$create_table_query = "
CREATE TABLE IF NOT EXISTS shift_notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_user_id INT NOT NULL,
    to_user_id INT DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    is_read TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE
)";

$conn->query($create_table_query);

// معالجة إضافة إشعار جديد
if ($_POST && isset($_POST['action']) && $_POST['action'] === 'add_notification') {
    $title = trim($_POST['title']);
    $message = trim($_POST['message']);
    $priority = $_POST['priority'];
    $to_user_id = $_POST['to_user_id'] === 'all' ? NULL : (int)$_POST['to_user_id'];
    
    if (!empty($title) && !empty($message)) {
        $stmt = $conn->prepare("INSERT INTO shift_notifications (from_user_id, to_user_id, title, message, priority) VALUES (?, ?, ?, ?, ?)");
        $stmt->bind_param("iisss", $_SESSION['user_id'], $to_user_id, $title, $message, $priority);
        
        if ($stmt->execute()) {
            $success_message = "تم إرسال الإشعار بنجاح";
        } else {
            $error_message = "حدث خطأ أثناء إرسال الإشعار";
        }
        $stmt->close();
    }
}

// معالجة تحديث حالة القراءة
if ($_POST && isset($_POST['action']) && $_POST['action'] === 'mark_read') {
    $notification_id = (int)$_POST['notification_id'];
    $stmt = $conn->prepare("UPDATE shift_notifications SET is_read = 1, read_at = NOW() WHERE id = ? AND (to_user_id = ? OR to_user_id IS NULL)");
    $stmt->bind_param("ii", $notification_id, $_SESSION['user_id']);
    $stmt->execute();
    $stmt->close();
}

// جلب المستخدمين للقائمة المنسدلة
$users_query = "SELECT user_id, full_name, user_type FROM users WHERE user_id != ? ORDER BY full_name";
$users_stmt = $conn->prepare($users_query);
$users_stmt->bind_param("i", $_SESSION['user_id']);
$users_stmt->execute();
$users_result = $users_stmt->get_result();
$users = $users_result->fetch_all(MYSQLI_ASSOC);
$users_stmt->close();

// جلب الإشعارات
$notifications_query = "
    SELECT 
        sn.*,
        u_from.full_name as from_user_name,
        u_to.full_name as to_user_name
    FROM shift_notifications sn
    LEFT JOIN users u_from ON sn.from_user_id = u_from.id
    LEFT JOIN users u_to ON sn.to_user_id = u_to.id
    WHERE sn.to_user_id = ? OR sn.to_user_id IS NULL
    ORDER BY sn.created_at DESC
    LIMIT 50
";

$notifications_stmt = $conn->prepare($notifications_query);
$notifications_stmt->bind_param("i", $_SESSION['user_id']);
$notifications_stmt->execute();
$notifications_result = $notifications_stmt->get_result();
$notifications = $notifications_result->fetch_all(MYSQLI_ASSOC);
$notifications_stmt->close();

// عدد الإشعارات غير المقروءة
$unread_count_query = "SELECT COUNT(*) as count FROM shift_notifications WHERE (to_user_id = ? OR to_user_id IS NULL) AND is_read = 0";
$unread_stmt = $conn->prepare($unread_count_query);
$unread_stmt->bind_param("i", $_SESSION['user_id']);
$unread_stmt->execute();
$unread_result = $unread_stmt->get_result();
$unread_count = $unread_result->fetch_assoc()['count'];
$unread_stmt->close();
?>

<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>إشعارات النوبات - فندق مارينا بلازا</title>
    <link href="../../assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="../../assets/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: 'Tajawal', sans-serif;
            min-height: 100vh;
        }

        .main-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            margin: 20px auto;
            max-width: 1200px;
            overflow: hidden;
        }

        .header-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
            position: relative;
        }

        .header-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
        }

        .header-section h1 {
            position: relative;
            z-index: 1;
            margin: 0;
            font-size: 2.5rem;
            font-weight: 700;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .stats-bar {
            background: rgba(255, 255, 255, 0.2);
            padding: 15px;
            margin-top: 20px;
            border-radius: 10px;
            position: relative;
            z-index: 1;
        }

        .notification-ticker {
            background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1, #96ceb4, #feca57);
            background-size: 300% 300%;
            animation: gradientShift 8s ease infinite;
            color: white;
            padding: 15px;
            margin: 20px 0;
            border-radius: 10px;
            position: relative;
            overflow: hidden;
        }

        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        .ticker-content {
            white-space: nowrap;
            animation: scroll 20s linear infinite;
            font-weight: 600;
            font-size: 1.1rem;
        }

        @keyframes scroll {
            0% { transform: translateX(100%); }
            100% { transform: translateX(-100%); }
        }

        .content-section {
            padding: 30px;
        }

        .notification-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
            overflow: hidden;
            transition: all 0.3s ease;
            border-left: 5px solid #ddd;
        }

        .notification-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }

        .notification-card.unread {
            border-left-color: #ff6b6b;
            background: linear-gradient(135deg, #fff 0%, #fff5f5 100%);
        }

        .notification-card.priority-high {
            border-left-color: #ff6b6b;
        }

        .notification-card.priority-urgent {
            border-left-color: #dc3545;
            animation: pulse 2s infinite;
        }

        .notification-card.priority-medium {
            border-left-color: #feca57;
        }

        .notification-card.priority-low {
            border-left-color: #4ecdc4;
        }

        @keyframes pulse {
            0% { box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1); }
            50% { box-shadow: 0 5px 25px rgba(220, 53, 69, 0.3); }
            100% { box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1); }
        }

        .notification-header {
            padding: 20px;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .notification-body {
            padding: 20px;
        }

        .priority-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .priority-low { background: #4ecdc4; color: white; }
        .priority-medium { background: #feca57; color: white; }
        .priority-high { background: #ff6b6b; color: white; }
        .priority-urgent { background: #dc3545; color: white; animation: blink 1s infinite; }

        @keyframes blink {
            0%, 50% { opacity: 1; }
            51%, 100% { opacity: 0.7; }
        }

        .form-section {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
        }

        .btn-send {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .btn-send:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }

        .btn-mark-read {
            background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
            border: none;
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9rem;
            transition: all 0.3s ease;
        }

        .btn-mark-read:hover {
            transform: translateY(-1px);
            box-shadow: 0 3px 10px rgba(78, 205, 196, 0.4);
            color: white;
        }

        .time-ago {
            color: #6c757d;
            font-size: 0.9rem;
        }

        .alert {
            border-radius: 15px;
            border: none;
            padding: 20px;
            margin-bottom: 20px;
        }

        .alert-success {
            background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
            color: white;
        }

        .alert-danger {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
            color: white;
        }

        .form-control, .form-select {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }

        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }

        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }

        @media (max-width: 768px) {
            .main-container {
                margin: 10px;
                border-radius: 15px;
            }

            .header-section {
                padding: 20px;
            }

            .header-section h1 {
                font-size: 2rem;
            }

            .content-section {
                padding: 20px;
            }

            .notification-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <div class="main-container">
        <!-- رأس الصفحة -->
        <div class="header-section">
            <h1><i class="fas fa-bell me-3"></i>إشعارات النوبات</h1>
            <div class="stats-bar">
                <div class="row text-center">
                    <div class="col-md-4">
                        <h4><?php echo $unread_count; ?></h4>
                        <small>إشعارات غير مقروءة</small>
                    </div>
                    <div class="col-md-4">
                        <h4><?php echo count($notifications); ?></h4>
                        <small>إجمالي الإشعارات</small>
                    </div>
                    <div class="col-md-4">
                        <h4><?php echo $_SESSION['full_name']; ?></h4>
                        <small>المستخدم الحالي</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- شريط الإشعارات المتحرك -->
        <?php if ($unread_count > 0): ?>
        <div class="notification-ticker">
            <div class="ticker-content">
                <i class="fas fa-exclamation-triangle me-2"></i>
                لديك <?php echo $unread_count; ?> إشعار<?php echo $unread_count > 1 ? 'ات' : ''; ?> غير مقروء<?php echo $unread_count > 1 ? 'ة' : ''; ?> من زملائك في النوبات الأخرى
                <i class="fas fa-bell ms-3 me-3"></i>
                تحقق من الإشعارات أدناه للاطلاع على آخر التحديثات والملاحظات المهمة
            </div>
        </div>
        <?php endif; ?>

        <div class="content-section">
            <!-- رسائل النجاح والخطأ -->
            <?php if (isset($success_message)): ?>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle me-2"></i><?php echo $success_message; ?>
                </div>
            <?php endif; ?>

            <?php if (isset($error_message)): ?>
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle me-2"></i><?php echo $error_message; ?>
                </div>
            <?php endif; ?>

            <!-- نموذج إضافة إشعار جديد -->
            <div class="form-section">
                <h3 class="mb-4"><i class="fas fa-plus-circle me-2"></i>إرسال إشعار جديد</h3>
                <form method="POST">
                    <input type="hidden" name="action" value="add_notification">
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="to_user_id" class="form-label">إرسال إلى</label>
                            <select name="to_user_id" id="to_user_id" class="form-select" required>
                                <option value="all">جميع الموظفين</option>
                                <?php foreach ($users as $user): ?>
                                    <option value="<?php echo $user['id']; ?>">
                                        <?php echo htmlspecialchars($user['full_name']); ?> 
                                        (<?php echo $user['user_type'] === 'admin' ? 'مدير' : 'موظف'; ?>)
                                    </option>
                                <?php endforeach; ?>
                            </select>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="priority" class="form-label">الأولوية</label>
                            <select name="priority" id="priority" class="form-select" required>
                                <option value="low">منخفضة</option>
                                <option value="medium" selected>متوسطة</option>
                                <option value="high">عالية</option>
                                <option value="urgent">عاجلة</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="title" class="form-label">عنوان الإشعار</label>
                        <input type="text" name="title" id="title" class="form-control" 
                               placeholder="مثال: تسليم النوبة - ملاحظات مهمة" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="message" class="form-label">محتوى الإشعار</label>
                        <textarea name="message" id="message" class="form-control" rows="4" 
                                  placeholder="اكتب تفاصيل الإشعار هنا..." required></textarea>
                    </div>
                    
                    <button type="submit" class="btn btn-send">
                        <i class="fas fa-paper-plane me-2"></i>إرسال الإشعار
                    </button>
                </form>
            </div>

            <!-- قائمة الإشعارات -->
            <h3 class="mb-4"><i class="fas fa-list me-2"></i>الإشعارات الواردة</h3>
            
            <?php if (empty($notifications)): ?>
                <div class="empty-state">
                    <i class="fas fa-bell-slash"></i>
                    <h4>لا توجد إشعارات</h4>
                    <p>لم يتم استلام أي إشعارات بعد</p>
                </div>
            <?php else: ?>
                <?php foreach ($notifications as $notification): ?>
                    <div class="notification-card <?php echo $notification['is_read'] ? '' : 'unread'; ?> priority-<?php echo $notification['priority']; ?>">
                        <div class="notification-header">
                            <div>
                                <h5 class="mb-1"><?php echo htmlspecialchars($notification['title']); ?></h5>
                                <small class="text-muted">
                                    من: <?php echo htmlspecialchars($notification['from_user_name']); ?>
                                    <?php if ($notification['to_user_name']): ?>
                                        | إلى: <?php echo htmlspecialchars($notification['to_user_name']); ?>
                                    <?php else: ?>
                                        | إلى: جميع الموظفين
                                    <?php endif; ?>
                                </small>
                            </div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="priority-badge priority-<?php echo $notification['priority']; ?>">
                                    <?php 
                                    $priority_labels = [
                                        'low' => 'منخفضة',
                                        'medium' => 'متوسطة', 
                                        'high' => 'عالية',
                                        'urgent' => 'عاجلة'
                                    ];
                                    echo $priority_labels[$notification['priority']];
                                    ?>
                                </span>
                                <?php if (!$notification['is_read']): ?>
                                    <form method="POST" style="display: inline;">
                                        <input type="hidden" name="action" value="mark_read">
                                        <input type="hidden" name="notification_id" value="<?php echo $notification['id']; ?>">
                                        <button type="submit" class="btn btn-mark-read btn-sm">
                                            <i class="fas fa-check me-1"></i>تم القراءة
                                        </button>
                                    </form>
                                <?php endif; ?>
                            </div>
                        </div>
                        <div class="notification-body">
                            <p class="mb-2"><?php echo nl2br(htmlspecialchars($notification['message'])); ?></p>
                            <small class="time-ago">
                                <i class="fas fa-clock me-1"></i>
                                <?php echo date('Y-m-d H:i', strtotime($notification['created_at'])); ?>
                                <?php if ($notification['is_read'] && $notification['read_at']): ?>
                                    | تم القراءة في: <?php echo date('Y-m-d H:i', strtotime($notification['read_at'])); ?>
                                <?php endif; ?>
                            </small>
                        </div>
                    </div>
                <?php endforeach; ?>
            <?php endif; ?>

            <!-- أزرار التنقل -->
            <div class="text-center mt-4">
                <a href="../dashboard.php" class="btn btn-secondary">
                    <i class="fas fa-arrow-right me-2"></i>العودة للوحة التحكم
                </a>
            </div>
        </div>
    </div>

    <script src="../../assets/js/vendor/bootstrap.bundle.min.js"></script>
    <script>
        // تحديث الصفحة كل 30 ثانية للحصول على إشعارات جديدة
        setInterval(function() {
            // تحديث عدد الإشعارات غير المقروءة فقط
            fetch('check_notifications.php')
                .then(response => response.json())
                .then(data => {
                    if (data.unread_count > <?php echo $unread_count; ?>) {
                        location.reload();
                    }
                })
                .catch(error => console.log('Error checking notifications:', error));
        }, 30000);

        // تأثيرات بصرية للنموذج
        document.getElementById('priority').addEventListener('change', function() {
            const priority = this.value;
            const form = this.closest('.form-section');
            
            // إزالة الفئات السابقة
            form.classList.remove('priority-low', 'priority-medium', 'priority-high', 'priority-urgent');
            
            // إضافة الفئة الجديدة
            form.classList.add('priority-' + priority);
        });

        // تأكيد الإرسال للإشعارات العاجلة
        document.querySelector('form').addEventListener('submit', function(e) {
            const priority = document.getElementById('priority').value;
            if (priority === 'urgent') {
                if (!confirm('هل أنت متأكد من إرسال إشعار عاجل؟ سيتم تنبيه جميع المستلمين فوراً.')) {
                    e.preventDefault();
                }
            }
        });
    </script>
</body>
</html>

