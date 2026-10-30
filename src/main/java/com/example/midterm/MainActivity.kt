package com.example.midterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.midterm.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AppNav() } }
    }
}

@Composable
fun AppNav(vm: StudentViewModel = viewModel()) {
    val nav = rememberNavController()
    val idArg = listOf(navArgument("id") { type = NavType.IntType })
    NavHost(nav, startDestination = "list") {
        composable("list") {
            ListScreen(vm, onOpen = { nav.navigate("detail/$it") }, onAdd = { nav.navigate("form/0") })
        }
        composable("detail/{id}", arguments = idArg) {
            DetailScreen(vm, it.arguments!!.getInt("id"),
                onBack = { nav.popBackStack() }, onEdit = { id -> nav.navigate("form/$id") })
        }
        composable("form/{id}", arguments = idArg) {
            FormScreen(vm, it.arguments!!.getInt("id"), onDone = { nav.popBackStack() })
        }
    }
}
