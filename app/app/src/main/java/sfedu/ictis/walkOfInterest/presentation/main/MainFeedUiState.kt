package sfedu.ictis.walkOfInterest.presentation.main


// TODO потом из Domain
sealed class FeedItem {
    data class Trip(val id: String, val title: String) : FeedItem()
    data class Spot(val id: String, val title: String) : FeedItem()
}
enum class MainTab {
    TRIPS, SPOTS, NONE
}

data class MainFeedUiState(
    val selectedTab: MainTab = MainTab.TRIPS,
    val items: List<FeedItem> = emptyList(),
    val isCreateMenuVisible: Boolean = false
)