package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.model.dto.CategoryWithSubcategoriesDto
import sfedu.ictis.walkOfInterest.data.model.dto.SubcategoryShortDto
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory

fun CategoryWithSubcategoriesDto.toDomainOrNull(): DomainPickCategory? {
    val name = categoryName?.takeIf { it.isNotBlank() } ?: return null
    val subs = subcategories.orEmpty().mapNotNull { it.toDomainOrNull(categoryId) }
    if (subs.isEmpty()) return null
    return DomainPickCategory(
        id = categoryId,
        name = name,
        icon = categoryIcon,
        subcategories = subs
    )
}

private fun SubcategoryShortDto.toDomainOrNull(categoryId: Int): DomainPickSubcategory? {
    val name = subcategoryName?.takeIf { it.isNotBlank() } ?: return null
    return DomainPickSubcategory(
        id = subcategoryId,
        name = name,
        categoryId = categoryId
    )
}
