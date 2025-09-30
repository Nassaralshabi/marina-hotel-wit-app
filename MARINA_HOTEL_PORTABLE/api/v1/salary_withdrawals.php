<?php
require_once __DIR__ . '/bootstrap.php';

handle_entity([
    'table' => 'salary_withdrawals',
    'pk' => 'id',
    'select' => '*',
    'filter_columns' => ['notes','date','withdrawal_type'],
    'writable' => [
        'employee_id' => 'i',
        'amount' => 'd',
        'date' => 's',
        'notes' => 's',
        'withdrawal_type' => 's',
        'cash_transaction_id' => 'i',
        'created_by' => 'i'
    ],
    'write_perm' => ['manage_expenses']
]);
