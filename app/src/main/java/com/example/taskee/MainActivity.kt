package com.example.taskee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskee.data.TaskeeRepository
import com.example.taskee.ui.TaskeeHomeScreen
import com.example.taskee.ui.TaskeeViewModel
import com.example.taskee.ui.theme.TaskeeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TaskeeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = TaskeeRepository(applicationContext)
                return TaskeeViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskeeTheme {
                TaskeeHomeScreen(viewModel = viewModel)
            }
        }
    }
}
