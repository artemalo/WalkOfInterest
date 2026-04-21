package sfedu.ictis.walkOfInterest.presentation.main



sealed class FeedItem {
    data class Trip(
        val id: String,
        val title: String,
        val addressFrom: String,
        val addressTo: String,
        val totalTime: Int,
        val totalPois: Int
        ) : FeedItem()
    data class Spot(
        val id: String,
        val photo: String,
        val title: String,
        val address: String
    ) : FeedItem() // TODO как из Domain
}
enum class MainTab {
    TRIPS, SPOTS
}

data class MainFeedUiState(
    val selectedTab: MainTab = MainTab.TRIPS,
    val items: List<FeedItem> = emptyList(),
    val isCreateMenuVisible: Boolean = false
)