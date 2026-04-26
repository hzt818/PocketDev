package com.pocketdev.ui.screens.pc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.PcConnectionConfig
import com.pocketdev.domain.usecase.AddPcConnectionUseCase
import com.pocketdev.domain.usecase.GetPcConnectionsUseCase
import com.pocketdev.domain.usecase.RemovePcConnectionUseCase
import com.pocketdev.domain.usecase.SetActivePcConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PcConnectionUiState(
    val connections: List<PcConnectionConfig> = emptyList(),
    val activeConnection: PcConnectionConfig? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val testingConnection: String? = null
)

sealed interface PcConnectionEvent {
    data class AddConnection(
        val name: String,
        val host: String,
        val port: Int,
        val apiKey: String
    ) : PcConnectionEvent
    data class RemoveConnection(val id: String) : PcConnectionEvent
    data class SetActive(val id: String) : PcConnectionEvent
    data object ShowAddDialog : PcConnectionEvent
    data object HideAddDialog : PcConnectionEvent
    data object ClearError : PcConnectionEvent
}

@HiltViewModel
class PcConnectionViewModel @Inject constructor(
    private val getPcConnections: GetPcConnectionsUseCase,
    private val addPcConnection: AddPcConnectionUseCase,
    private val removePcConnection: RemovePcConnectionUseCase,
    private val setActivePcConnection: SetActivePcConnectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PcConnectionUiState())
    val uiState: StateFlow<PcConnectionUiState> = _uiState.asStateFlow()

    init {
        observeConnections()
    }

    private fun observeConnections() {
        viewModelScope.launch {
            getPcConnections.connectionsFlow.collect { connections ->
                _uiState.update { it.copy(connections = connections) }
            }
        }
        viewModelScope.launch {
            getPcConnections.activeConnectionFlow.collect { active ->
                _uiState.update { it.copy(activeConnection = active) }
            }
        }
    }

    fun onEvent(event: PcConnectionEvent) {
        when (event) {
            is PcConnectionEvent.AddConnection -> addConnection(
                event.name,
                event.host,
                event.port,
                event.apiKey
            )
            is PcConnectionEvent.RemoveConnection -> removeConnection(event.id)
            is PcConnectionEvent.SetActive -> setActive(event.id)
            is PcConnectionEvent.ShowAddDialog -> _uiState.update { it.copy(showAddDialog = true) }
            is PcConnectionEvent.HideAddDialog -> _uiState.update { it.copy(showAddDialog = false) }
            is PcConnectionEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun addConnection(name: String, host: String, port: Int, apiKey: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showAddDialog = false) }

            val config = PcConnectionConfig(
                id = UUID.randomUUID().toString(),
                name = name,
                host = host,
                port = port,
                apiKey = apiKey
            )

            addPcConnection(config).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun removeConnection(id: String) {
        viewModelScope.launch {
            removePcConnection(id).fold(
                onSuccess = { /* Connection list will update via flow */ },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }

    private fun setActive(id: String) {
        viewModelScope.launch {
            setActivePcConnection(id).fold(
                onSuccess = { /* Active connection will update via flow */ },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }
}
