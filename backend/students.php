<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') { exit; }

require 'config.php';
$method = $_SERVER['REQUEST_METHOD'];
$in = json_decode(file_get_contents('php://input'), true) ?? [];
$id = isset($_GET['id']) ? (int)$_GET['id'] : 0;

function out($data, $code = 200) { http_response_code($code); echo json_encode($data); exit; }

function validate($in) {
    $name = trim($in['full_name'] ?? '');
    $email = trim($in['email'] ?? '');
    $course = trim($in['course'] ?? '');
    $year = (int)($in['year_level'] ?? 0);
    if (strlen($name) < 2) out(['success'=>false,'message'=>'Full name must be at least 2 characters'], 422);
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) out(['success'=>false,'message'=>'Invalid email'], 422);
    if ($course === '') out(['success'=>false,'message'=>'Course is required'], 422);
    if ($year < 1 || $year > 6) out(['success'=>false,'message'=>'Year level must be 1-6'], 422);
    return [$name, $email, $course, $year];
}

$cols = "id, full_name, email, course, year_level";
try {
    switch ($method) {
        case 'GET':
            if ($id) {
                $s = $pdo->prepare("SELECT $cols FROM students WHERE id=?");
                $s->execute([$id]); $row = $s->fetch();
                $row ? out($row) : out(['success'=>false,'message'=>'Not found'], 404);
            }
            out($pdo->query("SELECT $cols FROM students ORDER BY id DESC")->fetchAll());
        case 'POST':
            [$n,$e,$c,$y] = validate($in);
            $pdo->prepare("INSERT INTO students(full_name,email,course,year_level) VALUES(?,?,?,?)")->execute([$n,$e,$c,$y]);
            out(['success'=>true,'message'=>'Created','id'=>(int)$pdo->lastInsertId()], 201);
        case 'PUT':
            if (!$id) out(['success'=>false,'message'=>'id required'], 400);
            [$n,$e,$c,$y] = validate($in);
            $pdo->prepare("UPDATE students SET full_name=?,email=?,course=?,year_level=? WHERE id=?")->execute([$n,$e,$c,$y,$id]);
            out(['success'=>true,'message'=>'Updated']);
        case 'DELETE':
            if (!$id) out(['success'=>false,'message'=>'id required'], 400);
            $pdo->prepare("DELETE FROM students WHERE id=?")->execute([$id]);
            out(['success'=>true,'message'=>'Deleted']);
        default:
            out(['success'=>false,'message'=>'Method not allowed'], 405);
    }
} catch (Exception $e) {
    out(['success'=>false,'message'=>'Server error'], 500);
}
