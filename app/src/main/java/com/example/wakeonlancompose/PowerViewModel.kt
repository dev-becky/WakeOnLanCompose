package com.example.wakeonlancompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

sealed class WakeOnLanEvent {
    object Success : WakeOnLanEvent()
    data class Error(val message: String) : WakeOnLanEvent()
}

class PowerViewModel : ViewModel() {
    private val _events = MutableSharedFlow<WakeOnLanEvent>()
    val events: SharedFlow<WakeOnLanEvent> = _events

    private val ipAddress = "" // IP de broadcast
    private val macAddress = "" // MAC do computador

    fun sendWakeOnLanPacket() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val macBytes = macAddress.split(":").map { it.toInt(16).toByte() }.toByteArray()
                val packetData = ByteArray(102)

                for (i in 0 until 6) packetData[i] = 0xFF.toByte()
                for (i in 1..16) System.arraycopy(macBytes, 0, packetData, i * 6, macBytes.size)

                val broadcast = InetAddress.getByName(ipAddress)
                DatagramSocket().use { socket ->
                    socket.broadcast = true
                    socket.send(DatagramPacket(packetData, packetData.size, broadcast, 9))
                }

                _events.emit(WakeOnLanEvent.Success)
            } catch (e: Exception) {
                _events.emit(WakeOnLanEvent.Error("Erro ao ligar: ${e.message}"))
            }
        }
    }
}