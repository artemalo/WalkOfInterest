package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

enum class FormLang { RU, EN }

data class AddPoiFormUiState(
    val point: DomainPoint? = null,
    val targetPoiId: Long? = null,

    val photoUrl: String? = null,
    val isUploadingPhoto: Boolean = false,

    val name: String = "",
    val description: String = "",
    val lang: FormLang = FormLang.RU,

    val isLoadingCategories: Boolean = false,
    val categories: List<DomainPickCategory> = emptyList(),
    val selectedSubcategoryIds: Set<Int> = emptySet(),
    val selectedSubcategories: List<DomainPickSubcategory> = emptyList(),
    val originalSubcategoryIds: Set<Int>? = null,

    val isSubmitting: Boolean = false,
    val errorBanner: String? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && selectedSubcategoryIds.isNotEmpty() && point != null
}
