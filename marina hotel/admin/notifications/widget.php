<?php
// ويدجت الإشعارات للوحة التحكم
if (!isset($_SESSION['user_id'])) {
    return;
}

// جلب عدد الإشعارات غير المقروءة
$unread_count_query = "SELECT COUNT(*) as count FROM shift_notifications WHERE (to_user_id = ? OR to_user_id IS NULL) AND is_read = 0";
$unread_stmt = $conn->prepare($unread_count_query);
$unread_stmt->bind_param("i", $_SESSION['user_id']);
$unread_stmt->execute();
$unread_result = $unread_stmt->get_result();
$unread_count = $unread_result->fetch_assoc()['count'];
$unread_stmt->close();

// جلب آخر 3 إشعارات
$recent_notifications_query = "
    SELECT 
        sn.*,
        u_from.full_name as from_user_name
    FROM shift_notifications sn
    LEFT JOIN users u_from ON sn.from_user_id = u_from.id
    WHERE sn.to_user_id = ? OR sn.to_user_id IS NULL
    ORDER BY sn.created_at DESC
    LIMIT 3
";

$recent_stmt = $conn->prepare($recent_notifications_query);
$recent_stmt->bind_param("i", $_SESSION['user_id']);
$recent_stmt->execute();
$recent_result = $recent_stmt->get_result();
$recent_notifications = $recent_result->fetch_all(MYSQLI_ASSOC);
$recent_stmt->close();
?>

<div class="col-md-6 col-lg-4 mb-4">
    <div class="card h-100 notification-widget">
        <div class="card-header bg-info text-white d-flex justify-content-between align-items-center">
            <h6 class="mb-0">
                <i class="fas fa-bell me-2"></i>إشعارات النوبات
            </h6>
            <?php if ($unread_count > 0): ?>
                <span class="badge bg-danger"><?php echo $unread_count; ?></span>
            <?php endif; ?>
        </div>
        <div class="card-body p-0">
            <?php if (empty($recent_notifications)): ?>
                <div class="text-center p-4 text-muted">
                    <i class="fas fa-bell-slash fa-2x mb-2"></i>
                    <p class="mb-0">لا توجد إشعارات</p>
                </div>
            <?php else: ?>
                <div class="list-group list-group-flush">
                    <?php foreach ($recent_notifications as $notification): ?>
                        <div class="list-group-item <?php echo $notification['is_read'] ? '' : 'bg-light'; ?>">
                            <div class="d-flex justify-content-between align-items-start">
                                <div class="flex-grow-1">
                                    <h6 class="mb-1 <?php echo $notification['is_read'] ? 'text-muted' : ''; ?>">
                                        <?php echo htmlspecialchars(substr($notification['title'], 0, 30)); ?>
                                        <?php echo strlen($notification['title']) > 30 ? '...' : ''; ?>
                                    </h6>
                                    <p class="mb-1 small text-muted">
                                        <?php echo htmlspecialchars(substr($notification['message'], 0, 50)); ?>
                                        <?php echo strlen($notification['message']) > 50 ? '...' : ''; ?>
                                    </p>
                                    <small class="text-muted">
                                        من: <?php echo htmlspecialchars($notification['from_user_name']); ?>
                                        | <?php echo date('H:i', strtotime($notification['created_at'])); ?>
                                    </small>
                                </div>
                                <div class="ms-2">
                                    <?php if (!$notification['is_read']): ?>
                                        <span class="badge bg-primary">جديد</span>
                                    <?php endif; ?>
                                    <?php if ($notification['priority'] === 'urgent'): ?>
                                        <span class="badge bg-danger">عاجل</span>
                                    <?php elseif ($notification['priority'] === 'high'): ?>
                                        <span class="badge bg-warning">مهم</span>
                                    <?php endif; ?>
                                </div>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>
            <?php endif; ?>
        </div>
        <div class="card-footer text-center">
            <a href="/admin/notifications/index.php" class="btn btn-sm btn-outline-info">
                <i class="fas fa-eye me-1"></i>عرض جميع الإشعارات
            </a>
        </div>
    </div>
</div>

<style>
.notification-widget .list-group-item {
    border-left: 3px solid transparent;
    transition: all 0.3s ease;
}

.notification-widget .list-group-item:not(.bg-light) {
    border-left-color: #17a2b8;
}

.notification-widget .list-group-item.bg-light {
    border-left-color: #dc3545;
    animation: pulse-border 2s infinite;
}

@keyframes pulse-border {
    0% { border-left-color: #dc3545; }
    50% { border-left-color: #ff6b6b; }
    100% { border-left-color: #dc3545; }
}

.notification-widget .card-header .badge {
    animation: bounce 2s infinite;
}

@keyframes bounce {
    0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
    40% { transform: translateY(-5px); }
    60% { transform: translateY(-3px); }
}
</style>

<script>
// تحديث الإشعارات كل دقيقة
setInterval(function() {
    fetch('/admin/notifications/check_notifications.php')
        .then(response => response.json())
        .then(data => {
            const currentCount = <?php echo $unread_count; ?>;
            if (data.unread_count > currentCount) {
                // إعادة تحميل الويدجت أو الصفحة
                location.reload();
            }
        })
        .catch(error => console.log('Error checking notifications:', error));
}, 60000);
</script>

