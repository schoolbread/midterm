package com.example.midterm.ui

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.midterm.data.Student

// ---------- Quote banner (External API: ZenQuotes) ----------
@Composable
fun QuoteBanner(vm: StudentViewModel) {
    Card(
        Modifier.fillMaxWidth().padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Daily Inspiration", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(6.dp))
            val q = vm.quote
            when {
                q != null -> {
                    Text("\u201C${q.q}\u201D", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
                    Text("\u2014 ${q.a}", style = MaterialTheme.typography.bodySmall)
                }
                vm.quoteError -> Text("Couldn't load quote.")
                else -> LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            TextButton(onClick = { vm.loadQuote() }) { Text("New quote") }
        }
    }
}

// ---------- READ: list ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(vm: StudentViewModel, onOpen: (Int) -> Unit, onAdd: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("College Students") }, actions = {
                IconButton(onClick = { vm.loadStudents() }) { Icon(Icons.Default.Refresh, "Refresh") }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, "Add student") }
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            item { QuoteBanner(vm) }
            if (vm.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            vm.error?.let { e -> item { Text(e, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) } }
            if (!vm.loading && vm.students.isEmpty() && vm.error == null)
                item { Text("No students yet. Tap + to add one.", modifier = Modifier.padding(16.dp)) }
            items(vm.students, key = { it.id }) { s ->
                Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onOpen(s.id) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(s.fullName, style = MaterialTheme.typography.titleMedium)
                        Text("${s.course} \u2022 Year ${s.yearLevel}", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// ---------- Detail + DELETE confirmation ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(vm: StudentViewModel, id: Int, onBack: () -> Unit, onEdit: (Int) -> Unit) {
    val s = vm.students.find { it.id == id }
    var confirm by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Student Details") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            actions = {
                if (s != null) {
                    IconButton(onClick = { onEdit(id) }) { Icon(Icons.Default.Edit, "Edit") }
                    IconButton(onClick = { confirm = true }) { Icon(Icons.Default.Delete, "Delete") }
                }
            })
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (s == null) Text("Student not found.") else {
                Text(s.fullName, style = MaterialTheme.typography.headlineSmall)
                Text("Email: ${s.email}")
                Text("Course: ${s.course}")
                Text("Year level: ${s.yearLevel}")
            }
        }
    }

    if (confirm && s != null) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Delete student?") },
            text = { Text("\"${s.fullName}\" will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = { confirm = false; vm.delete(id, onBack) }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } }
        )
    }
}

// ---------- CREATE / UPDATE form with validation ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(vm: StudentViewModel, id: Int, onDone: () -> Unit) {
    val existing = vm.students.find { it.id == id }   // null when creating
    var name by remember { mutableStateOf(existing?.fullName ?: "") }
    var email by remember { mutableStateOf(existing?.email ?: "") }
    var course by remember { mutableStateOf(existing?.course ?: "") }
    var year by remember { mutableStateOf(existing?.yearLevel?.toString() ?: "") }
    var submitted by remember { mutableStateOf(false) }

    val nameErr = if (name.trim().length < 2) "Name must be at least 2 characters" else null
    val emailErr = if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Enter a valid email" else null
    val courseErr = if (course.isBlank()) "Course is required" else null
    val yearNum = year.toIntOrNull()
    val yearErr = if (yearNum == null || yearNum !in 1..6) "Year level must be 1-6" else null

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (id == 0) "Add Student" else "Edit Student") },
            navigationIcon = { IconButton(onClick = onDone) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth(),
                isError = submitted && nameErr != null, supportingText = { if (submitted) nameErr?.let { Text(it) } })
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = submitted && emailErr != null, supportingText = { if (submitted) emailErr?.let { Text(it) } })
            OutlinedTextField(course, { course = it }, label = { Text("Course / Program") }, modifier = Modifier.fillMaxWidth(),
                isError = submitted && courseErr != null, supportingText = { if (submitted) courseErr?.let { Text(it) } })
            OutlinedTextField(year, { year = it.filter(Char::isDigit) }, label = { Text("Year level (1-6)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = submitted && yearErr != null, supportingText = { if (submitted) yearErr?.let { Text(it) } })
            vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                submitted = true
                if (nameErr == null && emailErr == null && courseErr == null && yearErr == null) {
                    vm.clearError()
                    vm.save(Student(id, name.trim(), email.trim(), course.trim(), yearNum!!), onDone)
                }
            }) { Text(if (id == 0) "Save" else "Update") }
        }
    }
}
