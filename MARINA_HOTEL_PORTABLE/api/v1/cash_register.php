<?php
require_once __DIR__ . '/bootstrap.php';

handle_entity([
    'table' => 'cash_register',
    'pk' => 'id',
    'select' => '*',
    'filter_columns' => ['date','status','notes'],
    'writable' => [
        'date' => 's',
        'opening_balance' => 'd',
        'total_income' => 'd',
        'total_expense' => 'd',
        'closing_balance' => 'd',
        'status' => 's',
        'notes' => 's'
    ],
    'write_perm' => ['manage_cash']
]);
