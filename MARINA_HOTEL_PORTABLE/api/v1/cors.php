<?php
function handle_cors(array $CONFIG) {
    $allowed = $CONFIG['cors_allowed_origins'] ?? [];
    $origin = $_SERVER['HTTP_ORIGIN'] ?? '';
    
    // تسجيل طلبات CORS للتشخيص
    error_log("CORS Request - Origin: $origin, Method: {$_SERVER['REQUEST_METHOD']}");

    // السماح بالمصدر المحدد
    if ($origin && in_array($origin, $allowed, true)) {
        header('Access-Control-Allow-Origin: ' . $origin);
        header('Vary: Origin');
        error_log("CORS: Origin '$origin' allowed");
    } else if (empty($origin)) {
        // السماح بالطلبات المحلية (بدون Origin header)
        header('Access-Control-Allow-Origin: *');
        error_log("CORS: No origin header, allowing all");
    } else {
        error_log("CORS: Origin '$origin' not in allowed list: " . implode(', ', $allowed));
    }
    
    // رؤوس CORS المطلوبة
    header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH');
    header('Access-Control-Allow-Headers: Content-Type, Authorization, Accept, X-Requested-With, Origin');
    header('Access-Control-Allow-Credentials: true');
    header('Access-Control-Max-Age: 86400');

    // الرد على طلبات OPTIONS
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        echo json_encode([
            'success' => true, 
            'message' => 'CORS preflight successful',
            'timestamp' => time(),
            'origin' => $origin
        ]);
        exit;
    }
}
