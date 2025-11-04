package com.example.lifeinpoints.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryRepository
import com.example.lifeinpoints.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private var currentUserId: Int? = null

    init {
        println("DEBUG: CategoriesViewModel initialized")
        loadCategories()
    }

    fun loadCategories() {
        println("DEBUG: Starting to load categories")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 1. Получаем или создаем пользователя
                currentUserId = ensureUserExists()
                println("DEBUG: Current user ID: $currentUserId")

                // 2. Инициализируем категории по умолчанию
                currentUserId?.let { userId ->
                    println("DEBUG: Initializing default categories for user $userId")
                    categoryRepository.initializeDefaultCategories(userId)

                    val initialCategories = categoryRepository.getByUserId(userId)
                    println("DEBUG: Initial categories count: ${initialCategories.size}")

                    // 4. Подписываемся на обновления категорий
                    categoryRepository.observeByUserId(userId).collect { categories ->
                        println("DEBUG: Flow emitted ${categories.size} categories: ${categories.map { it.name }}")
                        val uiItems = categories.map {
                            CategoryUiItem(
                                id = it.id,
                                name = it.name
                            )
                        }
                        _uiState.update {
                            it.copy(
                                categories = uiItems,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load categories"
                    )
                }
            }
        }
    }

    suspend fun addCategory(name: String): Result<Unit> {
        println("DEBUG: Adding category: $name")
        return try {
            currentUserId?.let { userId ->
                categoryRepository.addCategory(userId, name)
            } ?: Result.failure(Exception("User not initialized"))
        } catch (e: Exception) {
            println("DEBUG: Error adding category: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun ensureUserExists(): Int {
        println("DEBUG: Ensuring user exists")
        // Ищем существующего пользователя или создаем нового
        val existingUser = userRepository.getByEmail("default@user.com")
        return if (existingUser != null) {
            println("DEBUG: Found existing user with ID: ${existingUser.id}")
            existingUser.id
        } else {
            println("DEBUG: Creating new user")
            val newUser = com.example.lifeinpoints.data.user.UserEntity(
                username = "Default User",
                email = "default@user.com"
            )
            userRepository.add(newUser)
            val createdUser = userRepository.getByEmail("default@user.com")
            println("DEBUG: Created user with ID: ${createdUser?.id}")
            createdUser?.id ?: throw Exception("User creation failed")
        }
    }
}