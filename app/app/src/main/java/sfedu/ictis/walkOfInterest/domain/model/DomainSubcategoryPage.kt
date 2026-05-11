package sfedu.ictis.walkOfInterest.domain.model

data class DomainSubcategoryPage(
    val content: List<DomainPickSubcategory>,
    val page: Int,
    val totalPages: Int,
    val isLast: Boolean
)