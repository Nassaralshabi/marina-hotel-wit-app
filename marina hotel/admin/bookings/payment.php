<?php
require_once '../../includes/functions.php';
include '../../includes/db.php';      // يفترض أن هذا الملف يقوم بإنشاء اتصال $conn بقاعدة البيانات
include '../../includes/auth.php';    // للمصادقة فقط

// جلب رقم الحجز من الرابط
$booking_id = intval($_GET['id'] ?? 0);
if ($booking_id <= 0) {
    die("رقم الحجز غير صالح");
}

// جلب بيانات الحجز مع سعر الغرفة
$booking_query = "
    SELECT b.booking_id, b.guest_name, b.guest_phone, b.room_number, b.checkin_date, b.checkout_date, 
           r.price AS room_price,
           b.status,
           IFNULL((SELECT SUM(p.amount) FROM payment p WHERE p.booking_id = b.booking_id), 0) AS paid_amount
    FROM bookings b
    LEFT JOIN rooms r ON b.room_number = r.room_number
    WHERE b.booking_id = ? LIMIT 1
";

$stmt = $conn->prepare($booking_query);
if (!$stmt) {
    die("خطأ في الاستعلام: " . $conn->error);
}
$stmt->bind_param("i", $booking_id);
$stmt->execute();
$booking_result = $stmt->get_result();
if ($booking_result->num_rows === 0) {
    die("الحجز غير موجود");
}
$booking = $booking_result->fetch_assoc();

// حساب عدد الليالي بين checkin_date و checkout_date
$checkin = new DateTime($booking['checkin_date']);
$checkout = new DateTime($booking['checkout_date']);
$nights = $checkout->diff($checkin)->days;
if ($nights < 1) $nights = 1;

// حساب المبلغ الإجمالي والمتبقي
$total_price = $booking['room_price'] * $nights;
$paid_amount = $booking['paid_amount'];
$remaining = max(0, $total_price - $paid_amount);

$payment_error = '';
$success_msg = '';

// معالجة تسجيل المغادرة
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['checkout'])) {
    if ($remaining == 0) {
        $conn->begin_transaction();
        try {
            // تحديث حالة الحجز إلى 'شاغرة' وتسجيل actual_checkout
            $update_booking = "UPDATE bookings SET status = 'شاغرة', actual_checkout = NOW() WHERE booking_id = ?";
            $stmt_update_booking = $conn->prepare($update_booking);
            $stmt_update_booking->bind_param("i", $booking_id);
            if (!$stmt_update_booking->execute()) {
                throw new Exception("خطأ في تحديث حالة الحجز: " . $stmt_update_booking->error);
            }

            // تحديث حالة الغرفة إلى 'شاغرة'
            $update_room = "UPDATE rooms SET status = 'شاغرة' WHERE room_number = ?";
            $stmt_update_room = $conn->prepare($update_room);
            $stmt_update_room->bind_param("s", $booking['room_number']);
            if (!$stmt_update_room->execute()) {
                throw new Exception("خطأ في تحديث حالة الغرفة: " . $stmt_update_room->error);
            }

            $conn->commit();

            header("Location: payment.php?id={$booking_id}&success=" . urlencode("تم تسجيل خروج النزيل بنجاح وتم تحرير الغرفة."));
            exit();

        } catch (Exception $e) {
            $conn->rollback();
            $payment_error = $e->getMessage();
        }
    } else {
        $payment_error = "لا يمكن تسجيل المغادرة قبل تسديد كافة المستحقات.";
    }
}

// معالجة تسجيل دفعة جديدة
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['submit_payment'])) {
    $amount = intval($_POST['amount'] ?? 0);
    $payment_date = $_POST['payment_date'] ?? date('Y-m-d H:i:s');
    $payment_method = $conn->real_escape_string($_POST['payment_method'] ?? 'نقدي');
    $notes = $conn->real_escape_string($_POST['notes'] ?? '');

    if ($amount <= 0 || $amount > $remaining) {
        $payment_error = "المبلغ يجب أن يكون بين 1 و {$remaining} ريال";
    } else {
        $insert_payment = "INSERT INTO payment (booking_id, amount, payment_date, payment_method, notes) VALUES (?, ?, ?, ?, ?)";
        $stmt_insert = $conn->prepare($insert_payment);
        if (!$stmt_insert) {
            $payment_error = "خطأ في تحضير الاستعلام: " . $conn->error;
        } else {
            $stmt_insert->bind_param("iisss", $booking_id, $amount, $payment_date, $payment_method, $notes);
            if ($stmt_insert->execute()) {
                // تحديث المبلغ المتبقي بعد الدفع الجديد
                $remaining_after_payment = $remaining - $amount;

                // إرسال رسالة واتساب للعميل
                $phone = $booking['guest_phone'];
                $message = "عزيزي {$booking['guest_name']}، تم استلام دفعة بقيمة: {$amount} ريال\nرقم الحجز: {$booking_id}\nالمبلغ المتبقي: {$remaining_after_payment} ريال\nشكراً لاختيارك فندقنا\nللاستفسار: 9677734587456";

                $wa_result = send_yemeni_whatsapp($phone, $message);

                $success_msg = "تم تسجيل الدفعة بنجاح";
                if (isset($wa_result['status']) && $wa_result['status'] === 'sent') {
                    $success_msg .= " وتم إرسال الإشعار للعميل عبر واتساب.";
                } else {
                    $success_msg .= " ولكن لم يتم إرسال الإشعار للعميل.";
                }

                header("Location: payment.php?id={$booking_id}&success=" . urlencode($success_msg));
                exit();
            } else {
                $payment_error = "خطأ في تسجيل الدفعة: " . $stmt_insert->error;
            }
        }
    }
}

// جلب سجل الدفعات السابقة
$payments_query = "SELECT * FROM payment WHERE booking_id = ? ORDER BY payment_date DESC";
$stmt_payments = $conn->prepare($payments_query);
$stmt_payments->bind_param("i", $booking_id);
$stmt_payments->execute();
$payments_result = $stmt_payments->get_result();

// تضمين الهيدر بعد انتهاء معالجة POST
include '../../includes/header.php';
?>

<style>
.payment-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
    font-family: 'Tajawal', sans-serif;
}

.payment-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 40px;
    border-radius: 20px;
    margin-bottom: 30px;
    box-shadow: 0 15px 35px rgba(0,0,0,0.1);
    text-align: center;
    position: relative;
    overflow: hidden;
}

.payment-header::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
    0%, 100% { transform: rotate(0deg); }
    50% { transform: rotate(180deg); }
}

.payment-header h1 {
    margin: 0;
    font-size: 3rem;
    font-weight: 800;
    text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
    position: relative;
    z-index: 1;
}

.payment-header .booking-id {
    font-size: 1.4rem;
    opacity: 0.95;
    margin-top: 15px;
    font-weight: 600;
    position: relative;
    z-index: 1;
}

.back-button {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    background: linear-gradient(135deg, #6c757d, #495057);
    color: white;
    padding: 15px 25px;
    border-radius: 12px;
    text-decoration: none;
    font-weight: 600;
    transition: all 0.3s ease;
    margin-bottom: 25px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}

.back-button:hover {
    background: linear-gradient(135deg, #5a6268, #343a40);
    color: white;
    text-decoration: none;
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(0,0,0,0.3);
}

.alert {
    padding: 20px 25px;
    border-radius: 15px;
    margin-bottom: 25px;
    border: none;
    font-weight: 600;
    font-size: 1.1rem;
    box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    position: relative;
    overflow: hidden;
}

.alert::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 5px;
    height: 100%;
    background: currentColor;
}

.alert-success {
    background: linear-gradient(135deg, #d4edda, #c3e6cb);
    color: #155724;
    border-left: 5px solid #28a745;
}

.alert-danger {
    background: linear-gradient(135deg, #f8d7da, #f5c6cb);
    color: #721c24;
    border-left: 5px solid #dc3545;
}

.alert i {
    font-size: 1.3rem;
    margin-left: 10px;
}

.financial-summary {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 25px;
    margin-bottom: 40px;
}

.summary-card {
    background: white;
    border-radius: 20px;
    padding: 30px;
    text-align: center;
    box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    border-top: 6px solid;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.summary-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
    transition: left 0.5s;
}

.summary-card:hover::before {
    left: 100%;
}

.summary-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 40px rgba(0,0,0,0.15);
}

.summary-card.total {
    border-top-color: #007bff;
    background: linear-gradient(135deg, #ffffff, #f8f9ff);
}

.summary-card.paid {
    border-top-color: #28a745;
    background: linear-gradient(135deg, #ffffff, #f8fff8);
}

.summary-card.remaining {
    border-top-color: #dc3545;
    background: linear-gradient(135deg, #ffffff, #fff8f8);
}

.summary-card h4 {
    margin: 0 0 15px 0;
    color: #6c757d;
    font-size: 1rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.summary-card .amount {
    font-size: 2.5rem;
    font-weight: 900;
    margin: 0;
    text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.summary-card.total .amount {
    color: #007bff;
}

.summary-card.paid .amount {
    color: #28a745;
}

.summary-card.remaining .amount {
    color: #dc3545;
}

.summary-card .icon {
    font-size: 3rem;
    opacity: 0.1;
    position: absolute;
    top: 20px;
    left: 20px;
}

.info-card {
    background: white;
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.08);
    margin-bottom: 30px;
    overflow: hidden;
}

.info-card-header {
    background: linear-gradient(135deg, #f8f9fa, #e9ecef);
    padding: 25px;
    border-bottom: 2px solid #dee2e6;
}

.info-card-header h3 {
    margin: 0;
    color: #495057;
    font-weight: 700;
    font-size: 1.3rem;
    display: flex;
    align-items: center;
    gap: 12px;
}

.info-table {
    width: 100%;
    margin: 0;
}

.info-table tr {
    border-bottom: 1px solid #f8f9fa;
    transition: background-color 0.3s ease;
}

.info-table tr:hover {
    background-color: #f8f9fa;
}

.info-table tr:last-child {
    border-bottom: none;
}

.info-table td {
    padding: 18px 25px;
    vertical-align: middle;
}

.info-table .label {
    font-weight: 700;
    color: #6c757d;
    width: 220px;
    background: #f8f9fa;
    font-size: 1rem;
}

.info-table .value {
    color: #495057;
    font-weight: 500;
    font-size: 1rem;
}

.payment-form {
    background: white;
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.08);
    padding: 40px;
    margin-bottom: 30px;
}

.payment-form h3 {
    margin-bottom: 30px;
    color: #495057;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 1.4rem;
    font-weight: 700;
}

.form-group {
    margin-bottom: 30px;
}

.form-label {
    display: block;
    font-weight: 700;
    color: #495057;
    margin-bottom: 10px;
    font-size: 1.1rem;
}

.form-control {
    width: 100%;
    padding: 15px 20px;
    border: 3px solid #e9ecef;
    border-radius: 12px;
    font-size: 1.1rem;
    transition: all 0.3s ease;
    background: #fff;
    font-weight: 500;
}

.form-control:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
    transform: translateY(-2px);
}

.btn {
    padding: 15px 35px;
    border: none;
    border-radius: 12px;
    font-weight: 700;
    font-size: 1.1rem;
    cursor: pointer;
    transition: all 0.3s ease;
    text-decoration: none;
    display: inline-block;
    text-align: center;
    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.btn-primary {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
}

.btn-primary:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-success {
    background: linear-gradient(135deg, #28a745, #20c997);
    color: white;
}

.btn-success:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(40, 167, 69, 0.4);
}

.btn-lg {
    padding: 18px 45px;
    font-size: 1.2rem;
}

.payments-history {
    background: white;
    border-radius: 20px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.08);
    overflow: hidden;
    margin-bottom: 30px;
}

.payments-table {
    width: 100%;
    margin: 0;
    border-collapse: collapse;
}

.payments-table thead {
    background: linear-gradient(135deg, #495057, #6c757d);
    color: white;
}

.payments-table th {
    padding: 20px;
    text-align: right;
    font-weight: 700;
    font-size: 1rem;
}

.payments-table td {
    padding: 18px 20px;
    border-bottom: 1px solid #f8f9fa;
    vertical-align: middle;
    font-weight: 500;
}

.payments-table tbody tr:hover {
    background: #f8f9fa;
}

.payments-table tbody tr:last-child td {
    border-bottom: none;
}

.empty-state {
    text-align: center;
    padding: 60px;
    color: #6c757d;
}

.empty-state i {
    font-size: 4rem;
    color: #dee2e6;
    margin-bottom: 20px;
}

.checkout-section {
    background: linear-gradient(135deg, #d4edda, #c3e6cb);
    border-radius: 20px;
    padding: 40px;
    text-align: center;
    margin: 30px 0;
    border: 3px solid #28a745;
    box-shadow: 0 10px 30px rgba(40, 167, 69, 0.2);
}

.checkout-message {
    background: linear-gradient(135deg, #d1ecf1, #bee5eb);
    border-radius: 20px;
    padding: 40px;
    text-align: center;
    margin: 30px 0;
    border: 3px solid #17a2b8;
    color: #0c5460;
    font-weight: 700;
    font-size: 1.2rem;
    box-shadow: 0 10px 30px rgba(23, 162, 184, 0.2);
}

.note {
    background: linear-gradient(135deg, #fff3cd, #ffeaa7);
    border-radius: 15px;
    padding: 20px 25px;
    margin-top: 25px;
    border-left: 5px solid #ffc107;
    color: #856404;
    font-size: 1rem;
    font-weight: 600;
    box-shadow: 0 5px 15px rgba(255, 193, 7, 0.2);
}

.badge {
    background: #6c757d;
    color: white;
    padding: 8px 15px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 600;
}

@media (max-width: 768px) {
    .payment-container {
        padding: 15px;
    }
    
    .payment-header h1 {
        font-size: 2.2rem;
    }
    
    .financial-summary {
        grid-template-columns: 1fr;
    }
    
    .summary-card .amount {
        font-size: 2rem;
    }
    
    .info-table .label {
        width: auto;
        display: block;
        background: none;
        font-size: 0.9rem;
        padding-bottom: 8px;
    }
    
    .info-table td {
        display: block;
        padding: 12px 20px;
    }
    
    .payments-table {
        font-size: 0.9rem;
    }
    
    .payments-table th,
    .payments-table td {
        padding: 12px 10px;
    }
}
</style>

<div class="payment-container">
    <a href="list.php" class="back-button">
        <i class="fas fa-arrow-right"></i>
        العودة لقائمة الحجوزات
    </a>

    <div class="payment-header">
        <h1><i class="fas fa-credit-card me-3"></i>إدارة المدفوعات</h1>
        <div class="booking-id">حجز رقم <?= htmlspecialchars($booking_id) ?> - <?= htmlspecialchars($booking['guest_name']) ?></div>
    </div>

    <?php if (!empty($payment_error)): ?>
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-triangle"></i>
            <?= htmlspecialchars($payment_error) ?>
        </div>
    <?php endif; ?>

    <?php if (!empty($_GET['success'])): ?>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            <?= htmlspecialchars($_GET['success']) ?>
        </div>
    <?php elseif (!empty($success_msg)): ?>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            <?= htmlspecialchars($success_msg) ?>
        </div>
    <?php endif; ?>

    <div class="financial-summary">
        <div class="summary-card total">
            <i class="fas fa-calculator icon"></i>
            <h4>المبلغ الإجمالي</h4>
            <p class="amount"><?= number_format($total_price, 0) ?></p>
            <small style="color: #6c757d; font-weight: 600;">ريال يمني</small>
        </div>
        <div class="summary-card paid">
            <i class="fas fa-check-circle icon"></i>
            <h4>المبلغ المدفوع</h4>
            <p class="amount"><?= number_format($paid_amount, 0) ?></p>
            <small style="color: #6c757d; font-weight: 600;">ريال يمني</small>
        </div>
        <div class="summary-card remaining">
            <i class="fas fa-clock icon"></i>
            <h4>المبلغ المتبقي</h4>
            <p class="amount"><?= number_format($remaining, 0) ?></p>
            <small style="color: #6c757d; font-weight: 600;">ريال يمني</small>
        </div>
    </div>

    <div class="info-card">
        <div class="info-card-header">
            <h3><i class="fas fa-info-circle"></i>تفاصيل الحجز</h3>
        </div>
        <table class="info-table">
            <tr>
                <td class="label">اسم النزيل:</td>
                <td class="value"><?= htmlspecialchars($booking['guest_name']) ?></td>
            </tr>
            <tr>
                <td class="label">رقم الهاتف:</td>
                <td class="value"><?= htmlspecialchars($booking['guest_phone']) ?></td>
            </tr>
            <tr>
                <td class="label">رقم الغرفة:</td>
                <td class="value"><strong><?= htmlspecialchars($booking['room_number']) ?></strong></td>
            </tr>
            <tr>
                <td class="label">تاريخ الوصول:</td>
                <td class="value"><?= date('d/m/Y', strtotime($booking['checkin_date'])) ?></td>
            </tr>
            <tr>
                <td class="label">تاريخ المغادرة:</td>
                <td class="value"><?= date('d/m/Y', strtotime($booking['checkout_date'])) ?></td>
            </tr>
            <tr>
                <td class="label">عدد الليالي:</td>
                <td class="value"><strong><?= $nights ?> ليلة</strong></td>
            </tr>
            <tr>
                <td class="label">سعر الليلة الواحدة:</td>
                <td class="value"><?= number_format($booking['room_price'], 0) ?> ريال</td>
            </tr>
            <tr>
                <td class="label">حالة الحجز:</td>
                <td class="value">
                    <span class="badge" style="background: <?= $booking['status'] == 'محجوزة' ? '#28a745' : '#17a2b8' ?>;">
                        <?= htmlspecialchars($booking['status']) ?>
                    </span>
                </td>
            </tr>
        </table>
    </div>

    <?php if ($remaining > 0): ?>
    <div class="payment-form">
        <h3>
            <i class="fas fa-plus-circle"></i>
            تسجيل دفعة جديدة
        </h3>
        
        <form method="post" novalidate>
            <div class="form-group">
                <label for="amount" class="form-label">
                    <i class="fas fa-money-bill-wave me-2"></i>
                    المبلغ المدفوع (ريال يمني)
                </label>
                <input type="number" id="amount" name="amount" class="form-control" 
                       min="1" max="<?= $remaining ?>" required 
                       placeholder="أدخل المبلغ المدفوع (الحد الأقصى: <?= number_format($remaining, 0) ?> ريال)">
            </div>

            <div class="form-group">
                <label for="payment_date" class="form-label">
                    <i class="fas fa-calendar-alt me-2"></i>
                    تاريخ ووقت الدفع
                </label>
                <input type="datetime-local" id="payment_date" name="payment_date" 
                       class="form-control" value="<?= date('Y-m-d\TH:i') ?>" required>
            </div>

            <div class="form-group">
                <label for="payment_method" class="form-label">
                    <i class="fas fa-credit-card me-2"></i>
                    طريقة الدفع
                </label>
                <select id="payment_method" name="payment_method" class="form-control">
                    <option value="نقدي">💵 نقدي</option>
                    <option value="تحويل">🏦 تحويل بنكي</option>
                    <option value="بطاقة ائتمان">💳 بطاقة ائتمان</option>
                    <option value="محفظة إلكترونية">📱 محفظة إلكترونية</option>
                    <option value="شيك">📄 شيك</option>
                </select>
            </div>

            <div class="form-group">
                <label for="notes" class="form-label">
                    <i class="fas fa-sticky-note me-2"></i>
                    ملاحظات إضافية (اختياري)
                </label>
                <textarea id="notes" name="notes" class="form-control" rows="4" 
                          placeholder="أضف أي ملاحظات مهمة حول هذه الدفعة..."></textarea>
            </div>

            <button type="submit" name="submit_payment" class="btn btn-primary btn-lg">
                <i class="fas fa-save me-2"></i>
                تسجيل الدفعة وإرسال إشعار للعميل
            </button>
        </form>
    </div>
    <?php endif; ?>

    <?php if ($remaining == 0 && $booking['status'] == 'محجوزة'): ?>
        <div class="checkout-section">
            <h3 style="margin-bottom: 20px; color: #155724;">
                <i class="fas fa-check-circle me-2"></i>
                تم تسديد كافة المستحقات بنجاح
            </h3>
            <p style="margin-bottom: 25px; color: #155724; font-size: 1.1rem;">
                يمكنك الآن تسجيل مغادرة النزيل وتحرير الغرفة للحجوزات الجديدة
            </p>
            <form method="post">
                <button type="submit" name="checkout" class="btn btn-success btn-lg">
                    <i class="fas fa-sign-out-alt me-2"></i>
                    تسجيل مغادرة النزيل وتحرير الغرفة
                </button>
            </form>
        </div>
    <?php elseif ($booking['status'] == 'شاغرة'): ?>
        <div class="checkout-message">
            <i class="fas fa-info-circle me-2"></i>
            تم تسجيل خروج النزيل بنجاح والغرفة متاحة الآن للحجوزات الجديدة
        </div>
    <?php endif; ?>

    <div class="payments-history">
        <div class="info-card-header">
            <h3><i class="fas fa-history"></i>سجل المدفوعات السابقة</h3>
        </div>
        
        <?php if ($payments_result->num_rows > 0): ?>
            <table class="payments-table">
                <thead>
                    <tr>
                        <th><i class="fas fa-hashtag me-1"></i>رقم الدفعة</th>
                        <th><i class="fas fa-money-bill me-1"></i>المبلغ</th>
                        <th><i class="fas fa-calendar me-1"></i>التاريخ والوقت</th>
                        <th><i class="fas fa-credit-card me-1"></i>طريقة الدفع</th>
                        <th><i class="fas fa-sticky-note me-1"></i>ملاحظات</th>
                    </tr>
                </thead>
                <tbody>
                    <?php while ($payment = $payments_result->fetch_assoc()): ?>
                        <tr>
                            <td><strong>#<?= htmlspecialchars($payment['payment_id']) ?></strong></td>
                            <td><strong style="color: #28a745;"><?= number_format($payment['amount'], 0) ?> ريال</strong></td>
                            <td><?= date('d/m/Y - H:i', strtotime($payment['payment_date'])) ?></td>
                            <td>
                                <span class="badge">
                                    <?= htmlspecialchars($payment['payment_method']) ?>
                                </span>
                            </td>
                            <td><?= htmlspecialchars($payment['notes'] ?: 'لا توجد ملاحظات') ?></td>
                        </tr>
                    <?php endwhile; ?>
                </tbody>
            </table>
        <?php else: ?>
            <div class="empty-state">
                <i class="fas fa-receipt"></i>
                <h4>لا توجد مدفوعات مسجلة</h4>
                <p>لم يتم تسجيل أي دفعات لهذا الحجز بعد</p>
            </div>
        <?php endif; ?>
    </div>

    <div class="note">
        <i class="fas fa-info-circle me-2"></i>
        <strong>ملاحظة مهمة:</strong> عند تسجيل أي دفعة جديدة سيتم إرسال إشعار تلقائي للعميل عبر واتساب يتضمن تفاصيل الدفعة والمبلغ المتبقي ومعلومات التواصل.
    </div>
</div>

<?php include '../../includes/footer.php'; ?>
