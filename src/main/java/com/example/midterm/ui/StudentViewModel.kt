package com.example.midterm.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.data.Student
import com.example.midterm.data.Network
import com.example.midterm.data.Quote
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    var students by mutableStateOf<List<Student>>(emptyList()); private set
    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set
    var quote by mutableStateOf<Quote?>(null); private set
    var quoteError by mutableStateOf(false); private set

    init { loadStudents(); loadQuote() }

    // READ
    fun loadStudents() = viewModelScope.launch {
        loading = true; error = null
        try { students = Network.students.getStudents() }
        catch (e: Exception) { error = "Could not load students: ${e.message}" }
        loading = false
    }

    // EXTERNAL API
    fun loadQuote() = viewModelScope.launch {
        quoteError = false
        try { quote = Network.quotes.random().firstOrNull() }
        catch (e: Exception) { quoteError = true }
    }

    // CREATE or UPDATE (id == 0 means create)
    fun save(student: Student, onDone: () -> Unit) = viewModelScope.launch {
        try {
            val r = if (student.id == 0) Network.students.create(student)
                    else Network.students.update(student.id, student)
            if (r.success) { loadStudents(); onDone() } else error = r.message
        } catch (e: Exception) { error = "Save failed: ${e.message}" }
    }

    // DELETE
    fun delete(id: Int, onDone: () -> Unit) = viewModelScope.launch {
        try {
            val r = Network.students.delete(id)
            if (r.success) { loadStudents(); onDone() } else error = r.message
        } catch (e: Exception) { error = "Delete failed: ${e.message}" }
    }

    fun clearError() { error = null }
}
