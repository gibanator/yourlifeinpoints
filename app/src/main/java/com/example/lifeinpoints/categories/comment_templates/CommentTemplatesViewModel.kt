package com.example.lifeinpoints.categories.comment_templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryRepositoryNew
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateEntity
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentTemplatesViewModel @Inject constructor(
    private val categoriesRepository: CategoryRepositoryNew,
    private val templatesRepository: CommentTemplateRepository      // wrapper over DAO
) : ViewModel() {

    private val _categoryName = MutableStateFlow<String?>(null)
    private val _categoryNameKey = MutableStateFlow<String?>(null)
    private val _isSystem = MutableStateFlow(false)
    val categoryName: StateFlow<String?> = _categoryName.asStateFlow()
    val categoryNameKey: StateFlow<String?> = _categoryNameKey.asStateFlow()
    val isSystem: StateFlow<Boolean> = _isSystem.asStateFlow()
    private var categoryJob: Job? = null

    fun observeTemplates(categoryId: Int): Flow<List<CommentTemplateEntity>> {
        return templatesRepository.observeByCategory(categoryId)
    }

    fun loadCategoryName(categoryId: Int) {
        categoryJob?.cancel()
        categoryJob = viewModelScope.launch {
            categoriesRepository.observeAll()
                .map { categories -> categories.firstOrNull { it.localId == categoryId } }
                .distinctUntilChanged()
                .collect { category ->
                    _categoryName.value = category?.name
                    _categoryNameKey.value = null
                    _isSystem.value = category?.isSystem ?: false
                }
        }
    }

    fun saveSlot(categoryId: Int, position: Int, text: String) {
        require(position in 0..4)
        viewModelScope.launch {
            templatesRepository.upsertTemplate(
                categoryId = categoryId,
                position = position,
                text = text
            )
        }
    }

    fun clearSlot(categoryId: Int, position: Int) {
        require(position in 0..4)
        viewModelScope.launch {
            templatesRepository.clearSlot(categoryId, position)
        }
    }
}
