package ru.tech.cookhelper.presentation.chat.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.tech.cookhelper.core.Action
import ru.tech.cookhelper.domain.model.Message
import ru.tech.cookhelper.domain.model.User
import ru.tech.cookhelper.domain.repository.MessageRepository
import ru.tech.cookhelper.domain.use_case.get_user.GetUserUseCase
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: MessageRepository,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    fun send(message: String) {
        repository.sendMessage(message)
    }

    private var user: User? = null

    val messages = mutableStateListOf<Message>()

    init {
        getUserUseCase().onEach {
            user = it
        }.launchIn(viewModelScope)

        repository.awaitNewMessages(chatId = "1", token = user?.token ?: "").onEach { action ->
            when (action) {
                is Action.Empty -> TODO()
                is Action.Error -> messages.add(Message(action.message ?: ""))
                is Action.Loading -> TODO()
                is Action.Success -> action.data?.let { messages.add(it) }
            }
        }.launchIn(viewModelScope)
    }

}