package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.model.dto.CategoryWithSubcategoriesDto
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory

fun CategoryWithSubcategoriesDto.toDomainOrNull(): DomainPickCategory? {
    val name = categoryName?.takeIf { it.isNotBlank() } ?: return null
    return DomainPickCategory(
        id           = categoryId,
        name         = name,
        icon         = categoryIcon,
        subcategories = emptyList()
    )
}
