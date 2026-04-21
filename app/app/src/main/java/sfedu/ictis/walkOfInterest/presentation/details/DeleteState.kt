package sfedu.ictis.walkOfInterest.presentation.details

sealed class DeleteState {
    object Idle : DeleteState()
    object Success : DeleteState()
    object Error : DeleteState()
}