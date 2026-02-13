package com.example.lifeinpoints.categories.comment_templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryRepository
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateEntity
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentTemplatesViewModel @Inject constructor(
    private val categoriesRepository: CategoryRepository,            // or whatever you use
    private val templatesRepository: CommentTemplateRepository      // wrapper over DAO
) : ViewModel() {

    private val _categoryName = MutableStateFlow<String?>(null)
    val categoryName: StateFlow<String?> = _categoryName.asStateFlow()

    fun observeTemplates(categoryId: Int): Flow<List<CommentTemplateEntity>> {
        return templatesRepository.observeByCategory(categoryId.toLong())
    }

    fun loadCategoryName(categoryId: Int) {
        viewModelScope.launch {
            _categoryName.value = categoriesRepository
                .getById(categoryId)
                ?.let { category ->
                    category.name.takeIf { it.isNotBlank() } ?: category.nameKey
                }
        }
    }

    fun saveSlot(categoryId: Int, position: Int, text: String) {
        require(position in 0..4)
        viewModelScope.launch {
            templatesRepository.upsertTemplate(
                categoryId = categoryId.toLong(),
                position = position,
                text = text
            )
        }
    }

    fun clearSlot(categoryId: Int, position: Int) {
        require(position in 0..4)
        viewModelScope.launch {
            templatesRepository.clearSlot(categoryId.toLong(), position)
        }
    }
}
